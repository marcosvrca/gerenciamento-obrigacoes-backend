package com.escritorio.obrigacoes.service;

import com.escritorio.obrigacoes.domain.Empresa;
import com.escritorio.obrigacoes.domain.Obrigacao;
import com.escritorio.obrigacoes.domain.StatusObrigacao;
import com.escritorio.obrigacoes.domain.Usuario;
import com.escritorio.obrigacoes.exception.BusinessException;
import com.escritorio.obrigacoes.exception.ResourceNotFoundException;
import com.escritorio.obrigacoes.repository.ObrigacaoRepository;
import com.escritorio.obrigacoes.web.dto.ObrigacaoRequest;
import com.escritorio.obrigacoes.web.dto.ObrigacaoResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ObrigacaoService {

    private final ObrigacaoRepository obrigacaoRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;

    public ObrigacaoService(ObrigacaoRepository obrigacaoRepository,
                            EmpresaService empresaService,
                            UsuarioService usuarioService) {
        this.obrigacaoRepository = obrigacaoRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<ObrigacaoResponse> listar(StatusObrigacao status, Long empresaId) {
        return obrigacaoRepository.findAllFiltered(status, empresaId).stream()
                .map(ObrigacaoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ObrigacaoResponse buscarPorId(Long id) {
        return ObrigacaoResponse.from(obterEntidade(id));
    }

    public ObrigacaoResponse criar(ObrigacaoRequest request) {
        Empresa empresa = empresaService.obterAtiva(request.empresaId());
        Usuario responsavel = usuarioService.obterAtivo(request.usuarioId());

        Obrigacao obrigacao = new Obrigacao();
        obrigacao.setTitulo(request.titulo().trim());
        obrigacao.setDescricao(normalizarDescricao(request.descricao()));
        obrigacao.setPrazo(request.prazo());
        obrigacao.setStatus(StatusObrigacao.PENDENTE);
        obrigacao.setEmpresa(empresa);
        obrigacao.setResponsavel(responsavel);

        return ObrigacaoResponse.from(obrigacaoRepository.save(obrigacao));
    }

    public ObrigacaoResponse atualizar(Long id, ObrigacaoRequest request) {
        Obrigacao obrigacao = obterEntidade(id);
        if (obrigacao.getStatus() == StatusObrigacao.CONCLUIDA) {
            throw new BusinessException("Não é permitido editar uma obrigação concluída");
        }

        boolean empresaAlterada = !obrigacao.getEmpresa().getId().equals(request.empresaId());
        boolean usuarioAlterado = !obrigacao.getResponsavel().getId().equals(request.usuarioId());

        if (empresaAlterada) {
            obrigacao.setEmpresa(empresaService.obterAtiva(request.empresaId()));
        }
        if (usuarioAlterado) {
            obrigacao.setResponsavel(usuarioService.obterAtivo(request.usuarioId()));
        }

        obrigacao.setTitulo(request.titulo().trim());
        obrigacao.setDescricao(normalizarDescricao(request.descricao()));
        obrigacao.setPrazo(request.prazo());
        obrigacao.setAtualizadoEm(Instant.now());

        return ObrigacaoResponse.from(obrigacaoRepository.save(obrigacao));
    }

    public ObrigacaoResponse atualizarStatus(Long id, StatusObrigacao novoStatus) {
        Obrigacao obrigacao = obterEntidade(id);
        StatusObrigacao atual = obrigacao.getStatus();

        if (atual == StatusObrigacao.CONCLUIDA) {
            throw new BusinessException("Não é permitido alterar o status de uma obrigação concluída");
        }
        if (!atual.podeTransicionarPara(novoStatus)) {
            throw new BusinessException(
                    "Transição de status inválida: " + atual + " -> " + novoStatus
                            + ". Fluxo permitido: PENDENTE -> EM_ANDAMENTO -> CONCLUIDA");
        }

        obrigacao.setStatus(novoStatus);
        obrigacao.setAtualizadoEm(Instant.now());
        return ObrigacaoResponse.from(obrigacaoRepository.save(obrigacao));
    }

    @Transactional(readOnly = true)
    public Obrigacao obterEntidade(Long id) {
        return obrigacaoRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Obrigação não encontrada: " + id));
    }

    private String normalizarDescricao(String descricao) {
        return descricao == null || descricao.isBlank() ? null : descricao.trim();
    }
}
