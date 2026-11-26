package com.escritorio.obrigacoes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ObrigacaoRequest(
        @NotBlank @Size(max = 200) String titulo,
        @Size(max = 2000) String descricao,
        @NotNull LocalDate prazo,
        @NotNull Long empresaId,
        @NotNull Long usuarioId
) {
}
