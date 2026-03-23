package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Projeto;
import com.example.tcc_backend.model.StatusProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {
    List<Projeto> findByStatus(StatusProjeto status);
    List<Projeto> findByAreaId(Integer areaId);
    List<Projeto> findByTituloContainingIgnoreCase(String busca);
}