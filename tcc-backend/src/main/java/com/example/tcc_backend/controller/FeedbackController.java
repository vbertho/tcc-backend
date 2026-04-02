package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.FeedbackRequest;
import com.example.tcc_backend.model.Feedback;
import com.example.tcc_backend.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Feedback> criar(@RequestBody @Valid FeedbackRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.criar(dto));
    }

    @GetMapping("/projeto/{id}")
    public ResponseEntity<List<Feedback>> listarPorProjeto(@PathVariable Integer id) {
        return ResponseEntity.ok(feedbackService.listarPorProjeto(id));
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<Feedback>> listarPorUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(feedbackService.listarPorUsuario(id));
    }
}

