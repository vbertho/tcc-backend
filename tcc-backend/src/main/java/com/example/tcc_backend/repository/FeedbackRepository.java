package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByProjetoId(Integer projetoId);
    List<Feedback> findByProjetoOrientadorUsuarioIdOrProjetoAlunoCriadorUsuarioId(Integer orientadorUsuarioId, Integer alunoCriadorUsuarioId);
}
