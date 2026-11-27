-- Demo seed: ~10 usuarios, ~30 empresas, ~60 obrigacoes
-- Idempotente para reexecução local (limpa dados de demo pelo padrão de e-mail/CNPJ).

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM anexos
WHERE obrigacao_id IN (
  SELECT id FROM (
    SELECT o.id
    FROM obrigacoes o
    INNER JOIN empresas e ON e.id = o.empresa_id
    WHERE e.cnpj LIKE '99%'
  ) t
);

DELETE FROM obrigacoes
WHERE empresa_id IN (SELECT id FROM empresas WHERE cnpj LIKE '99%')
   OR usuario_id IN (SELECT id FROM usuarios WHERE email LIKE '%@demo.obrigacoes.local');

DELETE FROM empresas WHERE cnpj LIKE '99%';
DELETE FROM usuarios WHERE email LIKE '%@demo.obrigacoes.local';

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO usuarios (nome, email, situacao) VALUES
('Ana Souza', 'ana.souza@demo.obrigacoes.local', 'ATIVO'),
('Bruno Lima', 'bruno.lima@demo.obrigacoes.local', 'ATIVO'),
('Carla Mendes', 'carla.mendes@demo.obrigacoes.local', 'ATIVO'),
('Diego Rocha', 'diego.rocha@demo.obrigacoes.local', 'ATIVO'),
('Elena Prado', 'elena.prado@demo.obrigacoes.local', 'ATIVO'),
('Fábio Nunes', 'fabio.nunes@demo.obrigacoes.local', 'ATIVO'),
('Gisele Torres', 'gisele.torres@demo.obrigacoes.local', 'ATIVO'),
('Hugo Martins', 'hugo.martins@demo.obrigacoes.local', 'ATIVO'),
('Iris Castro', 'iris.castro@demo.obrigacoes.local', 'INATIVO'),
('João Pires', 'joao.pires@demo.obrigacoes.local', 'INATIVO');

INSERT INTO empresas (razao_social, cnpj, email, situacao) VALUES
('Alpha Contábil Ltda', '99000001000101', 'contato@alpha-demo.local', 'ATIVO'),
('Beta Serviços S.A.', '99000002000102', 'contato@beta-demo.local', 'ATIVO'),
('Gama Fiscal ME', '99000003000103', 'contato@gama-demo.local', 'ATIVO'),
('Delta Auditoria Ltda', '99000004000104', 'contato@delta-demo.local', 'ATIVO'),
('Épsilon Tech Contábil', '99000005000105', 'contato@epsilon-demo.local', 'ATIVO'),
('Zeta Tributos Ltda', '99000006000106', 'contato@zeta-demo.local', 'ATIVO'),
('Eta Compliance S.A.', '99000007000107', 'contato@eta-demo.local', 'ATIVO'),
('Theta Folha Ltda', '99000008000108', 'contato@theta-demo.local', 'ATIVO'),
('Iota Consultoria ME', '99000009000109', 'contato@iota-demo.local', 'ATIVO'),
('Kappa Assessoria Ltda', '99000010000110', 'contato@kappa-demo.local', 'ATIVO'),
('Lambda Legal Contábil', '99000011000111', 'contato@lambda-demo.local', 'ATIVO'),
('Mu Impostos S.A.', '99000012000112', 'contato@mu-demo.local', 'ATIVO'),
('Nu Finanças Ltda', '99000013000113', 'contato@nu-demo.local', 'ATIVO'),
('Xi Planejamento ME', '99000014000114', 'contato@xi-demo.local', 'ATIVO'),
('Omicron RH Contábil', '99000015000115', 'contato@omicron-demo.local', 'ATIVO'),
('Pi Controladoria Ltda', '99000016000116', 'contato@pi-demo.local', 'ATIVO'),
('Rho Escritório S.A.', '99000017000117', 'contato@rho-demo.local', 'ATIVO'),
('Sigma BPO Ltda', '99000018000118', 'contato@sigma-demo.local', 'ATIVO'),
('Tau Obrigações ME', '99000019000119', 'contato@tau-demo.local', 'ATIVO'),
('Upsilon Parceiros Ltda', '99000020000120', 'contato@upsilon-demo.local', 'ATIVO'),
('Phi Clientes S.A.', '99000021000121', 'contato@phi-demo.local', 'ATIVO'),
('Chi Soluções Ltda', '99000022000122', 'contato@chi-demo.local', 'ATIVO'),
('Psi Contadores ME', '99000023000123', 'contato@psi-demo.local', 'ATIVO'),
('Ômega Holding Ltda', '99000024000124', 'contato@omega-demo.local', 'ATIVO'),
('Norte Fiscal Inativa', '99000025000125', 'contato@norte-demo.local', 'INATIVO'),
('Sul Tributos Inativa', '99000026000126', 'contato@sul-demo.local', 'INATIVO'),
('Leste Folha Inativa', '99000027000127', 'contato@leste-demo.local', 'INATIVO'),
('Oeste BPO Inativa', '99000028000128', 'contato@oeste-demo.local', 'INATIVO'),
('Centro Audit Inativa', '99000029000129', 'contato@centro-demo.local', 'INATIVO'),
('Planalto Contábil Inativa', '99000030000130', 'contato@planalto-demo.local', 'INATIVO');

-- 60 obrigações: 24 PENDENTE, 20 EM_ANDAMENTO, 16 CONCLUIDA
-- Vinculadas apenas a empresas/usuários ATIVOS (ids resolvidos por e-mail/CNPJ).
INSERT INTO obrigacoes (titulo, descricao, prazo, status, empresa_id, usuario_id)
SELECT
  titulo,
  descricao,
  prazo,
  status,
  (SELECT id FROM empresas WHERE cnpj = empresa_cnpj),
  (SELECT id FROM usuarios WHERE email = usuario_email)
FROM (
  SELECT 'Entrega DCTF' AS titulo, 'Preparar e transmitir DCTF mensal.' AS descricao, DATE_ADD(CURDATE(), INTERVAL 5 DAY) AS prazo, 'PENDENTE' AS status, '99000001000101' AS empresa_cnpj, 'ana.souza@demo.obrigacoes.local' AS usuario_email
  UNION ALL SELECT 'Apuração PIS/COFINS', 'Conferir créditos e débitos do período.', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'PENDENTE', '99000002000102', 'bruno.lima@demo.obrigacoes.local'
  UNION ALL SELECT 'Envio EFD-Contribuições', 'Validar bloco e transmitir SPED.', DATE_ADD(CURDATE(), INTERVAL 9 DAY), 'PENDENTE', '99000003000103', 'carla.mendes@demo.obrigacoes.local'
  UNION ALL SELECT 'Guia DAS Simples', 'Gerar DAS e enviar ao cliente.', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'PENDENTE', '99000004000104', 'diego.rocha@demo.obrigacoes.local'
  UNION ALL SELECT 'Folha mensal', 'Fechar folha e encargos.', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'PENDENTE', '99000005000105', 'elena.prado@demo.obrigacoes.local'
  UNION ALL SELECT 'eSocial eventos periódicos', 'Transmitir S-1200 e S-1210.', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'PENDENTE', '99000006000106', 'fabio.nunes@demo.obrigacoes.local'
  UNION ALL SELECT 'DIRF consolidação', 'Revisar informes de rendimentos.', DATE_ADD(CURDATE(), INTERVAL 12 DAY), 'PENDENTE', '99000007000107', 'gisele.torres@demo.obrigacoes.local'
  UNION ALL SELECT 'IRPJ/CSLL estimativa', 'Calcular antecipação trimestral.', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'PENDENTE', '99000008000108', 'hugo.martins@demo.obrigacoes.local'
  UNION ALL SELECT 'Conferência NF-e', 'Cruzar XML com livros fiscais.', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'PENDENTE', '99000009000109', 'ana.souza@demo.obrigacoes.local'
  UNION ALL SELECT 'Parcelamento PGFN', 'Atualizar demonstrativo e boletos.', DATE_ADD(CURDATE(), INTERVAL 8 DAY), 'PENDENTE', '99000010000110', 'bruno.lima@demo.obrigacoes.local'
  UNION ALL SELECT 'ECD escrituração', 'Organizar balancetes para ECD.', DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'PENDENTE', '99000011000111', 'carla.mendes@demo.obrigacoes.local'
  UNION ALL SELECT 'ECF parâmetros', 'Revisar parte A e B da ECF.', DATE_ADD(CURDATE(), INTERVAL 18 DAY), 'PENDENTE', '99000012000112', 'diego.rocha@demo.obrigacoes.local'
  UNION ALL SELECT 'Retenção INSS NF', 'Validar retenções de prestadores.', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'PENDENTE', '99000013000113', 'elena.prado@demo.obrigacoes.local'
  UNION ALL SELECT 'ISS apuração', 'Conferir alíquotas por município.', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'PENDENTE', '99000014000114', 'fabio.nunes@demo.obrigacoes.local'
  UNION ALL SELECT 'Relatório gerencial', 'Enviar DRE resumida ao cliente.', DATE_ADD(CURDATE(), INTERVAL 11 DAY), 'PENDENTE', '99000015000115', 'gisele.torres@demo.obrigacoes.local'
  UNION ALL SELECT 'Atualização cadastral', 'Sincronizar dados na prefeitura.', DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'PENDENTE', '99000016000116', 'hugo.martins@demo.obrigacoes.local'
  UNION ALL SELECT 'Conferência banco', 'Conciliar extrato x razão.', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PENDENTE', '99000017000117', 'ana.souza@demo.obrigacoes.local'
  UNION ALL SELECT 'Provisão férias', 'Calcular provisões do mês.', DATE_ADD(CURDATE(), INTERVAL 9 DAY), 'PENDENTE', '99000018000118', 'bruno.lima@demo.obrigacoes.local'
  UNION ALL SELECT 'FGTS recolhimento', 'Gerar guia e validar SEFIP/eSocial.', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'PENDENTE', '99000019000119', 'carla.mendes@demo.obrigacoes.local'
  UNION ALL SELECT 'Malha fiscal revisão', 'Responder pendências da SEFAZ.', DATE_ADD(CURDATE(), INTERVAL 13 DAY), 'PENDENTE', '99000020000120', 'diego.rocha@demo.obrigacoes.local'
  UNION ALL SELECT 'Certidão negativa', 'Emitir CND federal e estadual.', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'PENDENTE', '99000021000121', 'elena.prado@demo.obrigacoes.local'
  UNION ALL SELECT 'Inventário estoque', 'Conciliar inventário contábil.', DATE_ADD(CURDATE(), INTERVAL 16 DAY), 'PENDENTE', '99000022000122', 'fabio.nunes@demo.obrigacoes.local'
  UNION ALL SELECT 'Revisão contratos', 'Checar retenções contratuais.', DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'PENDENTE', '99000023000123', 'gisele.torres@demo.obrigacoes.local'
  UNION ALL SELECT 'Checklist fechamento', 'Rodar checklist mensal do cliente.', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'PENDENTE', '99000024000124', 'hugo.martins@demo.obrigacoes.local'

  UNION ALL SELECT 'SPED Fiscal em andamento', 'Ajustando registros C170.', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'EM_ANDAMENTO', '99000001000101', 'bruno.lima@demo.obrigacoes.local'
  UNION ALL SELECT 'DCTFWeb transmissão', 'Arquivo gerado, pendente recibo.', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'EM_ANDAMENTO', '99000002000102', 'carla.mendes@demo.obrigacoes.local'
  UNION ALL SELECT 'Admissão eSocial', 'Eventos S-2200 em validação.', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'EM_ANDAMENTO', '99000003000103', 'diego.rocha@demo.obrigacoes.local'
  UNION ALL SELECT 'Reforma tributária estudo', 'Simulando impactos de IBS/CBS.', DATE_ADD(CURDATE(), INTERVAL 25 DAY), 'EM_ANDAMENTO', '99000004000104', 'elena.prado@demo.obrigacoes.local'
  UNION ALL SELECT 'Balanço intermediário', 'Ajustes de reclassificação.', DATE_ADD(CURDATE(), INTERVAL 8 DAY), 'EM_ANDAMENTO', '99000005000105', 'fabio.nunes@demo.obrigacoes.local'
  UNION ALL SELECT 'Auditoria interna suporte', 'Enviando papéis de trabalho.', DATE_ADD(CURDATE(), INTERVAL 12 DAY), 'EM_ANDAMENTO', '99000006000106', 'gisele.torres@demo.obrigacoes.local'
  UNION ALL SELECT 'Parcelamento REFIS', 'Conferência de saldos.', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'EM_ANDAMENTO', '99000007000107', 'hugo.martins@demo.obrigacoes.local'
  UNION ALL SELECT 'NFSe importação', 'Mapeando municípios pendentes.', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'EM_ANDAMENTO', '99000008000108', 'ana.souza@demo.obrigacoes.local'
  UNION ALL SELECT 'Lucro real trimestral', 'Apuração parcial concluída.', DATE_ADD(CURDATE(), INTERVAL 9 DAY), 'EM_ANDAMENTO', '99000009000109', 'bruno.lima@demo.obrigacoes.local'
  UNION ALL SELECT 'Homologação folha', 'Cliente revisando holerites.', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'EM_ANDAMENTO', '99000010000110', 'carla.mendes@demo.obrigacoes.local'
  UNION ALL SELECT 'Créditos PIS/COFINS', 'Documentação de insumos.', DATE_ADD(CURDATE(), INTERVAL 11 DAY), 'EM_ANDAMENTO', '99000011000111', 'diego.rocha@demo.obrigacoes.local'
  UNION ALL SELECT 'Regularização CNPJ', 'Pendência cadastral RFB.', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'EM_ANDAMENTO', '99000012000112', 'elena.prado@demo.obrigacoes.local'
  UNION ALL SELECT 'Fechamento societário', 'Atas e integralização.', DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'EM_ANDAMENTO', '99000013000113', 'fabio.nunes@demo.obrigacoes.local'
  UNION ALL SELECT 'Controle de ativos', 'Imobilizado em reconciliação.', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'EM_ANDAMENTO', '99000014000114', 'gisele.torres@demo.obrigacoes.local'
  UNION ALL SELECT 'Obrigações acessórias DF', 'Checklist estadual em curso.', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'EM_ANDAMENTO', '99000015000115', 'hugo.martins@demo.obrigacoes.local'
  UNION ALL SELECT 'Revisão pró-labore', 'Cálculo INSS em análise.', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'EM_ANDAMENTO', '99000016000116', 'ana.souza@demo.obrigacoes.local'
  UNION ALL SELECT 'Integração ERP', 'Homologando plano de contas.', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'EM_ANDAMENTO', '99000017000117', 'bruno.lima@demo.obrigacoes.local'
  UNION ALL SELECT 'Defesa auto de infração', 'Minuta jurídica em revisão.', DATE_ADD(CURDATE(), INTERVAL 18 DAY), 'EM_ANDAMENTO', '99000018000118', 'carla.mendes@demo.obrigacoes.local'
  UNION ALL SELECT 'Apuração ST', 'Planilha de substituição.', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'EM_ANDAMENTO', '99000019000119', 'diego.rocha@demo.obrigacoes.local'
  UNION ALL SELECT 'Encargos patronais', 'Conferência GPS/DARF.', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'EM_ANDAMENTO', '99000020000120', 'elena.prado@demo.obrigacoes.local'

  UNION ALL SELECT 'DAS outubro', 'Transmitido e arquivado.', DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'CONCLUIDA', '99000001000101', 'carla.mendes@demo.obrigacoes.local'
  UNION ALL SELECT 'Folha setembro', 'Encargos pagos.', DATE_SUB(CURDATE(), INTERVAL 25 DAY), 'CONCLUIDA', '99000002000102', 'diego.rocha@demo.obrigacoes.local'
  UNION ALL SELECT 'EFD ICMS/IPI', 'Recibo armazenado.', DATE_SUB(CURDATE(), INTERVAL 18 DAY), 'CONCLUIDA', '99000003000103', 'elena.prado@demo.obrigacoes.local'
  UNION ALL SELECT 'DCTF setembro', 'Declaração aceita.', DATE_SUB(CURDATE(), INTERVAL 22 DAY), 'CONCLUIDA', '99000004000104', 'fabio.nunes@demo.obrigacoes.local'
  UNION ALL SELECT 'Relatório mensal', 'Enviado ao cliente.', DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'CONCLUIDA', '99000005000105', 'gisele.torres@demo.obrigacoes.local'
  UNION ALL SELECT 'eSocial não periódicos', 'Eventos sem rejeição.', DATE_SUB(CURDATE(), INTERVAL 12 DAY), 'CONCLUIDA', '99000006000106', 'hugo.martins@demo.obrigacoes.local'
  UNION ALL SELECT 'IRRF apuração', 'DARF quitado.', DATE_SUB(CURDATE(), INTERVAL 28 DAY), 'CONCLUIDA', '99000007000107', 'ana.souza@demo.obrigacoes.local'
  UNION ALL SELECT 'Conciliacão bancária', 'Diferenças zeradas.', DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'CONCLUIDA', '99000008000108', 'bruno.lima@demo.obrigacoes.local'
  UNION ALL SELECT 'CND municipal', 'Certidão válida anexada.', DATE_SUB(CURDATE(), INTERVAL 8 DAY), 'CONCLUIDA', '99000009000109', 'carla.mendes@demo.obrigacoes.local'
  UNION ALL SELECT 'Inventário parcial', 'Ajustes lançados.', DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'CONCLUIDA', '99000010000110', 'diego.rocha@demo.obrigacoes.local'
  UNION ALL SELECT 'Atualização sócios', 'Quadro societário ok.', DATE_SUB(CURDATE(), INTERVAL 40 DAY), 'CONCLUIDA', '99000011000111', 'elena.prado@demo.obrigacoes.local'
  UNION ALL SELECT 'Entrega RAIS', 'Protocolo salvo.', DATE_SUB(CURDATE(), INTERVAL 55 DAY), 'CONCLUIDA', '99000012000112', 'fabio.nunes@demo.obrigacoes.local'
  UNION ALL SELECT 'Revisão contratos PJ', 'Checklist concluído.', DATE_SUB(CURDATE(), INTERVAL 14 DAY), 'CONCLUIDA', '99000013000113', 'gisele.torres@demo.obrigacoes.local'
  UNION ALL SELECT 'Fechamento 3º trimestre', 'DRE aprovada.', DATE_SUB(CURDATE(), INTERVAL 35 DAY), 'CONCLUIDA', '99000014000114', 'hugo.martins@demo.obrigacoes.local'
  UNION ALL SELECT 'Homologação admissão', 'Documentação arquivada.', DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'CONCLUIDA', '99000015000115', 'ana.souza@demo.obrigacoes.local'
  UNION ALL SELECT 'Ajuste malha RFB', 'Pendência baixada.', DATE_SUB(CURDATE(), INTERVAL 45 DAY), 'CONCLUIDA', '99000016000116', 'bruno.lima@demo.obrigacoes.local'
) seed;

SELECT
  (SELECT COUNT(*) FROM usuarios WHERE email LIKE '%@demo.obrigacoes.local') AS usuarios_demo,
  (SELECT COUNT(*) FROM empresas WHERE cnpj LIKE '99%') AS empresas_demo,
  (SELECT COUNT(*) FROM obrigacoes o
     INNER JOIN empresas e ON e.id = o.empresa_id
     WHERE e.cnpj LIKE '99%') AS obrigacoes_demo,
  (SELECT COUNT(*) FROM usuarios WHERE email LIKE '%@demo.obrigacoes.local' AND situacao = 'ATIVO') AS usuarios_ativos,
  (SELECT COUNT(*) FROM usuarios WHERE email LIKE '%@demo.obrigacoes.local' AND situacao = 'INATIVO') AS usuarios_inativos,
  (SELECT COUNT(*) FROM empresas WHERE cnpj LIKE '99%' AND situacao = 'ATIVO') AS empresas_ativas,
  (SELECT COUNT(*) FROM empresas WHERE cnpj LIKE '99%' AND situacao = 'INATIVO') AS empresas_inativas,
  (SELECT COUNT(*) FROM obrigacoes o INNER JOIN empresas e ON e.id = o.empresa_id WHERE e.cnpj LIKE '99%' AND o.status = 'PENDENTE') AS obr_pendente,
  (SELECT COUNT(*) FROM obrigacoes o INNER JOIN empresas e ON e.id = o.empresa_id WHERE e.cnpj LIKE '99%' AND o.status = 'EM_ANDAMENTO') AS obr_andamento,
  (SELECT COUNT(*) FROM obrigacoes o INNER JOIN empresas e ON e.id = o.empresa_id WHERE e.cnpj LIKE '99%' AND o.status = 'CONCLUIDA') AS obr_concluida;
