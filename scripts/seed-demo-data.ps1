$ErrorActionPreference = 'Stop'
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$sqlFile = Join-Path $scriptDir 'seed-demo-data.sql'

if (-not (Test-Path $sqlFile)) {
  throw "Arquivo SQL nao encontrado: $sqlFile"
}

Write-Host "Aplicando seed em obrigacoes-mysql..."
Get-Content -Raw -Path $sqlFile -Encoding UTF8 |
  docker exec -i obrigacoes-mysql mysql -uobrigacoes -pobrigacoes obrigacoes

if ($LASTEXITCODE -ne 0) {
  throw "Falha ao executar seed (exit $LASTEXITCODE)"
}

Write-Host "Seed concluido."
