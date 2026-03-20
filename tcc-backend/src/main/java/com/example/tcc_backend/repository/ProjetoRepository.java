package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoRepository extends JpaRepository <Projeto, Integer> {
}
