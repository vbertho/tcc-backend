package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.ConversaRequest;
import com.example.tcc_backend.dto.request.MensagemRequest;
import com.example.tcc_backend.model.Conversa;
import com.example.tcc_backend.model.Mensagem;
import com.example.tcc_backend.service.ConversaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/conversas")
@RequiredArgsConstructor
public class ConversaController {

    private final ConversaService conversaService;

    @PostMapping
    public ResponseEntity<Conversa> criar(@RequestBody @Valid ConversaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conversaService.criar(dto.getProjetoId()));
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<Conversa>> listarConversas(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(conversaService.listarConversasDoUsuario(usuarioId));
    }

    @GetMapping("/{id}/mensagens")
    public ResponseEntity<List<Mensagem>> listarMensagens(@PathVariable Integer id) {
        return ResponseEntity.ok(conversaService.listarMensagens(id));
    }

    @PostMapping("/{id}/mensagem")
    public ResponseEntity<Mensagem> enviarMensagem(@PathVariable Integer id,
                                                   @RequestBody @Valid MensagemRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conversaService.enviarMensagem(id, dto.getConteudo()));
    }
}
