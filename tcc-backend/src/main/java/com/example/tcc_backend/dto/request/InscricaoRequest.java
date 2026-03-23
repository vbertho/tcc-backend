package com.example.tcc_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscricaoRequest {

    @NotNull(message = "Projeto é obrigatório")
    private Integer projetoId;
}