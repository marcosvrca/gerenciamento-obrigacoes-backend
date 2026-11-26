package com.escritorio.obrigacoes.web.dto;

import com.escritorio.obrigacoes.domain.StatusObrigacao;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull StatusObrigacao status
) {
}
