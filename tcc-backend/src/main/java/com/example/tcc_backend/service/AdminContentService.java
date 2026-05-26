package com.example.tcc_backend.service;

import com.example.tcc_backend.dto.request.*;
import com.example.tcc_backend.dto.response.*;
import com.example.tcc_backend.model.*;
import com.example.tcc_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminContentService {

    private final UsuarioRepository usuarioRepository;
    private final ProjetoRepository projetoRepository;
    private final InscricaoRepository inscricaoRepository;
    private final DocumentoRepository documentoRepository;
    private final AreaPesquisaRepository areaPesquisaRepository;
    private final DocumentoService documentoService;
    private final AdminAccessService accessService;
    private final AdminAuditService auditService;

    public AdminDashboardResponse dashboard() {
        accessService.requireAdmin();
        return AdminDashboardResponse.builder()
                .totalUsuarios(usuarioRepository.count())
                .usuariosAtivos(usuarioRepository.countByAtivoTrue())
                .totalAlunos(usuarioRepository.countByTipo(TipoUsuario.ALUNO))
                .totalOrientadores(usuarioRepository.countByTipo(TipoUsuario.ORIENTADOR))
                .totalAdministradores(usuarioRepository.countByTipo(TipoUsuario.ADMIN))
                .totalProjetos(projetoRepository.count())
                .projetosAbertos(projetoRepository.countByStatus(StatusProjeto.ABERTO))
                .inscricoesPendentes(inscricaoRepository.countByStatus(StatusInscricao.PENDENTE))
                .documentosEmAnalise(documentoRepository.countByStatus(StatusDocumento.EM_ANALISE))
                .atividadesRecentes(auditService.list(0, 6).getContent())
                .build();
    }

    public PageResponse<ProjetoResponse> listProjetos(StatusProjeto status, int page, int size) {
        accessService.requireAdmin();
        validatePage(page, size);
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "dataCriacao"));
        Page<Projeto> result = status == null
                ? projetoRepository.findAll(pageable)
                : projetoRepository.findByStatus(status, pageable);
        return PageResponse.from(result.map(ProjetoResponse::fromEntity));
    }

    @Transactional
    public ProjetoResponse createProjeto(ProjetoRequest dto) {
        accessService.requireAdmin();
        AreaPesquisa area = getArea(dto.getAreaId());
        Projeto projeto = projetoRepository.save(Projeto.builder()
                .titulo(dto.getTitulo().trim())
                .descricao(text(dto.getDescricao()))
                .requisitos(text(dto.getRequisitos()))
                .vagas(dto.getVagas())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .dataLimiteInscricao(dto.getDataLimiteInscricao())
                .area(area)
                .build());
        auditService.record("CRIAR", "PROJETO", projeto.getId(), projeto.getTitulo());
        return ProjetoResponse.fromEntity(projeto);
    }

    @Transactional
    public ProjetoResponse updateProjeto(Integer id, ProjetoRequest dto) {
        accessService.requireAdmin();
        Projeto projeto = getProjeto(id);
        projeto.setTitulo(dto.getTitulo().trim());
        projeto.setDescricao(text(dto.getDescricao()));
        projeto.setRequisitos(text(dto.getRequisitos()));
        projeto.setVagas(dto.getVagas());
        projeto.setDataInicio(dto.getDataInicio());
        projeto.setDataFim(dto.getDataFim());
        projeto.setDataLimiteInscricao(dto.getDataLimiteInscricao());
        projeto.setArea(getArea(dto.getAreaId()));
        projetoRepository.save(projeto);
        auditService.record("ATUALIZAR", "PROJETO", id, projeto.getTitulo());
        return ProjetoResponse.fromEntity(projeto);
    }

    @Transactional
    public ProjetoResponse setProjetoStatus(Integer id, AdminProjetoStatusRequest dto) {
        accessService.requireAdmin();
        Projeto projeto = getProjeto(id);
        projeto.setStatus(dto.getStatus());
        projetoRepository.save(projeto);
        auditService.record("ALTERAR_STATUS", "PROJETO", id, dto.getStatus().name());
        return ProjetoResponse.fromEntity(projeto);
    }

    @Transactional
    public void deleteProjeto(Integer id) {
        accessService.requireAdmin();
        Projeto projeto = getProjeto(id);
        projetoRepository.delete(projeto);
        auditService.record("REMOVER", "PROJETO", id, projeto.getTitulo());
    }

    public PageResponse<InscricaoResponse> listInscricoes(StatusInscricao status, int page, int size) {
        accessService.requireAdmin();
        validatePage(page, size);
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "dataInscricao"));
        Page<Inscricao> result = status == null
                ? inscricaoRepository.findAll(pageable)
                : inscricaoRepository.findByStatus(status, pageable);
        return PageResponse.from(result.map(InscricaoResponse::fromEntity));
    }

    @Transactional
    public InscricaoResponse setInscricaoStatus(Integer id, AdminInscricaoStatusRequest dto) {
        accessService.requireAdmin();
        Inscricao inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscricao nao encontrada"));
        inscricao.setStatus(dto.getStatus());
        inscricao.setParecerOrientador(text(dto.getParecer()));
        inscricaoRepository.save(inscricao);
        auditService.record("ALTERAR_STATUS", "INSCRICAO", id, dto.getStatus().name());
        return InscricaoResponse.fromEntity(inscricao);
    }

    @Transactional
    public void deleteInscricao(Integer id) {
        accessService.requireAdmin();
        Inscricao inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscricao nao encontrada"));
        inscricaoRepository.delete(inscricao);
        auditService.record("REMOVER", "INSCRICAO", id, inscricao.getProjeto().getTitulo());
    }

    public PageResponse<DocumentoResponse> listDocumentos(StatusDocumento status, int page, int size) {
        accessService.requireAdmin();
        validatePage(page, size);
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "dataEnvio"));
        Page<Documento> result = status == null
                ? documentoRepository.findAll(pageable)
                : documentoRepository.findByStatus(status, pageable);
        return PageResponse.from(result.map(DocumentoResponse::fromEntity));
    }

    @Transactional
    public DocumentoResponse setDocumentoStatus(Integer id, AdminDocumentoStatusRequest dto) {
        accessService.requireAdmin();
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento nao encontrado"));
        documento.setStatus(dto.getStatus());
        documento.setObservacaoStatus(text(dto.getObservacao()));
        documentoRepository.save(documento);
        auditService.record("ALTERAR_STATUS", "DOCUMENTO", id, dto.getStatus().name());
        return DocumentoResponse.fromEntity(documento);
    }

    @Transactional
    public void deleteDocumento(Integer id) {
        accessService.requireAdmin();
        documentoService.remover(id);
        auditService.record("REMOVER", "DOCUMENTO", id, "Documento removido");
    }

    public AdminReportResponse report() {
        accessService.requireAdmin();
        Map<String, Long> usuarios = new LinkedHashMap<>();
        for (TipoUsuario tipo : TipoUsuario.values()) {
            usuarios.put(tipo.name(), usuarioRepository.countByTipo(tipo));
        }
        Map<String, Long> projetos = new LinkedHashMap<>();
        for (StatusProjeto status : StatusProjeto.values()) {
            projetos.put(status.name(), projetoRepository.countByStatus(status));
        }
        Map<String, Long> inscricoes = new LinkedHashMap<>();
        for (StatusInscricao status : StatusInscricao.values()) {
            inscricoes.put(status.name(), inscricaoRepository.countByStatus(status));
        }
        Map<String, Long> documentos = new LinkedHashMap<>();
        for (StatusDocumento status : StatusDocumento.values()) {
            documentos.put(status.name(), documentoRepository.countByStatus(status));
        }
        return AdminReportResponse.builder()
                .geradoEm(LocalDateTime.now())
                .usuariosPorTipo(usuarios)
                .projetosPorStatus(projetos)
                .inscricoesPorStatus(inscricoes)
                .documentosPorStatus(documentos)
                .build();
    }

    private Projeto getProjeto(Integer id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto nao encontrado"));
    }

    private AreaPesquisa getArea(Integer id) {
        return areaPesquisaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Area nao encontrada"));
    }

    private String text(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginacao invalida");
        }
    }
}
