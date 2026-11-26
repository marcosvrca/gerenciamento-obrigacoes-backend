package com.escritorio.obrigacoes.web.dto;

import com.escritorio.obrigacoes.domain.Obrigacao;
import com.escritorio.obrigacoes.domain.StatusObrigacao;
import java.time.Instant;
import java.time.LocalDate;

public record ObrigacaoResponse(
        Long id,
        String titulo,
        String descricao,
        LocalDate prazo,
        StatusObrigacao status,
        Long empresaId,
        String empresaRazaoSocial,
        Long usuarioId,
        String usuarioNome,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ObrigacaoResponse from(Obrigacao obrigacao) {
        return new ObrigacaoResponse(
                obrigacao.getId(),
                obrigacao.getTitulo(),
                obrigacao.getDescricao(),
                obrigacao.getPrazo(),
                obrigacao.getStatus(),
                obrigacao.getEmpresa().getId(),
                obrigacao.getEmpresa().getRazaoSocial(),
                obrigacao.getResponsavel().getId(),
                obrigacao.getResponsavel().getNome(),
                obrigacao.getCriadoEm(),
                obrigacao.getAtualizadoEm()
        );
    }
}
