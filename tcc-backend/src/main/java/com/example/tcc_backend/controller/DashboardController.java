package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.response.DashboardResponse;
import com.example.tcc_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> resumo() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}
