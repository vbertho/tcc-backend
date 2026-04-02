package com.example.tcc_backend.controller;

import com.example.tcc_backend.model.Documento;
import com.example.tcc_backend.model.TipoDocumento;
import com.example.tcc_backend.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    @PostMapping("/upload")
    public ResponseEntity<Documento> upload(@RequestParam("tipo") TipoDocumento tipo,
                                            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoService.upload(tipo, arquivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        documentoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}

