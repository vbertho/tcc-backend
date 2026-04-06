package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.InscricaoAvaliacaoRequest;
import com.example.tcc_backend.dto.request.InscricaoRequest;
import com.example.tcc_backend.dto.response.InscricaoResponse;
import com.example.tcc_backend.dto.response.PageResponse;
import com.example.tcc_backend.service.InscricaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inscricoes")
@RequiredArgsConstructor
public class InscricaoController {

    private final InscricaoService inscricaoService;

    @GetMapping
    public ResponseEntity<List<InscricaoResponse>> findAll() {
        return ResponseEntity.ok(inscricaoService.findAll().stream().map(InscricaoResponse::fromEntity).toList());
    }

    @GetMapping("/pagina")
    public ResponseEntity<PageResponse<InscricaoResponse>> findAllPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataInscricao") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(
                PageResponse.from(
                        inscricaoService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort)))
                                .map(InscricaoResponse::fromEntity)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscricaoResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(InscricaoResponse.fromEntity(inscricaoService.findById(id)));
    }

    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<InscricaoResponse>> findByProjeto(@PathVariable Integer projetoId) {
        return ResponseEntity.ok(inscricaoService.findByProjeto(projetoId).stream().map(InscricaoResponse::fromEntity).toList());
    }

    @GetMapping("/projeto/{projetoId}/pagina")
    public ResponseEntity<PageResponse<InscricaoResponse>> findByProjetoPaginado(
            @PathVariable Integer projetoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataInscricao") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(
                PageResponse.from(
                        inscricaoService.findByProjeto(projetoId, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort)))
                                .map(InscricaoResponse::fromEntity)
                )
        );
    }

    @PostMapping
    public ResponseEntity<InscricaoResponse> create(@RequestBody @Valid InscricaoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(InscricaoResponse.fromEntity(inscricaoService.create(dto)));
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<InscricaoResponse> aprovar(@PathVariable Integer id,
                                                     @RequestBody(required = false) @Valid InscricaoAvaliacaoRequest dto) {
        return ResponseEntity.ok(InscricaoResponse.fromEntity(inscricaoService.aprovar(id, dto)));
    }

    @PutMapping("/{id}/rejeitar")
    public ResponseEntity<InscricaoResponse> rejeitar(@PathVariable Integer id,
                                                      @RequestBody(required = false) @Valid InscricaoAvaliacaoRequest dto) {
        return ResponseEntity.ok(InscricaoResponse.fromEntity(inscricaoService.rejeitar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        inscricaoService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarMinha(@PathVariable Integer id) {
        inscricaoService.cancelarMinha(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<InscricaoResponse> update(@PathVariable Integer id,
                                                    @RequestBody @Valid InscricaoRequest dto) {
        return ResponseEntity.ok(InscricaoResponse.fromEntity(inscricaoService.update(id, dto)));
    }
}
