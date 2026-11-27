$ErrorActionPreference = 'Stop'
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$sqlFile = Join-Path $scriptDir 'seed-demo-data.sql'

if (-not (Test-Path $sqlFile)) {
  throw "Arquivo SQL nao encontrado: $sqlFile"
}

Write-Host "Aplicando seed em obrigacoes-mysql (utf8mb4)..."
# Copy the file into the container — piping via PowerShell on Windows
# mangles UTF-8 (accents become '?'). docker cp keeps bytes intact.
$remoteSql = '/tmp/seed-demo-data.sql'
docker cp $sqlFile "obrigacoes-mysql:$remoteSql"
if ($LASTEXITCODE -ne 0) {
  throw "Falha ao copiar SQL para o container (exit $LASTEXITCODE)"
}

docker exec obrigacoes-mysql mysql -uobrigacoes -pobrigacoes --default-character-set=utf8mb4 obrigacoes -e "SOURCE $remoteSql"
if ($LASTEXITCODE -ne 0) {
  throw "Falha ao executar seed (exit $LASTEXITCODE)"
}

Write-Host "Seed concluido."
