package com.example.tcc_backend.controller;

import com.example.tcc_backend.model.AreaPesquisa;
import com.example.tcc_backend.model.Curso;
import com.example.tcc_backend.model.Projeto;
import com.example.tcc_backend.model.TipoUsuario;
import com.example.tcc_backend.repository.AreaPesquisaRepository;
import com.example.tcc_backend.repository.CursoRepository;
import com.example.tcc_backend.repository.ProjetoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:projeto-criacao-e2e;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.hikari.data-source-properties.sslmode=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test-secret-value-with-at-least-32-bytes!!"
})
@AutoConfigureMockMvc
class ProjetoCriacaoPersistenceE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AreaPesquisaRepository areaPesquisaRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Test
    void createDevePersistirTecnologiasECompetenciasNoBancoERetornarNaApi() throws Exception {
        Curso curso = cursoRepository.save(Curso.builder().nome("Ciencia da Computacao").build());
        AreaPesquisa area = areaPesquisaRepository.save(AreaPesquisa.builder().nome("Inteligencia Artificial").curso(curso).build());

        String token = registrarOrientadorERetornarToken();
        String tecnologias = "React, Spring Boot, PostgreSQL";

        String createdBody = mockMvc.perform(post("/api/projetos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "titulo", "Projeto com competencias",
                                "descricao", "Projeto criado em teste E2E real.",
                                "requisitos", "Java",
                                "competencias", tecnologias,
                                "areaId", area.getId(),
                                "vagas", 2
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tecnologias").value(tecnologias))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int projetoId = objectMapper.readTree(createdBody).get("id").asInt();

        Projeto persisted = projetoRepository.findById(projetoId).orElseThrow();
        assertThat(persisted.getTecnologias()).isEqualTo(tecnologias);

        mockMvc.perform(get("/api/projetos/{id}", projetoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tecnologias").value(tecnologias));
    }

    private String registrarOrientadorERetornarToken() throws Exception {
        String responseBody = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nome", "Orientador E2E",
                                "email", "orientador.e2e@example.com",
                                "senha", "SenhaE2E123!",
                                "tipo", TipoUsuario.ORIENTADOR,
                                "departamento", "Computacao",
                                "titulacao", "Doutor"
                        ))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(responseBody);
        return json.get("token").asText();
    }
}
