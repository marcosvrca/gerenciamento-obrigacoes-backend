#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_FILE="${SCRIPT_DIR}/seed-demo-data.sql"
REMOTE_SQL="/tmp/seed-demo-data.sql"

if [[ ! -f "$SQL_FILE" ]]; then
  echo "Arquivo SQL nao encontrado: $SQL_FILE" >&2
  exit 1
fi

echo "Aplicando seed em obrigacoes-mysql (utf8mb4)..."
# docker cp preserves UTF-8 bytes (pipe/redirect on some shells can corrupt accents).
docker cp "$SQL_FILE" "obrigacoes-mysql:${REMOTE_SQL}"
docker exec obrigacoes-mysql mysql -uobrigacoes -pobrigacoes \
  --default-character-set=utf8mb4 obrigacoes -e "SOURCE ${REMOTE_SQL}"

echo "Seed concluido."
