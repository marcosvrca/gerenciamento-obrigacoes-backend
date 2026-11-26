package com.escritorio.obrigacoes.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.escritorio.obrigacoes.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class ApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fluxoCompletoEmpresasUsuariosObrigacoesEAnexos() throws Exception {
        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "razaoSocial": "Contábil Alpha LTDA",
                                  "cnpj": "12345678000199",
                                  "email": "contato@alpha.com",
                                  "situacao": "ATIVO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ana Responsável",
                                  "email": "ana@escritorio.com",
                                  "situacao": "ATIVO"
                                }
                                """))
                .andExpect(status().isCreated());

        MvcResult obrigacaoResult = mockMvc.perform(post("/api/obrigacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Entrega DCTF",
                                  "descricao": "Declaração mensal",
                                  "prazo": "2030-01-15",
                                  "empresaId": 1,
                                  "usuarioId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andReturn();

        String obrigacaoJson = obrigacaoResult.getResponse().getContentAsString();
        long obrigacaoId = Long.parseLong(obrigacaoJson.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        mockMvc.perform(patch("/api/obrigacoes/{id}/status", obrigacaoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"EM_ANDAMENTO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));

        mockMvc.perform(patch("/api/obrigacoes/{id}/status", obrigacaoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PENDENTE\"}"))
                .andExpect(status().isUnprocessableEntity());

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "comprovante.pdf",
                "application/pdf",
                "conteudo-pdf".getBytes()
        );

        mockMvc.perform(multipart("/api/obrigacoes/{id}/anexos", obrigacaoId).file(arquivo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeOriginal").value("comprovante.pdf"));

        mockMvc.perform(get("/api/obrigacoes/{id}/anexos", obrigacaoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put("/api/empresas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "razaoSocial": "Contábil Alpha LTDA",
                                  "cnpj": "12345678000199",
                                  "email": "contato@alpha.com",
                                  "situacao": "INATIVO"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/obrigacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Nova obrigação",
                                  "descricao": null,
                                  "prazo": "2030-02-01",
                                  "empresaId": 1,
                                  "usuarioId": 1
                                }
                                """))
                .andExpect(status().isUnprocessableEntity());
    }
}
