package com.example.tcc_backend.service;

import com.example.tcc_backend.dto.request.UsuarioRequest;
import com.example.tcc_backend.model.Inscricao;
import com.example.tcc_backend.model.Projeto;
import com.example.tcc_backend.model.TipoUsuario;
import com.example.tcc_backend.model.Usuario;
import com.example.tcc_backend.repository.AlunoRepository;
import com.example.tcc_backend.repository.InscricaoRepository;
import com.example.tcc_backend.repository.OrientadorRepository;
import com.example.tcc_backend.repository.ProjetoRepository;
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
    private final ProjetoRepository projetoRepository;
    private final InscricaoRepository inscricaoRepository;
    private final AuthHelper authHelper;

    public List<Usuario> findAll() {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        if (usuarioLogado.getTipo() != TipoUsuario.ORIENTADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas orientadores podem listar usuarios");
        }
        return usuarioRepository.findAll();
    }

    public Usuario findById(Integer id) {
        validarAcessoAoUsuario(id, true);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }

    public Usuario update(Integer id, UsuarioRequest dto) {
        Usuario usuarioLogado = authHelper.getCurrentUser();

        if (!usuarioLogado.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Voce nao pode editar outro usuario");
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Voce nao pode remover outro usuario");
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

    public List<Projeto> findProjetosByUsuario(Integer id) {
        validarAcessoAoUsuario(id, true);
        findById(id);
        return projetoRepository.findByOrientadorUsuarioIdOrAlunoCriadorUsuarioId(id, id);
    }

    public List<Inscricao> findInscricoesByUsuario(Integer id) {
        validarAcessoAoUsuario(id, false);
        Usuario usuario = findById(id);
        if (usuario.getTipo() == TipoUsuario.ALUNO) {
            return inscricaoRepository.findByAlunoUsuarioId(id);
        }
        return inscricaoRepository.findByProjetoOrientadorUsuarioId(id);
    }

    public List<Inscricao> findMinhasInscricoes() {
        Usuario usuarioLogado = authHelper.getCurrentUser();
        if (usuarioLogado.getTipo() != TipoUsuario.ALUNO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint exclusivo para alunos");
        }
        return inscricaoRepository.findByAlunoUsuarioId(usuarioLogado.getId());
    }

    private void validarAcessoAoUsuario(Integer id, boolean permitirOrientador) {
        Usuario usuarioLogado = authHelper.getCurrentUser();

        if (usuarioLogado.getId().equals(id)) {
            return;
        }

        if (permitirOrientador && usuarioLogado.getTipo() == TipoUsuario.ORIENTADOR) {
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para acessar dados de outro usuario");
    }
}
