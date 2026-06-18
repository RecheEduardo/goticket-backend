# Verificacao pos-execucao - A-overselling-protected

- Overselling (sold+reserved > quota): 0  -> esperado 0
- Maior excesso sobre a cota: 0  -> esperado <= 0
- Idempotency-keys com >1 Order: 0  -> esperado 0
- Total de Orders: 100
- Orders orfaos (PENDING_PAYMENT = criados mas nao confirmados): 
