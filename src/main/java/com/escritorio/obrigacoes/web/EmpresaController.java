package com.escritorio.obrigacoes.web;

import com.escritorio.obrigacoes.domain.Situacao;
import com.escritorio.obrigacoes.service.EmpresaService;
import com.escritorio.obrigacoes.web.dto.EmpresaRequest;
import com.escritorio.obrigacoes.web.dto.EmpresaResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public List<EmpresaResponse> listar(@RequestParam(required = false) Situacao situacao) {
        return empresaService.listar(situacao);
    }

    @GetMapping("/{id}")
    public EmpresaResponse buscar(@PathVariable Long id) {
        return empresaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponse criar(@Valid @RequestBody EmpresaRequest request) {
        return empresaService.criar(request);
    }

    @PutMapping("/{id}")
    public EmpresaResponse atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaRequest request) {
        return empresaService.atualizar(id, request);
    }
}
