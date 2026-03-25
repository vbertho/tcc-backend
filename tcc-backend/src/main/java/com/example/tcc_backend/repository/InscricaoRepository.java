package com.example.tcc_backend.repository;

import com.example.tcc_backend.model.Inscricao;
import com.example.tcc_backend.model.StatusInscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Integer> {
    List<Inscricao> findByAlunoId(Integer alunoId);
    List<Inscricao> findByAlunoUsuarioId(Integer usuarioId);
    List<Inscricao> findByAlunoUsuarioIdAndStatus(Integer usuarioId, StatusInscricao status);
    List<Inscricao> findByProjetoId(Integer projetoId);
    List<Inscricao> findByProjetoIdAndStatus(Integer projetoId, StatusInscricao status);
    Optional<Inscricao> findByProjetoIdAndAlunoUsuarioId(Integer projetoId, Integer usuarioId);
    List<Inscricao> findByProjetoOrientadorUsuarioId(Integer usuarioId);
    boolean existsByAlunoIdAndProjetoId(Integer alunoId, Integer projetoId);
}
