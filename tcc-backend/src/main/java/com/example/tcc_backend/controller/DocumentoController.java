package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.response.DocumentoResponse;
import com.example.tcc_backend.model.TipoDocumento;
import com.example.tcc_backend.service.DocumentoService;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@Tag(name = "Documentos", description = "Endpoints para gerenciamento de arquivos")
public class DocumentoController {

    private final DocumentoService documentoService;

    @Operation(summary = "Upload de documento", description = "Realiza upload de um arquivo para um usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Arquivo enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/upload")
    public ResponseEntity<DocumentoResponse> upload(
            @RequestParam("usuarioId") Integer id_usuario,
            @RequestParam("tipo") TipoDocumento tipo,
            @RequestParam("arquivo") MultipartFile arquivo) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DocumentoResponse.fromEntity(documentoService.upload(id_usuario, tipo, arquivo)));
    }

    @Operation(summary = "Download de documento", description = "Realiza o download de um arquivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Download realizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Integer id) {
        Path arquivo = documentoService.obterArquivo(id);
        Resource resource = new FileSystemResource(arquivo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(arquivo.getFileName().toString()).build().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "Preview de documento", description = "Visualiza o arquivo diretamente no navegador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preview gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    })
    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> preview(@PathVariable Integer id) {
        Path arquivo = documentoService.obterArquivo(id);
        Resource resource = new FileSystemResource(arquivo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(arquivo.getFileName().toString()).build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @Operation(summary = "Listar documentos do usuário", description = "Retorna todos os documentos de um usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DocumentoResponse>> listarDoUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(
                documentoService.listarPorUsuario(usuarioId)
                        .stream()
                        .map(DocumentoResponse::fromEntity)
                        .toList()
        );
    }

    @Operation(summary = "Remover documento", description = "Remove um documento do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Documento removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        documentoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}