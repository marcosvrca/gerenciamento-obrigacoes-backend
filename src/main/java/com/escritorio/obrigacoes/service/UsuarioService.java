package com.escritorio.obrigacoes.service;

import com.escritorio.obrigacoes.domain.Situacao;
import com.escritorio.obrigacoes.domain.Usuario;
import com.escritorio.obrigacoes.exception.BusinessException;
import com.escritorio.obrigacoes.exception.ResourceNotFoundException;
import com.escritorio.obrigacoes.repository.UsuarioRepository;
import com.escritorio.obrigacoes.web.dto.UsuarioRequest;
import com.escritorio.obrigacoes.web.dto.UsuarioResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(Situacao situacao) {
        List<Usuario> usuarios = situacao == null
                ? usuarioRepository.findAll()
                : usuarioRepository.findBySituacao(situacao);
        return usuarios.stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.from(obterEntidade(id));
    }

    public UsuarioResponse criar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new BusinessException("Já existe usuário com este e-mail", HttpStatus.CONFLICT);
        }
        Usuario usuario = new Usuario();
        aplicar(usuario, request);
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = obterEntidade(id);
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new BusinessException("Já existe usuário com este e-mail", HttpStatus.CONFLICT);
        }
        aplicar(usuario, request);
        usuario.setAtualizadoEm(Instant.now());
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Usuario obterEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario obterAtivo(Long id) {
        Usuario usuario = obterEntidade(id);
        if (!usuario.isAtivo()) {
            throw new BusinessException("Usuário inativo não pode ser utilizado em novas atribuições");
        }
        return usuario;
    }

    private void aplicar(Usuario usuario, UsuarioRequest request) {
        usuario.setNome(request.nome().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setSituacao(request.situacao());
    }
}
