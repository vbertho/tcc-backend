package com.example.tcc_backend.controller;

import com.example.tcc_backend.model.Documento;
import com.example.tcc_backend.model.TipoDocumento;
import com.example.tcc_backend.service.DocumentoService;
import com.example.tcc_backend.support.ControllerTestSupport;
import com.example.tcc_backend.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentoControllerIntegrationTest {

    @Mock
    private DocumentoService documentoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = ControllerTestSupport.buildMockMvc(new DocumentoController(documentoService));
    }

    @Test
    void uploadDeveRetornarDocumentoResponse() throws Exception {
        Documento documento = TestDataFactory.documento(1, TestDataFactory.usuarioAluno(1), "uploads/documentos/1/arquivo.pdf");
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "arquivo.pdf", "application/pdf", "conteudo".getBytes());

        when(documentoService.upload(eq(TipoDocumento.CURRICULO), any())).thenReturn(documento);

        mockMvc.perform(multipart("/api/documentos/upload")
                        .file(arquivo)
                        .param("tipo", "CURRICULO"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("CURRICULO"))
                .andExpect(jsonPath("$.nomeArquivo").value("arquivo.pdf"));
    }

    @Test
    void deleteDeveRetornarNoContent() throws Exception {
        doNothing().when(documentoService).remover(5);

        mockMvc.perform(delete("/api/documentos/5"))
                .andExpect(status().isNoContent());
    }
}
