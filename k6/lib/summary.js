// Gera results/<RUN_TAG>.summary.json (dados crus p/ gráficos do TCC)
// e um resumo enxuto no stdout. Sem dependências remotas.
export function handleSummary(data) {
  const tag = __ENV.RUN_TAG || "run";
  const m = data.metrics || {};
  const val = (name, field) =>
    (m[name] && m[name].values && m[name].values[field] != null)
      ? m[name].values[field] : "-";

  const lines = [];
  lines.push(`=== ${tag} ===`);
  lines.push(`requests:            ${val("http_reqs", "count")}`);
  lines.push(`http_req_failed:     ${val("http_req_failed", "rate")}`);
  lines.push(`duration p95 (ms):   ${val("http_req_duration", "p(95)")}`);
  lines.push(`duration p99 (ms):   ${val("http_req_duration", "p(99)")}`);
  lines.push(`duration med (ms):   ${val("http_req_duration", "med")}`);

  // Counters/metrics custom dos cenários (orders_201, journey_completed, etc.)
  for (const k of Object.keys(m)) {
    if (k.startsWith("http_") || k === "vus" || k === "vus_max" || k === "iterations") continue;
    const t = m[k].type;
    if (t === "counter")      lines.push(`${k}: ${val(k, "count")}`);
    else if (t === "rate")    lines.push(`${k}: ${val(k, "rate")}`);
    else if (t === "trend")   lines.push(`${k} (p95): ${val(k, "p(95)")}`);
  }

  const text = lines.join("\n") + "\n";
  return {
    [`results/${tag}.summary.json`]: JSON.stringify(data, null, 2),  // anexo p/ gráficos
    stdout: text,                                                     // resumo no console
  };
}