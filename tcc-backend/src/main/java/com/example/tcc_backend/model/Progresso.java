package com.example.tcc_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "progresso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_progresso")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_projeto", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Projeto projeto;

    @NotBlank
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_registro")
    private LocalDateTime dataRegistro;

    @PrePersist
    public void prePersist() {
        this.dataRegistro = LocalDateTime.now();
    }
}
