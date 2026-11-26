package com.escritorio.obrigacoes.service;

import com.escritorio.obrigacoes.domain.Empresa;
import com.escritorio.obrigacoes.domain.Situacao;
import com.escritorio.obrigacoes.exception.BusinessException;
import com.escritorio.obrigacoes.exception.ResourceNotFoundException;
import com.escritorio.obrigacoes.repository.EmpresaRepository;
import com.escritorio.obrigacoes.web.dto.EmpresaRequest;
import com.escritorio.obrigacoes.web.dto.EmpresaResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> listar(Situacao situacao) {
        List<Empresa> empresas = situacao == null
                ? empresaRepository.findAll()
                : empresaRepository.findBySituacao(situacao);
        return empresas.stream().map(EmpresaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorId(Long id) {
        return EmpresaResponse.from(obterEntidade(id));
    }

    public EmpresaResponse criar(EmpresaRequest request) {
        if (empresaRepository.existsByCnpj(request.cnpj())) {
            throw new BusinessException("Já existe empresa com este CNPJ", HttpStatus.CONFLICT);
        }
        Empresa empresa = new Empresa();
        aplicar(empresa, request);
        return EmpresaResponse.from(empresaRepository.save(empresa));
    }

    public EmpresaResponse atualizar(Long id, EmpresaRequest request) {
        Empresa empresa = obterEntidade(id);
        if (empresaRepository.existsByCnpjAndIdNot(request.cnpj(), id)) {
            throw new BusinessException("Já existe empresa com este CNPJ", HttpStatus.CONFLICT);
        }
        aplicar(empresa, request);
        empresa.setAtualizadoEm(Instant.now());
        return EmpresaResponse.from(empresaRepository.save(empresa));
    }

    @Transactional(readOnly = true)
    public Empresa obterEntidade(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Empresa obterAtiva(Long id) {
        Empresa empresa = obterEntidade(id);
        if (!empresa.isAtiva()) {
            throw new BusinessException("Empresa inativa não pode ser utilizada em novas atribuições");
        }
        return empresa;
    }

    private void aplicar(Empresa empresa, EmpresaRequest request) {
        empresa.setRazaoSocial(request.razaoSocial().trim());
        empresa.setCnpj(request.cnpj());
        empresa.setEmail(request.email().trim().toLowerCase());
        empresa.setSituacao(request.situacao());
    }
}
