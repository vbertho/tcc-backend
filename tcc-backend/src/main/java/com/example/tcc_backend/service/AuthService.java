package com.example.tcc_backend.service;

import com.example.tcc_backend.dto.request.LoginRequest;
import com.example.tcc_backend.dto.request.RegisterRequest;
import com.example.tcc_backend.dto.response.AuthResponse;
import com.example.tcc_backend.model.*;
import com.example.tcc_backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final OrientadorRepository orientadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository,
                       AlunoRepository alunoRepository,
                       OrientadorRepository orientadorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.orientadorRepository = orientadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .tipo(dto.getTipo())
                .build();

        usuarioRepository.save(usuario);

        if (dto.getTipo() == TipoUsuario.ALUNO) {
            Aluno aluno = Aluno.builder()
                    .usuario(usuario)
                    .ra(dto.getRa())
                    .build();
            alunoRepository.save(aluno);
        } else {
            Orientador orientador = Orientador.builder()
                    .usuario(usuario)
                    .departamento(dto.getDepartamento())
                    .titulacao(dto.getTitulacao())
                    .build();
            orientadorRepository.save(orientador);
        }

        return new AuthResponse(jwtService.generateToken(usuario));
    }

    public AuthResponse login(LoginRequest dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail()).orElseThrow();
        return new AuthResponse(jwtService.generateToken(usuario));
    }

    public String logout() {
        SecurityContextHolder.clearContext();
        return "Logout realizado com sucesso";
    }
}