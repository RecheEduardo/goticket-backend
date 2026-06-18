import http from "k6/http";
import { check, sleep } from "k6";
import { Trend, Counter, Rate } from "k6/metrics";
export { handleSummary } from "../lib/summary.js";
import { SharedArray } from "k6/data";
const clients = new SharedArray("clients", () => JSON.parse(open("../data/clients.json")));

// ── BREAKDOWN POR PASSO ──
const mLogin   = new Trend("step_login_ms", true);
const mEnqueue = new Trend("step_enqueue_ms", true);
const mWait    = new Trend("queue_wait_ms", true);    // tempo NA fila (informativo — alto é OK)
const mSelect  = new Trend("step_select_ms", true);   // GET evento (escolher data/setor)
const mOrder   = new Trend("step_order_ms", true);
const mConfirm = new Trend("step_confirm_ms", true);
const mTickets = new Trend("step_tickets_ms", true);
const mCheckout= new Trend("checkout_ms", true);      // SOMA server-side da zona protegida = SLA
const mJourney = new Trend("journey_ms", true);

// ── DESFECHOS / FALHAS POR PASSO ──
const cCompleted = new Counter("outcome_completed");
const cAbandoned = new Counter("outcome_abandoned_queue");  // desistiu na fila (esperado, não é erro)
const cFailLogin = new Counter("fail_login");
const cFailEnq   = new Counter("fail_enqueue");
const cFailOrder = new Counter("fail_order");
const cFailConf  = new Counter("fail_confirm");
const cFailTk    = new Counter("fail_tickets");
const rCheckoutOk= new Rate("checkout_success");       // sucesso DENTRO da zona protegida

const PEAK   = Number(__ENV.ARRIVAL_PEAK || 20);       // novos fãs/s no pico (modelo aberto)
const MAXVUS = Number(__ENV.MAXVUS || 1200);           // teto de jornadas em voo (cabe em 16GB)
const POLL_S = Number(__ENV.POLL_S || 8);              // intervalo de polling da fila
const MAX_WAIT_S = Number(__ENV.MAX_WAIT_S || 300);    // paciência: desiste após 5 min
// think-times (s), com jitter ±50%
const T_BROWSE = Number(__ENV.THINK_BROWSE || 2);
const T_SELECT = Number(__ENV.THINK_SELECT || 25);     // escolher DATA+SETOR (segura o slot → TTL 600)
const T_PAY    = Number(__ENV.THINK_PAY || 8);
const jit = (s) => s * (0.5 + Math.random());

export const options = {
  scenarios: { journey: { executor: "ramping-arrival-rate",
    startRate: 0, timeUnit: "1s", preAllocatedVUs: 800, maxVUs: MAXVUS,
    stages: [{ duration: "1m", target: PEAK }, { duration: "5m", target: PEAK }, { duration: "1m", target: 0 }] } },
  thresholds: {
    // A AFIRMAÇÃO DA TESE — zona protegida saudável sob a carga que derruba o C:
    "checkout_ms":                     ["p(95)<2000"],
    "checkout_success":                ["rate>0.99"],
    "http_req_failed{phase:checkout}": ["rate<0.01"],
    // queue_wait_ms e abandono NÃO são thresholds — são o trade-off esperado.
  },
};

const BASE = __ENV.BASE_URL || "http://localhost:8080";
const EID = Number(__ENV.EVENT_ID), EDID = Number(__ENV.EVENT_DATE_ID), ALLOT = Number(__ENV.ALLOTMENT_ID);

export default function () {
  const t0 = Date.now();
  const email = `loadtest+${((__VU - 1) % 2000) + 1}@goticket.dev`;   // 2000 clientes semeados

  const token = clients[(__VU - 1) % clients.length].token;
  const H = { "Content-Type": "application/json", "Authorization": `Bearer ${token}` };
  let t;

  // 2) ENTRA NA FILA
  t = Date.now();
  const q = http.post(`${BASE}/events/${EID}/queue`, null, { headers: H, tags: { phase: "enqueue" } });
  mEnqueue.add(Date.now() - t);
  if (q.status !== 200) { cFailEnq.add(1); return; }
  let state = q.json("state"), qToken = q.json("admissionToken");

  // 3) ESPERA NA FILA (poll) — abandono por paciência limitada
  const w = Date.now();
  while (state === "WAITING") {
    if ((Date.now() - w) / 1000 >= MAX_WAIT_S) { mWait.add(Date.now() - w); cAbandoned.add(1); return; }
    sleep(POLL_S);
    const p = http.get(`${BASE}/events/${EID}/queue/position`, { headers: H, tags: { phase: "poll" } });
    state = p.json("state"); qToken = p.json("admissionToken");
  }
  mWait.add(Date.now() - w);
  if (state !== "ADMITTED") { cAbandoned.add(1); return; }

  // ═══ ZONA PROTEGIDA (já passou pela fila; slot segurado até o passo 4) ═══
  let ck = 0;
  // 3.5) ESCOLHE DATA + SETOR (carrega a página do evento + think longo → justifica TTL 600)
  t = Date.now();
  http.get(`${BASE}/events/${EID}`, { headers: H, tags: { phase: "select" } });
  mSelect.add(Date.now() - t);
  sleep(jit(T_SELECT));

  // 4) RESERVA (token de fila + idempotência) — aqui o slot é liberado no servidor
  const idem = `${email}-${__VU}-${__ITER}-${t0}`;
  t = Date.now();
  const o = http.post(`${BASE}/orders`, JSON.stringify({ eventDateId: EDID,
      items: [{ batchAllotmentId: ALLOT, ticketTypeId: 1, holderName: "LT", holderDocument: "00000000000" }] }),
    { headers: { ...H, "Idempotency-Key": idem, "X-Queue-Token": qToken }, tags: { phase: "checkout" } });
  const dO = Date.now() - t; mOrder.add(dO); ck += dO;
  if (o.status !== 201) { cFailOrder.add(1); rCheckoutOk.add(false); return; }
  const orderId = o.json("orderId");
  sleep(jit(T_PAY));                                  // digita o cartão (slot já liberado)

  // 5) CONFIRMA (seam de carga = webhook Stripe)
  t = Date.now();
  const c = http.post(`${BASE}/loadtest/confirm/${orderId}`, null, { headers: H, tags: { phase: "checkout" } });
  const dC = Date.now() - t; mConfirm.add(dC); ck += dC;
  if (c.status !== 204) { cFailConf.add(1); rCheckoutOk.add(false); return; }

  // 6) TICKET
  t = Date.now();
  const tk = http.get(`${BASE}/orders/${orderId}/tickets`, { headers: H, tags: { phase: "checkout" } });
  const dT = Date.now() - t; mTickets.add(dT); ck += dT;
  if (!check(tk, { "ticket gerado": (r) => r.status === 200 && r.json().length > 0 })) {
    cFailTk.add(1); rCheckoutOk.add(false); return;
  }
  mCheckout.add(ck); rCheckoutOk.add(true); cCompleted.add(1); mJourney.add(Date.now() - t0);
}