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
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/conversas")
@RequiredArgsConstructor
@Tag(name = "Conversas", description = "Endpoints de mensagens e comunicação entre usuários")
public class ConversaController {

    private final ConversaService conversaService;

    @Operation(summary = "Criar conversa", description = "Cria uma nova conversa vinculada a um projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conversa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ConversaResponse> criar(@RequestBody @Valid ConversaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ConversaResponse.fromEntity(conversaService.criar(dto.getProjetoId())));
    }

    @Operation(summary = "Abrir ou criar conversa por projeto", description = "Abre uma conversa existente ou cria uma nova para o projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversa retornada com sucesso")
    })
    @PostMapping("/projeto/{projetoId}/abrir")
    public ResponseEntity<ConversaResponse> abrirPorProjeto(@PathVariable Integer projetoId) {
        return ResponseEntity.ok(
                ConversaResponse.fromEntity(conversaService.abrirOuCriarPorProjeto(projetoId))
        );
    }

    @Operation(summary = "Buscar conversa por projeto", description = "Retorna a conversa associada a um projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversa encontrada"),
            @ApiResponse(responseCode = "404", description = "Conversa não encontrada")
    })
    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<ConversaResponse> buscarPorProjeto(@PathVariable Integer projetoId) {
        return ResponseEntity.ok(
                ConversaResponse.fromEntity(conversaService.buscarPorProjeto(projetoId))
        );
    }

    @Operation(summary = "Listar conversas do usuário", description = "Retorna todas as conversas de um usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<ConversaResponse>> listarConversas(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(
                conversaService.listarConversasDoUsuario(usuarioId)
                        .stream().map(ConversaResponse::fromEntity).toList()
        );
    }

    @Operation(summary = "Listar conversas paginadas", description = "Retorna conversas do usuário com paginação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    })
    @GetMapping("/{usuarioId}/pagina")
    public ResponseEntity<PageResponse<ConversaResponse>> listarConversasPaginadas(
            @PathVariable Integer usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCriacao") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        return ResponseEntity.ok(
                PageResponse.from(
                        conversaService.listarConversasDoUsuario(
                                usuarioId,
                                PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
                        ).map(ConversaResponse::fromEntity)
                )
        );
    }

    @Operation(summary = "Listar mensagens", description = "Retorna todas as mensagens de uma conversa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensagens retornadas com sucesso")
    })
    @GetMapping("/{id}/mensagens")
    public ResponseEntity<List<MensagemResponse>> listarMensagens(@PathVariable Integer id) {
        return ResponseEntity.ok(
                conversaService.listarMensagens(id)
                        .stream().map(MensagemResponse::fromEntity).toList()
        );
    }

    @Operation(summary = "Listar mensagens paginadas", description = "Retorna mensagens de uma conversa com paginação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    })
    @GetMapping("/{id}/mensagens/pagina")
    public ResponseEntity<PageResponse<MensagemResponse>> listarMensagensPaginadas(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataEnvio") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        return ResponseEntity.ok(
                PageResponse.from(
                        conversaService.listarMensagens(
                                id,
                                PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
                        ).map(MensagemResponse::fromEntity)
                )
        );
    }

    @Operation(summary = "Enviar mensagem", description = "Envia uma nova mensagem em uma conversa.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mensagem enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/{id}/mensagem")
    public ResponseEntity<MensagemResponse> enviarMensagem(@PathVariable Integer id,
                                                           @RequestBody @Valid MensagemRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MensagemResponse.fromEntity(
                        conversaService.enviarMensagem(id, dto.getConteudo())
                ));
    }
}