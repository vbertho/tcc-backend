package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.FeedbackRequest;
import com.example.tcc_backend.dto.response.FeedbackResponse;
import com.example.tcc_backend.dto.response.PageResponse;
import com.example.tcc_backend.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> criar(@RequestBody @Valid FeedbackRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FeedbackResponse.fromEntity(feedbackService.criar(dto)));
    }

    @GetMapping("/projeto/{id}")
    public ResponseEntity<List<FeedbackResponse>> listarPorProjeto(@PathVariable Integer id) {
        return ResponseEntity.ok(
                feedbackService.listarPorProjeto(id).stream()
                        .map(FeedbackResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/projeto/{id}/pagina")
    public ResponseEntity<PageResponse<FeedbackResponse>> listarPorProjetoPaginado(@PathVariable Integer id,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                   @RequestParam(defaultValue = "dataFeedback") String sort,
                                                                                   @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(
                PageResponse.from(
                        feedbackService.listarPorProjeto(id, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort)))
                                .map(FeedbackResponse::fromEntity)
                )
        );
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<FeedbackResponse>> listarPorUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(
                feedbackService.listarPorUsuario(id).stream()
                        .map(FeedbackResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/usuario/{id}/pagina")
    public ResponseEntity<PageResponse<FeedbackResponse>> listarPorUsuarioPaginado(@PathVariable Integer id,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                   @RequestParam(defaultValue = "dataFeedback") String sort,
                                                                                   @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(
                PageResponse.from(
                        feedbackService.listarPorUsuario(id, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort)))
                                .map(FeedbackResponse::fromEntity)
                )
        );
    }
}

