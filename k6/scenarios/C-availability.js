// scenarios/C-availability.js  — baseline SEM fila (tier NORMAL): demonstra o colapso
import http from "k6/http";
import { Counter } from "k6/metrics";
import { SharedArray } from "k6/data";
export { handleSummary } from "../lib/summary.js";

const c201 = new Counter("orders_201");      // pedidos confirmados ao cliente
const c409 = new Counter("orders_409");      // conflito/soldout/contention
const c5xx = new Counter("orders_5xx");      // erro de servidor sob saturação
const c401 = new Counter("orders_401");
const c403 = new Counter("orders_403");
const cTo  = new Counter("orders_timeout");  // timeout (status 0) -> origem das "falhas ambíguas"
const cOther = new Counter("orders_other");

const clients = new SharedArray("c", () => JSON.parse(open("../data/clients.json")));
const PEAK   = Number(__ENV.ARRIVAL_PEAK || 1200);  // 4 CPU: pico ~2-3x acima do novo teto
const MAXVUS = Number(__ENV.MAXVUS || 3000);

export const options = {
  scenarios: { ramp: {
    executor: "ramping-arrival-rate",          // modelo ABERTO
    startRate: 50, timeUnit: "1s",
    preAllocatedVUs: 1000, maxVUs: MAXVUS,
    stages: [
      { duration: "1m", target: 300 },
      { duration: "2m", target: 700 },         // provável joelho
      { duration: "2m", target: PEAK },        // saturação
      { duration: "30s", target: 0 },
    ],
  }},
  thresholds: {                                 // ESPERA-SE que reprovem — é a prova do colapso
    http_req_duration: ["p(95)<800", "p(99)<2000"],
    http_req_failed: ["rate<0.05"],
  },
};
const BASE = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const t = clients[Math.floor(Math.random() * clients.length)].token;
  const r = http.post(`${BASE}/orders`, JSON.stringify({ eventDateId: Number(__ENV.EVENT_DATE_ID),
      items: [{ batchAllotmentId: Number(__ENV.ALLOTMENT_ID), ticketTypeId: 1, holderName: "LT", holderDocument: "00000000000" }] }),
    { headers: { "Content-Type": "application/json",
                 "Idempotency-Key": `${__VU}-${__ITER}-${Date.now()}`, "Authorization": `Bearer ${t}` } });
  if (r.status === 201) c201.add(1);
  else if (r.status === 409) c409.add(1);
  else if (r.status === 0) cTo.add(1);
  else if (r.status >= 500) c5xx.add(1);
  else if (r.status === 401) c401.add(1);
  else cOther.add(1);
}