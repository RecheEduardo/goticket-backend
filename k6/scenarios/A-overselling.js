import http from "k6/http";
import { Counter } from "k6/metrics";
import { SharedArray } from "k6/data";
export { handleSummary } from "../lib/summary.js";

const c201 = new Counter("orders_201");      // venda confirmada (teto = cota do allotment)
const c409 = new Counter("orders_409");      // rejeição esperada: SoldOut / contention / conflito (tudo 409)
const c5xx = new Counter("orders_5xx");      // erro de servidor (alvo: ~0)
const cTo  = new Counter("orders_timeout");  // timeout do cliente (status 0)
const cOther = new Counter("orders_other");

const clients = new SharedArray("c", () => JSON.parse(open("../data/clients.json")));
const VUS = Number(__ENV.VUS || 3000);       // 4 CPU: mais tentativas concorrentes na MESMA cota

export const options = {
  scenarios: { stampede: { executor: "ramping-vus", startVUs: 0,
    stages: [{ duration: "1m", target: Math.floor(VUS / 2) },
             { duration: "2m", target: VUS },
             { duration: "30s", target: 0 }] } },
};
const BASE = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const t = clients[Math.floor(Math.random() * clients.length)].token;
  const r = http.post(`${BASE}/orders`, JSON.stringify({
      eventDateId: Number(__ENV.EVENT_DATE_ID),
      items: [{ batchAllotmentId: Number(__ENV.ALLOTMENT_ID), ticketTypeId: 1,
                holderName: "LT", holderDocument: "00000000000" }] }),
    { headers: { "Content-Type": "application/json",
                 "Idempotency-Key": `${__VU}-${__ITER}-${Date.now()}`,
                 "Authorization": `Bearer ${t}` } });
  if (r.status === 201) c201.add(1);
  else if (r.status === 409) c409.add(1);
  else if (r.status === 0) cTo.add(1);
  else if (r.status >= 500) c5xx.add(1);
  else cOther.add(1);
}