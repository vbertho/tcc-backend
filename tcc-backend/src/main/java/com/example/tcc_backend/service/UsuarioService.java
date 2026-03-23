package com.example.tcc_backend.service;

import com.example.tcc_backend.dto.request.UsuarioRequest;
import com.example.tcc_backend.model.Usuario;
import com.example.tcc_backend.repository.AlunoRepository;
import com.example.tcc_backend.repository.OrientadorRepository;
import com.example.tcc_backend.repository.UsuarioRepository;
import com.example.tcc_backend.security.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final OrientadorRepository orientadorRepository;
    private final AuthHelper authHelper;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public Usuario update(Integer id, UsuarioRequest dto) {
        Usuario usuarioLogado = authHelper.getCurrentUser();

        if (!usuarioLogado.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode editar outro usuário");
        }

        Usuario usuario = findById(id);

        Usuario atualizado = Usuario.builder()
                .id(usuario.getId())
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(usuario.getSenha())
                .tipo(usuario.getTipo())
                .dataCadastro(usuario.getDataCadastro())
                .ativo(usuario.getAtivo())
                .build();

        return usuarioRepository.save(atualizado);
    }

    public void delete(Integer id) {
        Usuario usuarioLogado = authHelper.getCurrentUser();

        if (!usuarioLogado.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode remover outro usuário");
        }

        Usuario usuario = findById(id);

        Usuario desativado = Usuario.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .senha(usuario.getSenha())
                .tipo(usuario.getTipo())
                .dataCadastro(usuario.getDataCadastro())
                .ativo(false)
                .build();

        usuarioRepository.save(desativado);
    }
}