package com.escritorio.obrigacoes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GerenciamentoObrigacoesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GerenciamentoObrigacoesApplication.class, args);
    }
}
