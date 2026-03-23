package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.ProjetoRequest;
import com.example.tcc_backend.model.Projeto;
import com.example.tcc_backend.model.StatusProjeto;
import com.example.tcc_backend.service.ProjetoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;

    @GetMapping
    public ResponseEntity<List<Projeto>> findAll(
            @RequestParam(required = false) StatusProjeto status,
            @RequestParam(required = false) Integer areaId,
            @RequestParam(required = false) String busca) {

        if (status != null) return ResponseEntity.ok(projetoService.findByStatus(status));
        if (areaId != null) return ResponseEntity.ok(projetoService.findByArea(areaId));
        if (busca != null) return ResponseEntity.ok(projetoService.findByBusca(busca));

        return ResponseEntity.ok(projetoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(projetoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Projeto> create(@RequestBody @Valid ProjetoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Projeto> update(@PathVariable Integer id,
                                          @RequestBody @Valid ProjetoRequest dto) {
        return ResponseEntity.ok(projetoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        projetoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}