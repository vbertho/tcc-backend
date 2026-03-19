package com.example.tcc_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscricao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscricao")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusInscricao status;

    @Column(name = "data_inscricao")
    private LocalDateTime dataInscricao;

    @PrePersist
    public void prePersist() {
        this.dataInscricao = LocalDateTime.now();
        this.status = StatusInscricao.PENDENTE;
    }
}
