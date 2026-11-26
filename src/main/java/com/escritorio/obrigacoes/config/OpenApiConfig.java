package com.escritorio.obrigacoes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Obrigações")
                        .description("API REST para escritório de contabilidade gerenciar empresas, usuários, obrigações e anexos.")
                        .version("1.0.0"));
    }
}
