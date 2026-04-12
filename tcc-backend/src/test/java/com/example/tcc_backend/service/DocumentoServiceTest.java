package com.example.tcc_backend.service;

import com.example.tcc_backend.model.Documento;
import com.example.tcc_backend.model.TipoDocumento;
import com.example.tcc_backend.model.Usuario;
import com.example.tcc_backend.repository.DocumentoRepository;
import com.example.tcc_backend.repository.UsuarioRepository;
import com.example.tcc_backend.security.AuthHelper;
import com.example.tcc_backend.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @Mock
    private DocumentoRepository documentoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AuthHelper authHelper;

    @InjectMocks
    private DocumentoService documentoService;

    @Test
    void uploadDeveSalvarDocumentoValido() throws Exception {
        Usuario usuario = TestDataFactory.usuarioAluno(1);
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "curriculo.pdf",
                "application/pdf",
                "conteudo".getBytes()
        );

        // 1. MUDANÇA AQUI: Nós não usamos mais o authHelper nesse método.
        // Agora o Service busca o usuário pelo ID, então precisamos simular o banco:
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(usuario));

        when(documentoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. MUDANÇA AQUI: Passamos o ID do usuário (1) como primeiro parâmetro!
        Documento documento = documentoService.upload(1, TipoDocumento.CURRICULO, arquivo);

        Path caminho = Path.of(documento.getCaminho());
        assertThat(Files.exists(caminho)).isTrue();
        assertThat(caminho.getFileName().toString()).endsWith(".pdf");

        // BÔNUS: Já que adicionamos a coluna nova, vamos garantir que ela está sendo preenchida!
        assertThat(documento.getNomeArquivo()).isEqualTo("curriculo.pdf");

        verify(documentoRepository).save(any());

        limparArquivoCriado(caminho);
    }

    @Test
    void uploadDeveNegarTipoDeArquivoInvalido() {
        Usuario usuario = TestDataFactory.usuarioAluno(1);
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "malicioso.exe",
                "application/octet-stream",
                "conteudo".getBytes()
        );

        when(authHelper.getCurrentUser()).thenReturn(usuario);

        assertThatThrownBy(() -> documentoService.upload(1,TipoDocumento.CURRICULO, arquivo))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void listarPorUsuarioDeveNegarAcessoDeOutroUsuario() {
        when(authHelper.getCurrentUser()).thenReturn(TestDataFactory.usuarioAluno(1));

        assertThatThrownBy(() -> documentoService.listarPorUsuario(2))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void removerDeveNegarQuandoDocumentoForDeOutroUsuario() {
        Usuario dono = TestDataFactory.usuarioAluno(2);
        Usuario usuarioLogado = TestDataFactory.usuarioAluno(1);
        Documento documento = TestDataFactory.documento(5, dono, "uploads/documentos/2/doc.pdf");

        when(authHelper.getCurrentUser()).thenReturn(usuarioLogado);
        when(documentoRepository.findById(5)).thenReturn(Optional.of(documento));

        assertThatThrownBy(() -> documentoService.remover(5))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    private void limparArquivoCriado(Path caminho) throws IOException {
        Files.deleteIfExists(caminho);
        try {
            Files.deleteIfExists(caminho.getParent());
        } catch (IOException ignored) {
        }
    }
}
