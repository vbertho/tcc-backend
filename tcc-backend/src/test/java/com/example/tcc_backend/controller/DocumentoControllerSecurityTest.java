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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentoControllerSecurityTest {

    @Mock
    private DocumentoService documentoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = ControllerTestSupport.buildMockMvc(new DocumentoController(documentoService));
    }

    @Test
    void uploadDeveRetornarNomeOriginalNoResponseNaoONomeFisicoUUID() throws Exception {
        var usuario = TestDataFactory.usuarioAluno(1);

        // Caminho fisico (UUID.ext), nomeArquivo e o original que queremos exibir no front.
        Documento documento = Documento.builder()
                .id(1)
                .usuario(usuario)
                .tipo(TipoDocumento.CURRICULO)
                .caminho("uploads/documentos/1/8b12c5bf-6a5b-4f04-9fa5-6b0a7d7b3c4a.pdf")
                .nomeArquivo("curriculo_do_joao.pdf")
                .build();

        when(documentoService.upload(eq(1), eq(TipoDocumento.CURRICULO), any())).thenReturn(documento);

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "curriculo_do_joao.pdf",
                "application/pdf",
                "conteudo".getBytes()
        );

        mockMvc.perform(multipart("/api/documentos/upload")
                        .param("usuarioId", "1")
                        .param("tipo", "CURRICULO")
                        .file(arquivo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("CURRICULO"))
                .andExpect(jsonPath("$.nomeArquivo").value("curriculo_do_joao.pdf"));
    }

    @Test
    void previewDocxDeveResponderComContentTypeDocx() throws Exception {
        when(documentoService.obterArquivo(1))
                .thenReturn(Path.of("uploads/documentos/1/arquivo.docx"));

        mockMvc.perform(get("/api/documentos/1/preview"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void previewDocDeveResponderComContentTypeDoc() throws Exception {
        when(documentoService.obterArquivo(2))
                .thenReturn(Path.of("uploads/documentos/1/arquivo.doc"));

        mockMvc.perform(get("/api/documentos/2/preview"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void previewPdfDeveResponderComContentTypePdf() throws Exception {
        Path tmp = java.nio.file.Files.createTempFile("documento-preview-", ".pdf");
        java.nio.file.Files.writeString(tmp, "%PDF-1.7\nx");
        when(documentoService.obterArquivo(3)).thenReturn(tmp);

        mockMvc.perform(get("/api/documentos/3/preview"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}
