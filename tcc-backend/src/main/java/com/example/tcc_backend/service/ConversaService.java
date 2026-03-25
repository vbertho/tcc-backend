package com.example.tcc_backend.service;

import com.example.tcc_backend.model.*;
import com.example.tcc_backend.repository.ConversaRepository;
import com.example.tcc_backend.repository.InscricaoRepository;
import com.example.tcc_backend.repository.MensagemRepository;
import com.example.tcc_backend.repository.ProjetoRepository;
import com.example.tcc_backend.security.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConversaService {

    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final ProjetoRepository projetoRepository;
    private final InscricaoRepository inscricaoRepository;
    private final AuthHelper authHelper;
    private final NotificacaoService notificacaoService;

    public Conversa criar(Integer projetoId) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto nao encontrado"));
        validarParticipacaoProjeto(projeto, usuarioLogado.getId());

        Conversa conversa = Conversa.builder()
                .projeto(projeto)
                .build();
        return conversaRepository.save(conversa);
    }

    public List<Conversa> listarConversasDoUsuario(Integer usuarioId) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        if (!usuarioLogado.getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para listar conversas de outro usuario");
        }

        Set<Conversa> conversas = new LinkedHashSet<>(
                conversaRepository.findByProjetoOrientadorUsuarioIdOrProjetoAlunoCriadorUsuarioId(usuarioId, usuarioId)
        );

        List<Inscricao> inscricoes = inscricaoRepository.findByAlunoUsuarioIdAndStatus(usuarioId, StatusInscricao.APROVADO);
        List<Integer> projetoIds = inscricoes.stream().map(i -> i.getProjeto().getId()).distinct().toList();
        if (!projetoIds.isEmpty()) {
            conversas.addAll(conversaRepository.findByProjetoIdIn(projetoIds));
        }

        return new ArrayList<>(conversas);
    }

    public List<Mensagem> listarMensagens(Integer conversaId) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversa nao encontrada"));
        validarParticipacaoProjeto(conversa.getProjeto(), usuarioLogado.getId());
        return mensagemRepository.findByConversaIdOrderByDataEnvioAsc(conversaId);
    }

    public Mensagem enviarMensagem(Integer conversaId, String conteudo) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversa nao encontrada"));
        Projeto projeto = conversa.getProjeto();
        validarParticipacaoProjeto(projeto, usuarioLogado.getId());

        Mensagem mensagem = Mensagem.builder()
                .conversa(conversa)
                .remetente(usuarioLogado)
                .conteudo(conteudo)
                .build();
        Mensagem salva = mensagemRepository.save(mensagem);

        if (projeto.getOrientador() != null) {
            Integer orientadorUsuarioId = projeto.getOrientador().getUsuario().getId();
            if (!orientadorUsuarioId.equals(usuarioLogado.getId())) {
                notificacaoService.criarNotificacao(orientadorUsuarioId, "Nova mensagem em conversa do projeto", TipoNotificacao.MENSAGEM_RECEBIDA);
            }
        }
        if (projeto.getAlunoCriador() != null) {
            Integer alunoCriadorUsuarioId = projeto.getAlunoCriador().getUsuario().getId();
            if (!alunoCriadorUsuarioId.equals(usuarioLogado.getId())) {
                notificacaoService.criarNotificacao(alunoCriadorUsuarioId, "Nova mensagem em conversa do projeto", TipoNotificacao.MENSAGEM_RECEBIDA);
            }
        }

        return salva;
    }

    private void validarParticipacaoProjeto(Projeto projeto, Integer usuarioId) {
        boolean donoProjeto = (projeto.getOrientador() != null && projeto.getOrientador().getUsuario().getId().equals(usuarioId))
                || (projeto.getAlunoCriador() != null && projeto.getAlunoCriador().getUsuario().getId().equals(usuarioId));

        if (donoProjeto) {
            return;
        }

        Inscricao inscricao = inscricaoRepository.findByProjetoIdAndAlunoUsuarioId(projeto.getId(), usuarioId).orElse(null);
        if (inscricao == null || inscricao.getStatus() != StatusInscricao.APROVADO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario nao participa deste projeto");
        }
    }
}
