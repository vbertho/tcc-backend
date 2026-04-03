package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.ProgressoRequest;
import com.example.tcc_backend.dto.response.ProgressoResponse;
import com.example.tcc_backend.service.ProgressoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

