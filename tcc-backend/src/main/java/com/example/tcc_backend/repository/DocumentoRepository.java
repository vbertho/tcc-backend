package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {
}
