package com.escritorio.obrigacoes.web.dto;

import com.escritorio.obrigacoes.domain.Empresa;
import com.escritorio.obrigacoes.domain.Situacao;
import java.time.Instant;

public record EmpresaResponse(
        Long id,
        String razaoSocial,
        String cnpj,
        String email,
        Situacao situacao,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static EmpresaResponse from(Empresa empresa) {
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getRazaoSocial(),
                empresa.getCnpj(),
                empresa.getEmail(),
                empresa.getSituacao(),
                empresa.getCriadoEm(),
                empresa.getAtualizadoEm()
        );
    }
}
