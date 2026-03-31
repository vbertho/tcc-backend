package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.response.UsuarioResponse;
import com.example.tcc_backend.service.UsuarioService;
import com.example.tcc_backend.service.DocumentoService;
import com.example.tcc_backend.model.Documento;
import com.example.tcc_backend.model.Inscricao;
import com.example.tcc_backend.model.Projeto;
import com.example.tcc_backend.dto.request.UsuarioRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
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
    public ResponseEntity<List<Projeto>> findProjetosByUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.findProjetosByUsuario(id));
    }

    @GetMapping("/{id}/inscricoes")
    public ResponseEntity<List<Inscricao>> findInscricoesByUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.findInscricoesByUsuario(id));
    }

    @GetMapping("/minhas-inscricoes")
    public ResponseEntity<List<Inscricao>> findMinhasInscricoes() {
        return ResponseEntity.ok(usuarioService.findMinhasInscricoes());
    }

    @GetMapping("/{id}/documentos")
    public ResponseEntity<List<Documento>> findDocumentosByUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(documentoService.listarPorUsuario(id));
    }
}
