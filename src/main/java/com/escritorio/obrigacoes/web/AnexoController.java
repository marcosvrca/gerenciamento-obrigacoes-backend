package com.escritorio.obrigacoes.web;

import com.escritorio.obrigacoes.service.AnexoService;
import com.escritorio.obrigacoes.web.dto.AnexoResponse;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AnexoController {

    private final AnexoService anexoService;

    public AnexoController(AnexoService anexoService) {
        this.anexoService = anexoService;
    }

    @GetMapping("/obrigacoes/{obrigacaoId}/anexos")
    public List<AnexoResponse> listar(@PathVariable Long obrigacaoId) {
        return anexoService.listarPorObrigacao(obrigacaoId);
    }

    @PostMapping(value = "/obrigacoes/{obrigacaoId}/anexos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AnexoResponse upload(@PathVariable Long obrigacaoId,
                                @RequestPart("arquivo") MultipartFile arquivo) {
        return anexoService.upload(obrigacaoId, arquivo);
    }

    @GetMapping("/anexos/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        AnexoService.AnexoArquivo arquivo = anexoService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + arquivo.nomeOriginal() + "\"")
                .body(arquivo.resource());
    }
}
