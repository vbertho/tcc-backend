package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Conversa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversaRepository extends JpaRepository<Conversa, Integer> {
    List<Conversa> findByProjetoIdIn(List<Integer> projetoIds);
    List<Conversa> findByProjetoOrientadorUsuarioIdOrProjetoAlunoCriadorUsuarioId(Integer orientadorUsuarioId, Integer alunoCriadorUsuarioId);
    Page<Conversa> findByProjetoOrientadorUsuarioIdOrProjetoAlunoCriadorUsuarioId(Integer orientadorUsuarioId, Integer alunoCriadorUsuarioId, Pageable pageable);
    Optional<Conversa> findByProjetoId(Integer projetoId);
}
