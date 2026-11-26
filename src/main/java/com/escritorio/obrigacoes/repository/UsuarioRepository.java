package com.escritorio.obrigacoes.repository;

import com.escritorio.obrigacoes.domain.Situacao;
import com.escritorio.obrigacoes.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Usuario> findBySituacao(Situacao situacao);
}
