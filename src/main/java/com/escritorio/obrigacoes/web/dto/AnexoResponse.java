package com.escritorio.obrigacoes.web.dto;

import com.escritorio.obrigacoes.domain.Anexo;
import java.time.Instant;

public record AnexoResponse(
        Long id,
        String nomeOriginal,
        String contentType,
        long tamanhoBytes,
        Long obrigacaoId,
        Instant criadoEm
) {
    public static AnexoResponse from(Anexo anexo) {
        return new AnexoResponse(
                anexo.getId(),
                anexo.getNomeOriginal(),
                anexo.getContentType(),
                anexo.getTamanhoBytes(),
                anexo.getObrigacao().getId(),
                anexo.getCriadoEm()
        );
    }
}
