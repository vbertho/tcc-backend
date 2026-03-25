package com.example.tcc_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MensagemRequest {
    @NotBlank(message = "Conteudo e obrigatorio")
    private String conteudo;
}
