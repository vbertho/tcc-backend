package com.example.tcc_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProgressoRequest {
    @NotBlank(message = "Descricao e obrigatoria")
    private String descricao;
}
