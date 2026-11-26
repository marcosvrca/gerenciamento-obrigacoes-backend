package com.escritorio.obrigacoes.repository;

import com.escritorio.obrigacoes.domain.Anexo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnexoRepository extends JpaRepository<Anexo, Long> {

    List<Anexo> findByObrigacaoIdOrderByCriadoEmDesc(Long obrigacaoId);
}
