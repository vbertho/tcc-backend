package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Integer> {
    List<Inscricao> findByAlunoId(Integer alunoId);
    List<Inscricao> findByProjetoId(Integer projetoId);
    boolean existsByAlunoIdAndProjetoId(Integer alunoId, Integer projetoId);
}