package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.UsuarioPreferenciasRequest;
import com.example.tcc_backend.dto.request.UsuarioRequest;
import com.example.tcc_backend.dto.response.DocumentoResponse;
import com.example.tcc_backend.dto.response.InscricaoResponse;
import com.example.tcc_backend.dto.response.PageResponse;
import com.example.tcc_backend.dto.response.ProjetoResponse;
import com.example.tcc_backend.dto.response.UsuarioProfileResponse;
import com.example.tcc_backend.dto.response.UsuarioResponse;
import com.example.tcc_backend.service.DocumentoService;
import com.example.tcc_backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final DocumentoService documentoService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(
                usuarioService.findAll().stream()
                        .map(UsuarioResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/pagina")
    public ResponseEntity<PageResponse<UsuarioResponse>> findAllPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(
                PageResponse.from(
                        usuarioService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort)))
                                .map(UsuarioResponse::fromEntity)
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioProfileResponse> me() {
        return ResponseEntity.ok(usuarioService.me());
    }

    @PutMapping("/me/preferencias")
    public ResponseEntity<UsuarioProfileResponse> updatePreferencias(@RequestBody @Valid UsuarioPreferenciasRequest dto) {
        return ResponseEntity.ok(usuarioService.updatePreferencias(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(UsuarioResponse.fromEntity(usuarioService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(@PathVariable Integer id,
                                                  @RequestBody @Valid UsuarioRequest dto) {
        return ResponseEntity.ok(UsuarioResponse.fromEntity(usuarioService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/projetos")
    public ResponseEntity<List<ProjetoResponse>> findProjetosByUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.findProjetosByUsuario(id).stream().map(ProjetoResponse::fromEntity).toList());
    }

    @GetMapping("/{id}/inscricoes")
    public ResponseEntity<List<InscricaoResponse>> findInscricoesByUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.findInscricoesByUsuario(id).stream().map(InscricaoResponse::fromEntity).toList());
    }

    @GetMapping("/minhas-inscricoes")
    public ResponseEntity<List<InscricaoResponse>> findMinhasInscricoes() {
        return ResponseEntity.ok(usuarioService.findMinhasInscricoes().stream().map(InscricaoResponse::fromEntity).toList());
    }

    @GetMapping("/{id}/documentos")
    public ResponseEntity<List<DocumentoResponse>> findDocumentosByUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(
                documentoService.listarPorUsuario(id).stream()
                        .map(DocumentoResponse::fromEntity)
                        .toList()
        );
    }
}
