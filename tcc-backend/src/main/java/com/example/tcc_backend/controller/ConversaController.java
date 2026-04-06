package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.ConversaRequest;
import com.example.tcc_backend.dto.request.MensagemRequest;
import com.example.tcc_backend.dto.response.ConversaResponse;
import com.example.tcc_backend.dto.response.MensagemResponse;
import com.example.tcc_backend.dto.response.PageResponse;
import com.example.tcc_backend.service.ConversaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversas")
@RequiredArgsConstructor
public class ConversaController {

    private final ConversaService conversaService;

    @PostMapping
    public ResponseEntity<ConversaResponse> criar(@RequestBody @Valid ConversaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ConversaResponse.fromEntity(conversaService.criar(dto.getProjetoId())));
    }

    @PostMapping("/projeto/{projetoId}/abrir")
    public ResponseEntity<ConversaResponse> abrirPorProjeto(@PathVariable Integer projetoId) {
        return ResponseEntity.ok(ConversaResponse.fromEntity(conversaService.abrirOuCriarPorProjeto(projetoId)));
    }

    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<ConversaResponse> buscarPorProjeto(@PathVariable Integer projetoId) {
        return ResponseEntity.ok(ConversaResponse.fromEntity(conversaService.buscarPorProjeto(projetoId)));
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<ConversaResponse>> listarConversas(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(conversaService.listarConversasDoUsuario(usuarioId).stream().map(ConversaResponse::fromEntity).toList());
    }

    @GetMapping("/{usuarioId}/pagina")
    public ResponseEntity<PageResponse<ConversaResponse>> listarConversasPaginadas(
            @PathVariable Integer usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCriacao") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(
                PageResponse.from(
                        conversaService.listarConversasDoUsuario(usuarioId, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort)))
                                .map(ConversaResponse::fromEntity)
                )
        );
    }

    @GetMapping("/{id}/mensagens")
    public ResponseEntity<List<MensagemResponse>> listarMensagens(@PathVariable Integer id) {
        return ResponseEntity.ok(conversaService.listarMensagens(id).stream().map(MensagemResponse::fromEntity).toList());
    }

    @GetMapping("/{id}/mensagens/pagina")
    public ResponseEntity<PageResponse<MensagemResponse>> listarMensagensPaginadas(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataEnvio") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(
                PageResponse.from(
                        conversaService.listarMensagens(id, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort)))
                                .map(MensagemResponse::fromEntity)
                )
        );
    }

    @PostMapping("/{id}/mensagem")
    public ResponseEntity<MensagemResponse> enviarMensagem(@PathVariable Integer id,
                                                           @RequestBody @Valid MensagemRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(MensagemResponse.fromEntity(conversaService.enviarMensagem(id, dto.getConteudo())));
    }
}
