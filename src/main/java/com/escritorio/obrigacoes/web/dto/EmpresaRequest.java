package com.escritorio.obrigacoes.web.dto;

import com.escritorio.obrigacoes.domain.Situacao;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(
        @NotBlank @Size(max = 200) String razaoSocial,
        @NotBlank @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos numéricos") String cnpj,
        @NotBlank @Email @Size(max = 150) String email,
        @NotNull Situacao situacao
) {
}
