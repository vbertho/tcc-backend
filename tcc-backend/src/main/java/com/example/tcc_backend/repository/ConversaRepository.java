package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Conversa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversaRepository extends JpaRepository<Conversa, Integer> {
}
