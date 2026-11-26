package com.escritorio.obrigacoes.repository;

import com.escritorio.obrigacoes.domain.Empresa;
import com.escritorio.obrigacoes.domain.Situacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    List<Empresa> findBySituacao(Situacao situacao);
}
