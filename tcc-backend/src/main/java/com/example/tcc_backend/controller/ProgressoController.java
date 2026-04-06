package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.ProgressoRequest;
import com.example.tcc_backend.dto.response.PageResponse;
import com.example.tcc_backend.dto.response.ProgressoResponse;
import com.example.tcc_backend.service.ProgressoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProgressoController {

    private final ProgressoService progressoService;

    @PostMapping("/projetos/{id}/progresso")
    public ResponseEntity<ProgressoResponse> criar(@PathVariable Integer id,
                                                   @RequestBody @Valid ProgressoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProgressoResponse.fromEntity(progressoService.criar(id, dto)));
    }

    @GetMapping("/projetos/{id}/progresso")
    public ResponseEntity<List<ProgressoResponse>> listarPorProjeto(@PathVariable Integer id) {
        return ResponseEntity.ok(
                progressoService.listarPorProjeto(id).stream()
                        .map(ProgressoResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/projetos/{id}/progresso/pagina")
    public ResponseEntity<PageResponse<ProgressoResponse>> listarPorProjetoPaginado(@PathVariable Integer id,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size) {
        List<ProgressoResponse> content = progressoService.listarPorProjeto(id).stream()
                .map(ProgressoResponse::fromEntity)
                .toList();
        int start = Math.min(page * size, content.size());
        int end = Math.min(start + size, content.size());
        return ResponseEntity.ok(PageResponse.from(new PageImpl<>(content.subList(start, end), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataRegistro")), content.size())));
    }

    @PutMapping("/progresso/{id}")
    public ResponseEntity<ProgressoResponse> atualizar(@PathVariable Integer id,
                                                       @RequestBody @Valid ProgressoRequest dto) {
        return ResponseEntity.ok(ProgressoResponse.fromEntity(progressoService.atualizar(id, dto)));
    }

    @DeleteMapping("/progresso/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        progressoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}

