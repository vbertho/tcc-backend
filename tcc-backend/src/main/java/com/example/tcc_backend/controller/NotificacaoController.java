package com.example.tcc_backend.controller;

import com.example.tcc_backend.model.Notificacao;
import com.example.tcc_backend.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<Notificacao>> listarMinhas() {
        return ResponseEntity.ok(notificacaoService.minhasNotificacoes());
    }

    @PutMapping("/{id}/ler")
    public ResponseEntity<Notificacao> marcarComoLida(@PathVariable Integer id) {
        return ResponseEntity.ok(notificacaoService.marcarComoLida(id));
    }

    @PutMapping("/ler-todas")
    public ResponseEntity<Void> marcarTodasComoLidas() {
        notificacaoService.marcarTodasComoLidas();
        return ResponseEntity.noContent().build();
    }
}
