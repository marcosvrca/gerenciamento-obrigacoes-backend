package com.escritorio.obrigacoes.service;

import com.escritorio.obrigacoes.config.StorageProperties;
import com.escritorio.obrigacoes.domain.Anexo;
import com.escritorio.obrigacoes.domain.Obrigacao;
import com.escritorio.obrigacoes.exception.BusinessException;
import com.escritorio.obrigacoes.exception.ResourceNotFoundException;
import com.escritorio.obrigacoes.repository.AnexoRepository;
import com.escritorio.obrigacoes.web.dto.AnexoResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class AnexoService {

    private final AnexoRepository anexoRepository;
    private final ObrigacaoService obrigacaoService;
    private final Path storageRoot;

    public AnexoService(AnexoRepository anexoRepository,
                        ObrigacaoService obrigacaoService,
                        StorageProperties storageProperties) {
        this.anexoRepository = anexoRepository;
        this.obrigacaoService = obrigacaoService;
        this.storageRoot = Path.of(storageProperties.getPath()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar o diretório de uploads", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AnexoResponse> listarPorObrigacao(Long obrigacaoId) {
        obrigacaoService.obterEntidade(obrigacaoId);
        return anexoRepository.findByObrigacaoIdOrderByCriadoEmDesc(obrigacaoId).stream()
                .map(AnexoResponse::from)
                .toList();
    }

    public AnexoResponse upload(Long obrigacaoId, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Arquivo é obrigatório", HttpStatus.BAD_REQUEST);
        }

        Obrigacao obrigacao = obrigacaoService.obterEntidade(obrigacaoId);
        String nomeOriginal = arquivo.getOriginalFilename() == null ? "arquivo" : arquivo.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + sanitize(nomeOriginal);
        Path destino = storageRoot.resolve(storedName);

        try (InputStream inputStream = arquivo.getInputStream()) {
            Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("Falha ao armazenar o arquivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Anexo anexo = new Anexo();
        anexo.setNomeOriginal(nomeOriginal);
        anexo.setContentType(arquivo.getContentType() == null ? "application/octet-stream" : arquivo.getContentType());
        anexo.setCaminhoArmazenado(storedName);
        anexo.setTamanhoBytes(arquivo.getSize());
        anexo.setObrigacao(obrigacao);

        return AnexoResponse.from(anexoRepository.save(anexo));
    }

    @Transactional(readOnly = true)
    public AnexoArquivo download(Long id) {
        Anexo anexo = anexoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anexo não encontrado: " + id));
        try {
            Path arquivo = storageRoot.resolve(anexo.getCaminhoArmazenado()).normalize();
            if (!arquivo.startsWith(storageRoot) || !Files.exists(arquivo)) {
                throw new ResourceNotFoundException("Arquivo físico do anexo não encontrado: " + id);
            }
            Resource resource = new UrlResource(arquivo.toUri());
            return new AnexoArquivo(anexo.getNomeOriginal(), anexo.getContentType(), resource);
        } catch (IOException e) {
            throw new BusinessException("Falha ao ler o arquivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String sanitize(String nome) {
        return nome.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public record AnexoArquivo(String nomeOriginal, String contentType, Resource resource) {
    }
}
