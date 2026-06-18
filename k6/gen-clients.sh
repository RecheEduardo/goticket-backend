#!/bin/bash
# Gera data/clients.json com {token} de N clientes seed.
N="${1:-2000}"
OUT="data/clients.json"; echo "[" > "$OUT"
for i in $(seq 1 "$N"); do
  EMAIL="loadtest+${i}@goticket.dev"; PASS="LoadTest123!"
  TOK=$(curl -s -X POST http://localhost:8080/login \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"${EMAIL}\",\"password\":\"${PASS}\"}" | grep -oP '"accessToken":"\K[^"]+')
  SEP=$([ "$i" -lt "$N" ] && echo "," || echo "")
  echo "  {\"token\":\"${TOK}\"}${SEP}" >> "$OUT"
done
echo "]" >> "$OUT"
echo "✓ ${N} tokens em ${OUT}"