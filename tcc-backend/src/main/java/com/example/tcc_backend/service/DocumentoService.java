package com.example.tcc_backend.service;

import com.example.tcc_backend.model.Documento;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthHelper authHelper;

    public Documento upload(TipoDocumento tipo, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo obrigatorio");
        }

        Usuario usuarioLogado = authHelper.getCurrentUser();
        String caminho = salvarArquivo(arquivo);

        Documento documento = Documento.builder()
                .usuario(usuarioLogado)
                .tipo(tipo)
                .caminho(caminho)
                .build();
        return documentoRepository.save(documento);
    }

    public List<Documento> listarPorUsuario(Integer usuarioId) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        if (!usuarioLogado.getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para listar documentos de outro usuario");
        }

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
        return documentoRepository.findByUsuarioId(usuarioId);
    }

    public void remover(Integer id) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento nao encontrado"));

        if (!documento.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para remover este documento");
        }

        documentoRepository.delete(documento);
    }

    private String salvarArquivo(MultipartFile arquivo) {
        try {
            Path dir = Path.of("uploads");
            Files.createDirectories(dir);
            String nomeOriginal = arquivo.getOriginalFilename() == null ? "arquivo.bin" : arquivo.getOriginalFilename();
            String nomeArquivo = UUID.randomUUID() + "_" + nomeOriginal.replaceAll("\\s+", "_");
            Path destino = dir.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toString();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao salvar arquivo");
        }
    }
}
