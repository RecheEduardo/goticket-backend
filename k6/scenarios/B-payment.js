import http from "k6/http";
import { Counter } from "k6/metrics";
import { SharedArray } from "k6/data";
export { handleSummary } from "../lib/summary.js";

const c201 = new Counter("orders_201");     // criada OU replay idempotente (ambos 201)
const c409 = new Counter("orders_409");     // conflito esperado: corrida na mesma key / in-flight
const c401 = new Counter("orders_401");     // token expirado/ausente (não deveria ocorrer)
const c5xx = new Counter("orders_5xx");     // erro do servidor (alvo do hardening: ~0)
const cTo  = new Counter("orders_timeout"); // timeout (status 0)
const cOther = new Counter("orders_other");

// Pool de pares (chave, token): a MESMA chave vem SEMPRE do MESMO cliente.
const KEY_POOL = Number(__ENV.KEY_POOL || 200);
const VUS = Number(__ENV.VUS || 700);       // 4 CPU: mais VUs por chave => corrida mais densa
const pairs = new SharedArray("pairs", () => {
  const clients = JSON.parse(open("../data/clients.json"));
  const out = [];
  for (let i = 0; i < KEY_POOL; i++) {
    out.push({ key: `lt-key-${i}`, token: clients[i % clients.length].token });
  }
  return out;
});

export const options = {
  scenarios: { storm: { executor: "ramping-vus", startVUs: 0,
    stages: [{ duration: "20s", target: Math.floor(VUS / 2) },
             { duration: "1m", target: VUS },
             { duration: "20s", target: 0 }] } },
};
const BASE = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const p = pairs[Math.floor(Math.random() * pairs.length)];
  const r = http.post(`${BASE}/orders`, JSON.stringify({
      eventDateId: Number(__ENV.EVENT_DATE_ID),
      items: [{ batchAllotmentId: Number(__ENV.ALLOTMENT_ID), ticketTypeId: 1,
                holderName: "LT", holderDocument: "00000000000" }] }),
    { headers: { "Content-Type": "application/json",
                 "Idempotency-Key": p.key, "Authorization": `Bearer ${p.token}` } });
  if (r.status === 201) c201.add(1);
  else if (r.status === 409) c409.add(1);
  else if (r.status === 401) c401.add(1);
  else if (r.status === 0) cTo.add(1);
  else if (r.status >= 500) c5xx.add(1);
  else cOther.add(1);
}