package com.escritorio.obrigacoes.domain;

import java.util.EnumSet;
import java.util.Set;

public enum StatusObrigacao {
    PENDENTE,
    EM_ANDAMENTO,
    CONCLUIDA;

    public boolean podeTransicionarPara(StatusObrigacao novoStatus) {
        return transicoesPermitidas().contains(novoStatus);
    }

    private Set<StatusObrigacao> transicoesPermitidas() {
        return switch (this) {
            case PENDENTE -> EnumSet.of(EM_ANDAMENTO);
            case EM_ANDAMENTO -> EnumSet.of(CONCLUIDA);
            case CONCLUIDA -> EnumSet.noneOf(StatusObrigacao.class);
        };
    }
}
