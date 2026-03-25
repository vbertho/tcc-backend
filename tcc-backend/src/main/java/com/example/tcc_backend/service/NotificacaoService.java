package com.example.tcc_backend.service;

import com.example.tcc_backend.model.Notificacao;
import com.example.tcc_backend.model.TipoNotificacao;
import com.example.tcc_backend.model.Usuario;
import com.example.tcc_backend.repository.NotificacaoRepository;
import com.example.tcc_backend.repository.UsuarioRepository;
import com.example.tcc_backend.security.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthHelper authHelper;

    public List<Notificacao> minhasNotificacoes() {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        return notificacaoRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuarioLogado.getId());
    }

    public Notificacao marcarComoLida(Integer id) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificacao nao encontrada"));

        if (!notificacao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para alterar esta notificacao");
        }

        notificacao.setLida(true);
        return notificacaoRepository.save(notificacao);
    }

    public void marcarTodasComoLidas() {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuarioLogado.getId());

        for (Notificacao notificacao : notificacoes) {
            if (!Boolean.TRUE.equals(notificacao.getLida())) {
                notificacao.setLida(true);
            }
        }
        notificacaoRepository.saveAll(notificacoes);
    }

    public void criarNotificacao(Integer usuarioId, String mensagem, TipoNotificacao tipo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));

        Notificacao notificacao = Notificacao.builder()
                .usuario(usuario)
                .mensagem(mensagem)
                .tipo(tipo)
                .build();
        notificacaoRepository.save(notificacao);
    }
}
