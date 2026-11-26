package com.escritorio.obrigacoes.repository;

import com.escritorio.obrigacoes.domain.Obrigacao;
import com.escritorio.obrigacoes.domain.StatusObrigacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ObrigacaoRepository extends JpaRepository<Obrigacao, Long> {

    @Query("""
            SELECT o FROM Obrigacao o
            JOIN FETCH o.empresa
            JOIN FETCH o.responsavel
            WHERE (:status IS NULL OR o.status = :status)
              AND (:empresaId IS NULL OR o.empresa.id = :empresaId)
            ORDER BY o.prazo ASC
            """)
    List<Obrigacao> findAllFiltered(@Param("status") StatusObrigacao status,
                                    @Param("empresaId") Long empresaId);

    @Query("""
            SELECT o FROM Obrigacao o
            JOIN FETCH o.empresa
            JOIN FETCH o.responsavel
            WHERE o.id = :id
            """)
    java.util.Optional<Obrigacao> findDetailedById(@Param("id") Long id);
}
