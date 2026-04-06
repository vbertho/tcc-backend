package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.response.NotificacaoResponse;
import com.example.tcc_backend.dto.response.PageResponse;
import com.example.tcc_backend.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<NotificacaoResponse>> listarMinhas() {
        return ResponseEntity.ok(notificacaoService.minhasNotificacoes().stream().map(NotificacaoResponse::fromEntity).toList());
    }

    @GetMapping("/pagina")
    public ResponseEntity<PageResponse<NotificacaoResponse>> listarMinhasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCriacao") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(
                PageResponse.from(
                        notificacaoService.minhasNotificacoes(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort)))
                                .map(NotificacaoResponse::fromEntity)
                )
        );
    }

    @PutMapping("/{id}/ler")
    public ResponseEntity<NotificacaoResponse> marcarComoLida(@PathVariable Integer id) {
        return ResponseEntity.ok(NotificacaoResponse.fromEntity(notificacaoService.marcarComoLida(id)));
    }

    @PutMapping("/ler-todas")
    public ResponseEntity<Void> marcarTodasComoLidas() {
        notificacaoService.marcarTodasComoLidas();
        return ResponseEntity.noContent().build();
    }
}
