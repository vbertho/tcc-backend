package com.example.tcc_backend.service;

import com.example.tcc_backend.dto.request.InscricaoRequest;
import com.example.tcc_backend.model.*;
import com.example.tcc_backend.repository.AlunoRepository;
import com.example.tcc_backend.repository.InscricaoRepository;
import com.example.tcc_backend.repository.ProjetoRepository;
import com.example.tcc_backend.security.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final AlunoRepository alunoRepository;
    private final ProjetoRepository projetoRepository;
    private final AuthHelper authHelper;

    public List<Inscricao> findAll() {
        return inscricaoRepository.findAll();
    }

    public Inscricao findById(Integer id) {
        return inscricaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscrição não encontrada"));
    }

    public List<Inscricao> findByProjeto(Integer projetoId) {
        return inscricaoRepository.findByProjetoId(projetoId);
    }

    public Inscricao create(InscricaoRequest dto) {
        Usuario usuarioLogado = authHelper.getCurrentUser();

        if (usuarioLogado.getTipo() != TipoUsuario.ALUNO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas alunos podem se inscrever em projetos");
        }

        Aluno aluno = alunoRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));

        Projeto projeto = projetoRepository.findById(dto.getProjetoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));

        if (inscricaoRepository.existsByAlunoIdAndProjetoId(aluno.getId(), projeto.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já está inscrito neste projeto");
        }

        if (projeto.getStatus() != StatusProjeto.ABERTO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este projeto não está aceitando inscrições");
        }

        Inscricao inscricao = Inscricao.builder()
                .aluno(aluno)
                .projeto(projeto)
                .build();

        return inscricaoRepository.save(inscricao);
    }

    public Inscricao aprovar(Integer id) {
        Inscricao inscricao = findById(id);
        validarOrientador(inscricao);

        Inscricao atualizada = Inscricao.builder()
                .id(inscricao.getId())
                .aluno(inscricao.getAluno())
                .projeto(inscricao.getProjeto())
                .status(StatusInscricao.APROVADO)
                .dataInscricao(inscricao.getDataInscricao())
                .build();

        return inscricaoRepository.save(atualizada);
    }

    public Inscricao rejeitar(Integer id) {
        Inscricao inscricao = findById(id);
        validarOrientador(inscricao);

        Inscricao atualizada = Inscricao.builder()
                .id(inscricao.getId())
                .aluno(inscricao.getAluno())
                .projeto(inscricao.getProjeto())
                .status(StatusInscricao.REJEITADO)
                .dataInscricao(inscricao.getDataInscricao())
                .build();

        return inscricaoRepository.save(atualizada);
    }

    public void cancel(Integer id) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Inscricao inscricao = findById(id);

        if (!inscricao.getAluno().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode cancelar a inscrição de outro aluno");
        }

        inscricaoRepository.delete(inscricao);
    }

    public Inscricao update(Integer id, InscricaoRequest dto) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Inscricao inscricao = findById(id);

        if (!inscricao.getAluno().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode editar a inscrição de outro aluno");
        }

        Projeto projeto = projetoRepository.findById(dto.getProjetoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));

        Inscricao atualizada = Inscricao.builder()
                .id(inscricao.getId())
                .aluno(inscricao.getAluno())
                .projeto(projeto)
                .status(inscricao.getStatus())
                .dataInscricao(inscricao.getDataInscricao())
                .build();

        return inscricaoRepository.save(atualizada);
    }

    private void validarOrientador(Inscricao inscricao) {
        Usuario usuarioLogado = authHelper.getCurrentUser();

        if (usuarioLogado.getTipo() != TipoUsuario.ORIENTADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas orientadores podem aprovar ou rejeitar inscrições");
        }

        boolean isOrientadorDoProjeto = inscricao.getProjeto().getOrientador() != null &&
                inscricao.getProjeto().getOrientador().getUsuario().getId().equals(usuarioLogado.getId());

        if (!isOrientadorDoProjeto) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não é o orientador deste projeto");
        }
    }
}