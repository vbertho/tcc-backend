package com.example.tcc_backend.service;

import com.example.tcc_backend.dto.request.ProjetoRequest;
import com.example.tcc_backend.model.*;
import com.example.tcc_backend.repository.AlunoRepository;
import com.example.tcc_backend.repository.AreaPesquisaRepository;
import com.example.tcc_backend.repository.OrientadorRepository;
import com.example.tcc_backend.repository.ProjetoRepository;
import com.example.tcc_backend.security.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final OrientadorRepository orientadorRepository;
    private final AlunoRepository alunoRepository;
    private final AreaPesquisaRepository areaPesquisaRepository;
    private final AuthHelper authHelper;

    public List<Projeto> findAll() {
        return projetoRepository.findAll();
    }

    public Projeto findById(Integer id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
    }

    public List<Projeto> findByStatus(StatusProjeto status) {
        return projetoRepository.findByStatus(status);
    }

    public List<Projeto> findByArea(Integer areaId) {
        return projetoRepository.findByAreaId(areaId);
    }

    public List<Projeto> findByBusca(String busca) {
        return projetoRepository.findByTituloContainingIgnoreCase(busca);
    }

    public Projeto create(ProjetoRequest dto) {
        Usuario usuarioLogado = authHelper.getCurrentUser();

        AreaPesquisa area = areaPesquisaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área não encontrada"));

        Projeto.ProjetoBuilder builder = Projeto.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .requisitos(dto.getRequisitos())
                .vagas(dto.getVagas())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .dataLimiteInscricao(dto.getDataLimiteInscricao())
                .area(area);

        if (usuarioLogado.getTipo() == TipoUsuario.ORIENTADOR) {
            Orientador orientador = orientadorRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orientador não encontrado"));
            builder.orientador(orientador);
        } else {
            Aluno aluno = alunoRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
            builder.alunoCriador(aluno);
        }

        return projetoRepository.save(builder.build());
    }

    public Projeto update(Integer id, ProjetoRequest dto) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Projeto projeto = findById(id);

        boolean isOrientadorDoProjeto = projeto.getOrientador() != null &&
                projeto.getOrientador().getUsuario().getId().equals(usuarioLogado.getId());

        boolean isAlunoCriador = projeto.getAlunoCriador() != null &&
                projeto.getAlunoCriador().getUsuario().getId().equals(usuarioLogado.getId());

        if (!isOrientadorDoProjeto && !isAlunoCriador) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para editar este projeto");
        }

        AreaPesquisa area = areaPesquisaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área não encontrada"));

        Projeto atualizado = Projeto.builder()
                .id(projeto.getId())
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .requisitos(dto.getRequisitos())
                .vagas(dto.getVagas())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .dataLimiteInscricao(dto.getDataLimiteInscricao())
                .area(area)
                .orientador(projeto.getOrientador())
                .alunoCriador(projeto.getAlunoCriador())
                .dataCriacao(projeto.getDataCriacao())
                .status(projeto.getStatus())
                .build();

        return projetoRepository.save(atualizado);
    }

    public void delete(Integer id) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Projeto projeto = findById(id);

        boolean isOrientadorDoProjeto = projeto.getOrientador() != null &&
                projeto.getOrientador().getUsuario().getId().equals(usuarioLogado.getId());

        boolean isAlunoCriador = projeto.getAlunoCriador() != null &&
                projeto.getAlunoCriador().getUsuario().getId().equals(usuarioLogado.getId());

        if (!isOrientadorDoProjeto && !isAlunoCriador) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este projeto");
        }

        projetoRepository.delete(projeto);
    }
}