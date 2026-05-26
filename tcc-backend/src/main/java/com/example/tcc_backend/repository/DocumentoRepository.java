package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Documento;
import com.example.tcc_backend.model.StatusDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {
    List<Documento> findByUsuarioId(Integer usuarioId);
    long countByUsuarioId(Integer usuarioId);
    Page<Documento> findByStatus(StatusDocumento status, Pageable pageable);
    long countByStatus(StatusDocumento status);
}
