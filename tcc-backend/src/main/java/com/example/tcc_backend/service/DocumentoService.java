package com.example.tcc_backend.service;

import com.example.tcc_backend.model.Documento;
import com.example.tcc_backend.model.StatusDocumento;
import com.example.tcc_backend.model.TipoDocumento;
import com.example.tcc_backend.model.Usuario;
import com.example.tcc_backend.repository.DocumentoRepository;
import com.example.tcc_backend.repository.UsuarioRepository;
import com.example.tcc_backend.security.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private static final Map<String, List<String>> EXTENSOES_PERMITIDAS = Map.of(
            "application/pdf", List.of(".pdf"),
            "application/msword", List.of(".doc"),
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", List.of(".docx")
    );

    private final DocumentoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthHelper authHelper;

    public Documento upload(Integer usuarioId, TipoDocumento tipo, MultipartFile arquivo) {
        if (tipo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo do documento obrigatório");
        }

        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo obrigatório");
        }

        // 1. Em vez de usar o authHelper (que pegaria o Admin),
        // nós buscamos o usuário pelo ID que veio do React
        Usuario usuarioAlvo = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // 2. Salva o arquivo na pasta correta desse usuário
        String caminho = salvarArquivo(usuarioAlvo, arquivo);

        // 3. Monta o documento vinculando ao usuário alvo
        Documento documento = Documento.builder()
                .usuario(usuarioAlvo)
                .tipo(tipo)
                .nomeArquivo(arquivo.getOriginalFilename()) // ⬅️ IMPORTANTE: Salva o nome para o React mostrar na tela!
                .status(StatusDocumento.ENVIADO)
                .caminho(caminho)
                .build();

        return documentoRepository.save(documento);
    }

    public List<Documento> listarPorUsuario(Integer usuarioId) {
        // Removemos a verificação do authHelper que bloqueava o Admin
        // Agora ele simplesmente vai no banco e devolve os arquivos do usuário
        return documentoRepository.findByUsuarioId(usuarioId);
    }

    public void remover(Integer id) {
        Documento documento = buscarDocumentoDoUsuario(id);

        apagarArquivo(documento.getCaminho());
        documentoRepository.delete(documento);
    }

    public Documento buscarDocumentoDoUsuario(Integer id) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento nao encontrado"));

        if (!documento.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para acessar este documento");
        }
        return documento;
    }

    public Path obterArquivo(Integer id) {
        Documento documento = buscarDocumentoDoUsuario(id);
        Path caminho = Path.of(documento.getCaminho());
        if (!Files.exists(caminho)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Arquivo do documento nao encontrado");
        }
        return caminho;
    }

    private String salvarArquivo(Usuario usuario, MultipartFile arquivo) {
        try {
            String extensao = validarArquivo(arquivo);
            Path dir = Path.of("uploads", "documentos", usuario.getId().toString());
            Files.createDirectories(dir);
            String nomeArquivo = UUID.randomUUID() + extensao;
            Path destino = dir.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toString();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao salvar arquivo");
        }
    }

    private String validarArquivo(MultipartFile arquivo) {
        String contentType = arquivo.getContentType();
        if (contentType == null || !EXTENSOES_PERMITIDAS.containsKey(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de arquivo nao permitido");
        }

        String nomeOriginal = arquivo.getOriginalFilename() == null
                ? ""
                : Path.of(arquivo.getOriginalFilename()).getFileName().toString().toLowerCase(Locale.ROOT);

        for (String extensao : EXTENSOES_PERMITIDAS.get(contentType)) {
            if (nomeOriginal.endsWith(extensao)) {
                return extensao;
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Extensao de arquivo nao permitida");
    }

    private void apagarArquivo(String caminho) {
        try {
            Files.deleteIfExists(Path.of(caminho));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao remover arquivo");
        }
    }
}
