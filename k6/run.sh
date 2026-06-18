#!/usr/bin/env bash
# Orquestrador de carga (Git Bash). Uso: ./run.sh setup|A|B|C|E|E-naive [carga]
# Ex.: ./run.sh A 3000   |   ./run.sh C 1500   |   ./run.sh E 35
# Rode de dentro da pasta k6/.

BASE_URL="http://localhost:8080"
COMPOSE_FILES="-f ../docker-compose.yml -f ../docker-compose.loadtest.yml"
mkdir -p results data

psql_q() {
  docker compose $COMPOSE_FILES exec -T postgres psql -U goticket -d GoTicketDB -tA -c "$1"
}

wait_health() {
  echo "Aguardando app..."
  for i in $(seq 1 60); do
    code=$(curl -s -o /dev/null -w '%{http_code}' "$BASE_URL/events" 2>/dev/null || true)
    if [ "$code" = "200" ]; then echo "App no ar (GET /events 200)."; return 0; fi
    sleep 2
  done
  echo "App nao respondeu a tempo." >&2; exit 1
}

gen_clients() {
  local n="${1:-2000}"
  echo "Gerando $n tokens..."
  echo "[" > data/clients.json
  for i in $(seq 1 "$n"); do
    tok=$(curl -s -X POST "$BASE_URL/login" -H "Content-Type: application/json" \
      -d "{\"email\":\"loadtest+$i@goticket.dev\",\"password\":\"LoadTest123!\"}" \
      | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
    local sep=","; [ "$i" -eq "$n" ] && sep=""
    echo "  {\"token\":\"$tok\"}$sep" >> data/clients.json
  done
  echo "]" >> data/clients.json
  echo "✓ tokens em data/clients.json"
}

discover_ids() {
  local j="JOIN tb_ticket_batches tb ON tb.batch_id=ba.batch_id JOIN tb_event_date_sectors eds ON eds.event_date_sector_id=tb.event_date_sector_id JOIN tb_event_sectors es ON es.sector_id=eds.event_sector_id"
  local sql="SELECT e.event_id, ed.event_date_id, \
(SELECT ba.allotment_id FROM tb_batch_allotments ba $j WHERE es.event_id=e.event_id AND es.name='Pista Premium' AND ba.ticket_type_id=1 LIMIT 1), \
(SELECT ba.allotment_id FROM tb_batch_allotments ba $j WHERE es.event_id=e.event_id AND es.name='Pista' AND ba.ticket_type_id=1 LIMIT 1) \
FROM tb_events e JOIN tb_event_dates ed ON ed.event_id=e.event_id \
WHERE e.title='Allianz Live Experience [LT]' ORDER BY ed.event_date_id LIMIT 1;"
  local row; row=$(psql_q "$sql")
  IFS='|' read -r EVENT_ID EVENT_DATE_ID ALLOT_SMALL ALLOT_BULK <<< "$row"
  cat > ids.env <<EOF
EVENT_ID=$EVENT_ID
EVENT_DATE_ID=$EVENT_DATE_ID
ALLOT_SMALL=$ALLOT_SMALL
ALLOT_BULK=$ALLOT_BULK
EOF
  echo "IDs: $(tr '\n' ' ' < ids.env)"
}

set_tier() {
  local eid="$1" tier="$2"
  psql_q "INSERT INTO tb_event_demand_profiles(event_id,tier,source,last_evaluated_at,manual_override_until)
          VALUES($eid,'$tier','MANUAL',NOW(),NOW()+INTERVAL '1 day')
          ON CONFLICT(event_id) DO UPDATE SET tier='$tier',source='MANUAL',
            manual_override_until=NOW()+INTERVAL '1 day',last_evaluated_at=NOW();" >/dev/null
  if [ "$tier" = "HIGH" ]; then
    docker compose $COMPOSE_FILES exec -T redis redis-cli SET "demand:event:$eid:tier" HIGH >/dev/null
  else
    docker compose $COMPOSE_FILES exec -T redis redis-cli DEL "demand:event:$eid:tier" >/dev/null
  fi
  echo "Tier evento $eid => $tier (MANUAL pin)"
}

# reset único e DE FATO chamado (antes: definido em cada case e nunca invocado).
# Roda ANTES do set_tier — senão o DELETE de demand_profiles + FLUSHALL apagariam o tier.
reset_state() {
  psql_q "TRUNCATE tb_tickets, tb_order_items, tb_orders, tb_payment_events, tb_idempotency_keys RESTART IDENTITY CASCADE;" >/dev/null
  psql_q "UPDATE tb_batch_allotments SET sold_tickets=0, reserved_tickets=0;" >/dev/null
  psql_q "DELETE FROM tb_event_demand_profiles;" >/dev/null
  docker compose $COMPOSE_FILES exec -T redis redis-cli FLUSHALL >/dev/null
  echo "Estado resetado (orders/inventory/demanda/redis)."
}

verify_results() {
  local tag="$1" out="results/$1.verify.md"
  local oversold maxexc dup orders confirmed orphans
  oversold=$(psql_q "SELECT COUNT(*) FROM tb_batch_allotments WHERE sold_tickets+reserved_tickets>quota;")
  maxexc=$(psql_q  "SELECT COALESCE(MAX(sold_tickets+reserved_tickets-quota),0) FROM tb_batch_allotments;")
  dup=$(psql_q     "SELECT COUNT(*) FROM (SELECT idempotency_key FROM tb_orders GROUP BY idempotency_key HAVING COUNT(*)>1) d;")
  orders=$(psql_q  "SELECT COUNT(*) FROM tb_orders;")
  orphans=$(psql_q "SELECT COUNT(*) FROM tb_orders o JOIN tb_order_status s ON s.status_id=o.status_id WHERE s.name='PENDING_PAYMENT';")
  {
    echo "# Verificacao pos-execucao - $tag"
    echo
    echo "- Overselling (sold+reserved > quota): $oversold  -> esperado 0"
    echo "- Maior excesso sobre a cota: $maxexc  -> esperado <= 0"
    echo "- Idempotency-keys com >1 Order: $dup  -> esperado 0"
    echo "- Total de Orders: $orders"
    echo "- Orders orfaos (PENDING_PAYMENT = criados mas nao confirmados): $orphans"
  } | tee "$out"
}

case "$1" in
  setup)
    ( cd .. && ./mvnw clean package -DskipTests -q \
      && docker compose -f docker-compose.yml -f docker-compose.loadtest.yml down -v \
      && docker compose -f docker-compose.yml -f docker-compose.loadtest.yml up -d --build )
    wait_health
    gen_clients "${2:-2000}"
    discover_ids
    echo "✓ setup completo. Agora: ./run.sh A|B|C|E|E-naive [carga]"
    ;;

  A)  # overselling — NORMAL, allotment PEQUENO
    source ids.env
    reset_state
    set_tier "$EVENT_ID" NORMAL
    RUN_TAG=A-overselling-protected BASE_URL=$BASE_URL \
      EVENT_DATE_ID=$EVENT_DATE_ID ALLOTMENT_ID=$ALLOT_SMALL VUS="${2:-3000}" \
      k6 run scenarios/A-overselling.js
    verify_results A-overselling-protected
    ;;

  B)  # cobrança/duplicação — NORMAL, allotment GRANDE
    source ids.env
    reset_state
    set_tier "$EVENT_ID" NORMAL
    RUN_TAG=B-payment-protected BASE_URL=$BASE_URL \
      EVENT_DATE_ID=$EVENT_DATE_ID ALLOTMENT_ID=$ALLOT_BULK KEY_POOL=200 VUS="${2:-700}" \
      k6 run scenarios/B-payment.js
    verify_results B-payment-protected
    ;;

  C)  # disponibilidade SEM fila (baseline de colapso) — NORMAL, allotment GRANDE
    source ids.env
    reset_state
    set_tier "$EVENT_ID" NORMAL
    RUN_TAG=C-availability-noqueue BASE_URL=$BASE_URL \
      EVENT_DATE_ID=$EVENT_DATE_ID ALLOTMENT_ID=$ALLOT_BULK ARRIVAL_PEAK="${2:-1200}" MAXVUS=3000 \
      k6 run scenarios/C-availability.js
    verify_results C-availability-noqueue
    ;;

  E)  # jornada protegida — HIGH (passa pela fila)
    source ids.env
    reset_state
    set_tier "$EVENT_ID" HIGH
    RUN_TAG=E-journey-protected BASE_URL=$BASE_URL \
      EVENT_ID=$EVENT_ID EVENT_DATE_ID=$EVENT_DATE_ID ALLOTMENT_ID=$ALLOT_BULK \
      ARRIVAL_PEAK="${2:-30}" POLL_S=15 MAXVUS=1500 \
      k6 run scenarios/E-journey-e2e.js
    verify_results E-journey-protected
    ;;

  E-naive)  # MESMO script, fila DESLIGADA (tier NORMAL) — contraparte de colapso da jornada
    source ids.env
    reset_state
    set_tier "$EVENT_ID" NORMAL
    RUN_TAG=E-journey-naive BASE_URL=$BASE_URL \
      EVENT_ID=$EVENT_ID EVENT_DATE_ID=$EVENT_DATE_ID ALLOTMENT_ID=$ALLOT_BULK \
      ARRIVAL_PEAK="${2:-30}" POLL_S=15 MAXVUS=1500 \
      k6 run scenarios/E-journey-e2e.js
    verify_results E-journey-naive
    ;;

  *) echo "uso: ./run.sh setup|A|B|C|E|E-naive [carga]" ;;
esac