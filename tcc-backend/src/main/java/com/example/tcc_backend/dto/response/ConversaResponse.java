package com.example.tcc_backend.dto.response;

import com.example.tcc_backend.model.Conversa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversaResponse {

    private Integer id;
    private LocalDateTime dataCriacao;
    private Integer projetoId;
    private String projetoTitulo;
    private Integer orientadorId;
    private String orientadorNome;
    private Integer alunoCriadorId;
    private String alunoCriadorNome;

    public static ConversaResponse fromEntity(Conversa conversa) {
        return ConversaResponse.builder()
                .id(conversa.getId())
                .dataCriacao(conversa.getDataCriacao())
                .projetoId(conversa.getProjeto() != null ? conversa.getProjeto().getId() : null)
                .projetoTitulo(conversa.getProjeto() != null ? conversa.getProjeto().getTitulo() : null)
                .orientadorId(conversa.getProjeto() != null && conversa.getProjeto().getOrientador() != null ? conversa.getProjeto().getOrientador().getUsuario().getId() : null)
                .orientadorNome(conversa.getProjeto() != null && conversa.getProjeto().getOrientador() != null ? conversa.getProjeto().getOrientador().getUsuario().getNome() : null)
                .alunoCriadorId(conversa.getProjeto() != null && conversa.getProjeto().getAlunoCriador() != null ? conversa.getProjeto().getAlunoCriador().getUsuario().getId() : null)
                .alunoCriadorNome(conversa.getProjeto() != null && conversa.getProjeto().getAlunoCriador() != null ? conversa.getProjeto().getAlunoCriador().getUsuario().getNome() : null)
                .build();
    }
}
