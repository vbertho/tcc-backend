package com.example.tcc_backend.dto.response;

import com.example.tcc_backend.model.Progresso;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProgressoResponse {
    private Integer id;
    private Integer projetoId;
    private String descricao;
    private LocalDateTime dataRegistro;
    private Integer autorId;
    private String autorNome;

    public static ProgressoResponse fromEntity(Progresso progresso) {
        return ProgressoResponse.builder()
                .id(progresso.getId())
                .projetoId(progresso.getProjeto().getId())
                .descricao(progresso.getDescricao())
                .dataRegistro(progresso.getDataRegistro())
                .autorId(progresso.getAutor() != null ? progresso.getAutor().getId() : null)
                .autorNome(progresso.getAutor() != null ? progresso.getAutor().getNome() : null)
                .build();
    }
}
