package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Progresso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressoRepository extends JpaRepository <Progresso, Integer> {
}
