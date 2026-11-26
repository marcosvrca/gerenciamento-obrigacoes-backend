package com.escritorio.obrigacoes.web;

import com.escritorio.obrigacoes.domain.StatusObrigacao;
import com.escritorio.obrigacoes.service.ObrigacaoService;
import com.escritorio.obrigacoes.web.dto.ObrigacaoRequest;
import com.escritorio.obrigacoes.web.dto.ObrigacaoResponse;
import com.escritorio.obrigacoes.web.dto.StatusUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/obrigacoes")
public class ObrigacaoController {

    private final ObrigacaoService obrigacaoService;

    public ObrigacaoController(ObrigacaoService obrigacaoService) {
        this.obrigacaoService = obrigacaoService;
    }

    @GetMapping
    public List<ObrigacaoResponse> listar(@RequestParam(required = false) StatusObrigacao status,
                                          @RequestParam(required = false) Long empresaId) {
        return obrigacaoService.listar(status, empresaId);
    }

    @GetMapping("/{id}")
    public ObrigacaoResponse buscar(@PathVariable Long id) {
        return obrigacaoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObrigacaoResponse criar(@Valid @RequestBody ObrigacaoRequest request) {
        return obrigacaoService.criar(request);
    }

    @PutMapping("/{id}")
    public ObrigacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ObrigacaoRequest request) {
        return obrigacaoService.atualizar(id, request);
    }

    @PatchMapping("/{id}/status")
    public ObrigacaoResponse atualizarStatus(@PathVariable Long id,
                                             @Valid @RequestBody StatusUpdateRequest request) {
        return obrigacaoService.atualizarStatus(id, request.status());
    }
}
