package com.escritorio.obrigacoes.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StatusObrigacaoTest {

    @Test
    void devePermitirFluxoLinear() {
        assertTrue(StatusObrigacao.PENDENTE.podeTransicionarPara(StatusObrigacao.EM_ANDAMENTO));
        assertTrue(StatusObrigacao.EM_ANDAMENTO.podeTransicionarPara(StatusObrigacao.CONCLUIDA));
    }

    @Test
    void deveRejeitarRegressaoESaltos() {
        assertFalse(StatusObrigacao.EM_ANDAMENTO.podeTransicionarPara(StatusObrigacao.PENDENTE));
        assertFalse(StatusObrigacao.PENDENTE.podeTransicionarPara(StatusObrigacao.CONCLUIDA));
        assertFalse(StatusObrigacao.CONCLUIDA.podeTransicionarPara(StatusObrigacao.PENDENTE));
        assertFalse(StatusObrigacao.CONCLUIDA.podeTransicionarPara(StatusObrigacao.EM_ANDAMENTO));
        assertFalse(StatusObrigacao.CONCLUIDA.podeTransicionarPara(StatusObrigacao.CONCLUIDA));
    }
}
