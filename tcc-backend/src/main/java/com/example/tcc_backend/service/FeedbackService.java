package com.example.tcc_backend.service;

import com.example.tcc_backend.dto.request.FeedbackRequest;
import com.example.tcc_backend.model.Feedback;
import com.example.tcc_backend.model.Projeto;
import com.example.tcc_backend.model.Usuario;
import com.example.tcc_backend.repository.FeedbackRepository;
import com.example.tcc_backend.repository.ProjetoRepository;
import com.example.tcc_backend.security.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ProjetoRepository projetoRepository;
    private final AuthHelper authHelper;

    public Feedback criar(FeedbackRequest dto) {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        Projeto projeto = projetoRepository.findById(dto.getProjetoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto nao encontrado"));

        Feedback feedback = Feedback.builder()
                .projeto(projeto)
                .avaliador(usuarioLogado)
                .nota(dto.getNota())
                .comentario(dto.getComentario())
                .build();
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> listarPorProjeto(Integer projetoId) {
        return feedbackRepository.findByProjetoId(projetoId);
    }

    public List<Feedback> listarPorUsuario(Integer usuarioId) {
        return feedbackRepository.findByProjetoOrientadorUsuarioIdOrProjetoAlunoCriadorUsuarioId(usuarioId, usuarioId);
    }
}
