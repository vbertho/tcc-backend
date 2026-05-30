package com.example.tcc_backend.controller;

import com.example.tcc_backend.config.JacksonConfig;
import com.example.tcc_backend.repository.UsuarioRepository;
import com.example.tcc_backend.security.JwtAuthFilter;
import com.example.tcc_backend.security.SecurityConfig;
import com.example.tcc_backend.security.TokenRevocationService;
import com.example.tcc_backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.not;

@WebMvcTest(controllers = HealthController.class)
@Import({SecurityConfig.class, JacksonConfig.class, JwtAuthFilter.class})
class HealthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private TokenRevocationService tokenRevocationService;

    @Test
    void healthDeveSerPublicoERetornarOkSemCorpo() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void faviconDeveSerPublico() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().is(not(401)));
    }

    @Test
    void rotaNaoLiberadaDeveContinuarExigindoAutenticacao() throws Exception {
        mockMvc.perform(get("/api/protected-probe"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
}
