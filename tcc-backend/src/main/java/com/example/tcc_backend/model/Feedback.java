package com.example.tcc_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_feedback")
    private Long id;

    @Min(1)
    @Max(5)
    @Column(name = "nota", nullable = false)
    private Integer nota;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;

    @ManyToOne
    @JoinColumn(name = "id_usuario_avaliador", nullable = false)
    private Usuario avaliador;

    @Column(name = "data_feedback")
    private LocalDateTime dataFeedback;

    @PrePersist
    public void prePersist() {
        this.dataFeedback = LocalDateTime.now();
    }
}
