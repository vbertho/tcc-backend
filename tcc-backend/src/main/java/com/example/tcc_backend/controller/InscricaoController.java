package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.InscricaoRequest;
import com.example.tcc_backend.model.Inscricao;
import com.example.tcc_backend.service.InscricaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscricoes")
@RequiredArgsConstructor
public class InscricaoController {

    private final InscricaoService inscricaoService;

    @GetMapping
    public ResponseEntity<List<Inscricao>> findAll() {
        return ResponseEntity.ok(inscricaoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscricao> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(inscricaoService.findById(id));
    }

    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<Inscricao>> findByProjeto(@PathVariable Integer projetoId) {
        return ResponseEntity.ok(inscricaoService.findByProjeto(projetoId));
    }

    @PostMapping
    public ResponseEntity<Inscricao> create(@RequestBody @Valid InscricaoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inscricaoService.create(dto));
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<Inscricao> aprovar(@PathVariable Integer id) {
        return ResponseEntity.ok(inscricaoService.aprovar(id));
    }

    @PutMapping("/{id}/rejeitar")
    public ResponseEntity<Inscricao> rejeitar(@PathVariable Integer id) {
        return ResponseEntity.ok(inscricaoService.rejeitar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        inscricaoService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inscricao> update(@PathVariable Integer id,
                                            @RequestBody @Valid InscricaoRequest dto) {
        return ResponseEntity.ok(inscricaoService.update(id, dto));
    }
}
