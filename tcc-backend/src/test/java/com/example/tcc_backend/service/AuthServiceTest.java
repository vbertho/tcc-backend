package com.example.tcc_backend.service;

import com.example.tcc_backend.dto.request.LoginRequest;
import com.example.tcc_backend.dto.request.RegisterRequest;
import com.example.tcc_backend.dto.response.AuthResponse;
import com.example.tcc_backend.model.Usuario;
import com.example.tcc_backend.repository.AlunoRepository;
import com.example.tcc_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerDeveNormalizarCamposSalvarUsuarioEAlunoERetornarToken() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("  Rodrigo  ");
        request.setEmail("  RODRIGO@TESTE.COM  ");
        request.setSenha("12345678");
        request.setRa(" 12345 ");

        when(usuarioRepository.existsByEmail("rodrigo@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("senha-codificada");
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertThat(usuarioSalvo.getNome()).isEqualTo("Rodrigo");
        assertThat(usuarioSalvo.getEmail()).isEqualTo("rodrigo@teste.com");
        assertThat(usuarioSalvo.getSenha()).isEqualTo("senha-codificada");
        assertThat(response.getToken()).isEqualTo("jwt-token");

        verify(alunoRepository).save(any());
    }

    @Test
    void registerDeveRetornarConflitoQuandoEmailJaExiste() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Rodrigo");
        request.setEmail("rodrigo@teste.com");
        request.setSenha("12345678");
        request.setRa("12345");

        when(usuarioRepository.existsByEmail("rodrigo@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void loginDeveAutenticarEBuscarUsuarioParaGerarToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("rodrigo@teste.com");
        request.setSenha("12345678");

        Usuario usuario = Usuario.builder()
                .id(1)
                .nome("Rodrigo")
                .email("rodrigo@teste.com")
                .senha("hash")
                .build();

        when(usuarioRepository.findByEmail("rodrigo@teste.com")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("jwt-login");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("rodrigo@teste.com", "12345678"));
        assertThat(response.getToken()).isEqualTo("jwt-login");
    }
}
