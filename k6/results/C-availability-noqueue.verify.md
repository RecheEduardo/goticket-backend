# Verificacao pos-execucao - C-availability-noqueue

- Overselling (sold+reserved > quota): 0  -> esperado 0
- Maior excesso sobre a cota: 0  -> esperado <= 0
- Idempotency-keys com >1 Order: 0  -> esperado 0
- Total de Orders: 6000
- Orders orfaos (PENDING_PAYMENT = criados mas nao confirmados): 6000
