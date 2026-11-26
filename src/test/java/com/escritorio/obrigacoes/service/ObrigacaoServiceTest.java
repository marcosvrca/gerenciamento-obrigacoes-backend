package com.escritorio.obrigacoes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.escritorio.obrigacoes.domain.Empresa;
import com.escritorio.obrigacoes.domain.Obrigacao;
import com.escritorio.obrigacoes.domain.Situacao;
import com.escritorio.obrigacoes.domain.StatusObrigacao;
import com.escritorio.obrigacoes.domain.Usuario;
import com.escritorio.obrigacoes.exception.BusinessException;
import com.escritorio.obrigacoes.repository.ObrigacaoRepository;
import com.escritorio.obrigacoes.web.dto.ObrigacaoRequest;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ObrigacaoServiceTest {

    @Mock
    private ObrigacaoRepository obrigacaoRepository;
    @Mock
    private EmpresaService empresaService;
    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ObrigacaoService obrigacaoService;

    private Empresa empresa;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setRazaoSocial("Empresa Teste");
        empresa.setSituacao(Situacao.ATIVO);

        usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNome("Responsável");
        usuario.setSituacao(Situacao.ATIVO);
    }

    @Test
    void criarDeveIniciarComoPendente() {
        when(empresaService.obterAtiva(1L)).thenReturn(empresa);
        when(usuarioService.obterAtivo(2L)).thenReturn(usuario);
        when(obrigacaoRepository.save(any(Obrigacao.class))).thenAnswer(invocation -> {
            Obrigacao o = invocation.getArgument(0);
            o.setId(10L);
            return o;
        });

        ObrigacaoRequest request = new ObrigacaoRequest(
                "DARF", "Pagamento", LocalDate.now().plusDays(5), 1L, 2L);

        var response = obrigacaoService.criar(request);

        assertEquals(StatusObrigacao.PENDENTE, response.status());
        ArgumentCaptor<Obrigacao> captor = ArgumentCaptor.forClass(Obrigacao.class);
        verify(obrigacaoRepository).save(captor.capture());
        assertEquals(StatusObrigacao.PENDENTE, captor.getValue().getStatus());
    }

    @Test
    void atualizarStatusDeveSeguirFluxo() {
        Obrigacao obrigacao = obrigacaoPendente();
        when(obrigacaoRepository.findDetailedById(10L)).thenReturn(Optional.of(obrigacao));
        when(obrigacaoRepository.save(any(Obrigacao.class))).thenAnswer(i -> i.getArgument(0));

        var response = obrigacaoService.atualizarStatus(10L, StatusObrigacao.EM_ANDAMENTO);

        assertEquals(StatusObrigacao.EM_ANDAMENTO, response.status());
    }

    @Test
    void atualizarStatusDeveRejeitarRegressao() {
        Obrigacao obrigacao = obrigacaoPendente();
        obrigacao.setStatus(StatusObrigacao.EM_ANDAMENTO);
        when(obrigacaoRepository.findDetailedById(10L)).thenReturn(Optional.of(obrigacao));

        assertThrows(BusinessException.class,
                () -> obrigacaoService.atualizarStatus(10L, StatusObrigacao.PENDENTE));
    }

    @Test
    void atualizarStatusDeveBloquearConcluida() {
        Obrigacao obrigacao = obrigacaoPendente();
        obrigacao.setStatus(StatusObrigacao.CONCLUIDA);
        when(obrigacaoRepository.findDetailedById(10L)).thenReturn(Optional.of(obrigacao));

        assertThrows(BusinessException.class,
                () -> obrigacaoService.atualizarStatus(10L, StatusObrigacao.EM_ANDAMENTO));
    }

    @Test
    void atualizarDeveBloquearQuandoConcluida() {
        Obrigacao obrigacao = obrigacaoPendente();
        obrigacao.setStatus(StatusObrigacao.CONCLUIDA);
        when(obrigacaoRepository.findDetailedById(10L)).thenReturn(Optional.of(obrigacao));

        ObrigacaoRequest request = new ObrigacaoRequest(
                "Nova", null, LocalDate.now().plusDays(1), 1L, 2L);

        assertThrows(BusinessException.class, () -> obrigacaoService.atualizar(10L, request));
    }

    private Obrigacao obrigacaoPendente() {
        Obrigacao obrigacao = new Obrigacao();
        obrigacao.setId(10L);
        obrigacao.setTitulo("DARF");
        obrigacao.setPrazo(LocalDate.now().plusDays(3));
        obrigacao.setStatus(StatusObrigacao.PENDENTE);
        obrigacao.setEmpresa(empresa);
        obrigacao.setResponsavel(usuario);
        return obrigacao;
    }
}
