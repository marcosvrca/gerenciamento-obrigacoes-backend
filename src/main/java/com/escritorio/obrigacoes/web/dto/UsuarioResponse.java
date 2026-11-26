package com.escritorio.obrigacoes.web.dto;

import com.escritorio.obrigacoes.domain.Situacao;
import com.escritorio.obrigacoes.domain.Usuario;
import java.time.Instant;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Situacao situacao,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSituacao(),
                usuario.getCriadoEm(),
                usuario.getAtualizadoEm()
        );
    }
}
