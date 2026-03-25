package com.example.tcc_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensagem")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_conversa", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Conversa conversa;

    @ManyToOne
    @JoinColumn(name = "id_remetente", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario remetente;

    @NotBlank
    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @PrePersist
    public void prePersist() {
        this.dataEnvio = LocalDateTime.now();
    }
}
