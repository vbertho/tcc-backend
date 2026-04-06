package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.ProjetoRequest;
import com.example.tcc_backend.dto.request.RecrutarColaboradorRequest;
import com.example.tcc_backend.dto.response.InscricaoResponse;
import com.example.tcc_backend.dto.response.PageResponse;
import com.example.tcc_backend.dto.response.ProjetoResponse;
import com.example.tcc_backend.dto.response.UsuarioResponse;
import com.example.tcc_backend.model.StatusProjeto;
import com.example.tcc_backend.service.ProjetoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;

    @GetMapping
    public ResponseEntity<List<ProjetoResponse>> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer areaId,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String busca) {

        List<ProjetoResponse> response;
        if (status != null) {
            try {
                response = projetoService.findByStatus(StatusProjeto.valueOf(status.toUpperCase())).stream().map(ProjetoResponse::fromEntity).toList();
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status invalido");
            }
            return ResponseEntity.ok(response);
        }
        if (areaId != null) return ResponseEntity.ok(projetoService.findByArea(areaId).stream().map(ProjetoResponse::fromEntity).toList());
        if (area != null) return ResponseEntity.ok(projetoService.findByAreaNome(area).stream().map(ProjetoResponse::fromEntity).toList());
        if (curso != null) return ResponseEntity.ok(projetoService.findByCursoNome(curso).stream().map(ProjetoResponse::fromEntity).toList());
        if (busca != null) return ResponseEntity.ok(projetoService.findByBusca(busca).stream().map(ProjetoResponse::fromEntity).toList());

        return ResponseEntity.ok(projetoService.findAll().stream().map(ProjetoResponse::fromEntity).toList());
    }

    @GetMapping("/pagina")
    public ResponseEntity<PageResponse<ProjetoResponse>> findAllPaginado(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer areaId,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false, defaultValue = "false") Boolean meusProjetos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCriacao") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));

        if (Boolean.TRUE.equals(meusProjetos)) {
            return ResponseEntity.ok(PageResponse.from(projetoService.findMeusProjetos(pageable).map(ProjetoResponse::fromEntity)));
        }
        if (status != null) {
            try {
                return ResponseEntity.ok(PageResponse.from(projetoService.findByStatus(StatusProjeto.valueOf(status.toUpperCase()), pageable).map(ProjetoResponse::fromEntity)));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status invalido");
            }
        }
        if (areaId != null) return ResponseEntity.ok(PageResponse.from(projetoService.findByArea(areaId, pageable).map(ProjetoResponse::fromEntity)));
        if (area != null) return ResponseEntity.ok(PageResponse.from(projetoService.findByAreaNome(area, pageable).map(ProjetoResponse::fromEntity)));
        if (curso != null) return ResponseEntity.ok(PageResponse.from(projetoService.findByCursoNome(curso, pageable).map(ProjetoResponse::fromEntity)));
        if (busca != null) return ResponseEntity.ok(PageResponse.from(projetoService.findByBusca(busca, pageable).map(ProjetoResponse::fromEntity)));

        return ResponseEntity.ok(PageResponse.from(projetoService.findAll(pageable).map(ProjetoResponse::fromEntity)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ProjetoResponse.fromEntity(projetoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ProjetoResponse> create(@RequestBody @Valid ProjetoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjetoResponse.fromEntity(projetoService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoResponse> update(@PathVariable Integer id,
                                                  @RequestBody @Valid ProjetoRequest dto) {
        return ResponseEntity.ok(ProjetoResponse.fromEntity(projetoService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        projetoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/recrutar")
    public ResponseEntity<InscricaoResponse> recrutar(@PathVariable Integer id,
                                                      @RequestBody @Valid RecrutarColaboradorRequest dto) {
        return ResponseEntity.ok(InscricaoResponse.fromEntity(projetoService.recrutar(id, dto.getUsuarioId())));
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
