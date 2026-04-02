package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.ProjetoRequest;
import com.example.tcc_backend.dto.request.RecrutarColaboradorRequest;
import com.example.tcc_backend.dto.response.UsuarioResponse;
import com.example.tcc_backend.model.Inscricao;
import com.example.tcc_backend.model.Projeto;
import com.example.tcc_backend.model.StatusProjeto;
import com.example.tcc_backend.service.ProjetoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;

    @GetMapping
    public ResponseEntity<List<Projeto>> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer areaId,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String busca) {

        if (status != null) {
            try {
                return ResponseEntity.ok(projetoService.findByStatus(StatusProjeto.valueOf(status.toUpperCase())));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status invalido");
            }
        }
        if (areaId != null) return ResponseEntity.ok(projetoService.findByArea(areaId));
        if (area != null) return ResponseEntity.ok(projetoService.findByAreaNome(area));
        if (curso != null) return ResponseEntity.ok(projetoService.findByCursoNome(curso));
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

    @PostMapping("/{id}/recrutar")
    public ResponseEntity<Inscricao> recrutar(@PathVariable Integer id,
                                              @RequestBody @Valid RecrutarColaboradorRequest dto) {
        return ResponseEntity.ok(projetoService.recrutar(id, dto.getUsuarioId()));
    }

    @GetMapping("/{id}/colaboradores")
    public ResponseEntity<List<UsuarioResponse>> listarColaboradores(@PathVariable Integer id) {
        return ResponseEntity.ok(
                projetoService.listarColaboradores(id).stream()
                        .map(UsuarioResponse::fromEntity)
                        .toList()
        );
    }

    @DeleteMapping("/{id}/colaboradores/{usuarioId}")
    public ResponseEntity<Void> removerColaborador(@PathVariable Integer id,
                                                   @PathVariable Integer usuarioId) {
        projetoService.removerColaborador(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}

