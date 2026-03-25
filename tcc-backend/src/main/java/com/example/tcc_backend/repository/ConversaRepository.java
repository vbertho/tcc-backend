package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Conversa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversaRepository extends JpaRepository<Conversa, Integer> {
    List<Conversa> findByProjetoIdIn(List<Integer> projetoIds);
    List<Conversa> findByProjetoOrientadorUsuarioIdOrProjetoAlunoCriadorUsuarioId(Integer orientadorUsuarioId, Integer alunoCriadorUsuarioId);
}
