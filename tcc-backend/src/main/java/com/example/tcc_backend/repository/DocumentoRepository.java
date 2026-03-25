package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {
    List<Documento> findByUsuarioId(Integer usuarioId);
}
