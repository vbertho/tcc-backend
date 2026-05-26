package com.example.tcc_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Operation(summary = "Verificar disponibilidade do servico")
    @ApiResponse(responseCode = "200", description = "Servico disponivel")
    @GetMapping
    public ResponseEntity<Void> ping() {
        return ResponseEntity.ok().build();
    }
}
