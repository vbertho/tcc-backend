package com.example.tcc_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conversa")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_projeto", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Projeto projeto;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}
