package com.example.tcc_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoRequest {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String descricao;
    private String requisitos;

    @NotNull(message = "Vagas é obrigatório")
    private Integer vagas;

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalDate dataLimiteInscricao;

    @NotNull(message = "Área é obrigatória")
    private Integer areaId;
}
