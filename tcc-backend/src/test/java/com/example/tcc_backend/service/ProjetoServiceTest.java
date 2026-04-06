package com.example.tcc_backend.service;

import com.example.tcc_backend.dto.request.ProjetoRequest;
import com.example.tcc_backend.model.*;
import com.example.tcc_backend.repository.AlunoRepository;
import com.example.tcc_backend.repository.AreaPesquisaRepository;
import com.example.tcc_backend.repository.InscricaoRepository;
import com.example.tcc_backend.repository.OrientadorRepository;
import com.example.tcc_backend.repository.ProjetoRepository;
import com.example.tcc_backend.repository.UsuarioRepository;
import com.example.tcc_backend.security.AuthHelper;
import com.example.tcc_backend.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;
    @Mock
    private OrientadorRepository orientadorRepository;
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private InscricaoRepository inscricaoRepository;
    @Mock
    private AreaPesquisaRepository areaPesquisaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AuthHelper authHelper;
    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private ProjetoService projetoService;

    @Test
    void createDeveCriarProjetoEInscricaoQuandoUsuarioForAluno() {
        Usuario usuario = TestDataFactory.usuarioAluno(1);
        Aluno aluno = TestDataFactory.aluno(1, usuario);
        AreaPesquisa area = AreaPesquisa.builder().id(3).nome("IA").build();
        ProjetoRequest request = requestProjeto();
        Projeto projetoSalvo = TestDataFactory.projetoComAlunoCriador(10, aluno);

        when(authHelper.getCurrentUser()).thenReturn(usuario);
        when(areaPesquisaRepository.findById(3)).thenReturn(Optional.of(area));
        when(alunoRepository.findByUsuarioId(1)).thenReturn(Optional.of(aluno));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoSalvo);
        when(inscricaoRepository.save(any(Inscricao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Projeto projeto = projetoService.create(request);

        assertThat(projeto.getId()).isEqualTo(10);
        verify(inscricaoRepository).save(any(Inscricao.class));
    }

    @Test
    void updateDeveNegarUsuarioSemPermissao() {
        Usuario outro = TestDataFactory.usuarioAluno(9);
        Projeto projeto = TestDataFactory.projetoComOrientador(10, TestDataFactory.orientador(2, TestDataFactory.usuarioOrientador(2)));

        when(authHelper.getCurrentUser()).thenReturn(outro);
        when(projetoRepository.findById(10)).thenReturn(Optional.of(projeto));

        assertThatThrownBy(() -> projetoService.update(10, requestProjeto()))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteDevePermitirAlunoCriador() {
        Usuario usuario = TestDataFactory.usuarioAluno(1);
        Projeto projeto = TestDataFactory.projetoComAlunoCriador(10, TestDataFactory.aluno(1, usuario));

        when(authHelper.getCurrentUser()).thenReturn(usuario);
        when(projetoRepository.findById(10)).thenReturn(Optional.of(projeto));

        projetoService.delete(10);

        verify(projetoRepository).delete(projeto);
    }

    @Test
    void recrutarDeveAprovarAlunoECriarNotificacao() {
        Usuario gestor = TestDataFactory.usuarioOrientador(2);
        Projeto projeto = TestDataFactory.projetoComOrientador(10, TestDataFactory.orientador(2, gestor));
        Usuario alunoUsuario = TestDataFactory.usuarioAluno(3);
        Aluno aluno = TestDataFactory.aluno(3, alunoUsuario);

        when(authHelper.getCurrentUser()).thenReturn(gestor);
        when(projetoRepository.findById(10)).thenReturn(Optional.of(projeto));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(alunoUsuario));
        when(alunoRepository.findByUsuarioId(3)).thenReturn(Optional.of(aluno));
        when(inscricaoRepository.findByProjetoIdAndAlunoUsuarioId(10, 3)).thenReturn(Optional.empty());
        when(inscricaoRepository.save(any(Inscricao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inscricao inscricao = projetoService.recrutar(10, 3);

        assertThat(inscricao.getStatus()).isEqualTo(StatusInscricao.APROVADO);
        verify(notificacaoService).criarNotificacao(
                3,
                "Voce foi recrutado para um projeto",
                TipoNotificacao.INSCRICAO_APROVADA,
                "PROJETO",
                10,
                "/projetos/10",
                "Projeto 10"
        );
    }

    @Test
    void listarColaboradoresDeveRetornarUsuariosSemDuplicar() {
        Usuario orientadorUsuario = TestDataFactory.usuarioOrientador(2);
        Usuario alunoUsuario = TestDataFactory.usuarioAluno(1);
        Projeto projeto = TestDataFactory.projetoComOrientador(10, TestDataFactory.orientador(2, orientadorUsuario));
        projeto.setAlunoCriador(TestDataFactory.aluno(1, alunoUsuario));
        Inscricao inscricao = TestDataFactory.inscricaoAprovada(5, TestDataFactory.aluno(1, alunoUsuario), projeto);

        when(projetoRepository.findById(10)).thenReturn(Optional.of(projeto));
        when(inscricaoRepository.findByProjetoIdAndStatus(10, StatusInscricao.APROVADO)).thenReturn(List.of(inscricao));

        List<Usuario> colaboradores = projetoService.listarColaboradores(10);

        assertThat(colaboradores).hasSize(2);
    }

    @Test
    void removerColaboradorDeveNegarRemocaoDoAlunoCriador() {
        Usuario usuario = TestDataFactory.usuarioAluno(1);
        Projeto projeto = TestDataFactory.projetoComAlunoCriador(10, TestDataFactory.aluno(1, usuario));

        when(authHelper.getCurrentUser()).thenReturn(usuario);
        when(projetoRepository.findById(10)).thenReturn(Optional.of(projeto));

        assertThatThrownBy(() -> projetoService.removerColaborador(10, 1))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findByBuscaDeveDelegarAoRepositorio() {
        when(projetoRepository.findByTituloContainingIgnoreCase("java")).thenReturn(List.of());

        List<Projeto> projetos = projetoService.findByBusca("java");

        assertThat(projetos).isEmpty();
    }

    private ProjetoRequest requestProjeto() {
        ProjetoRequest request = new ProjetoRequest();
        request.setTitulo("Projeto");
        request.setDescricao("Descricao");
        request.setRequisitos("Java");
        request.setVagas(2);
        request.setAreaId(3);
        return request;
    }
}
