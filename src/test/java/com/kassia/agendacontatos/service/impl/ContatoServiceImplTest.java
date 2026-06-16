package com.kassia.agendacontatos.service.impl;

import com.kassia.agendacontatos.dto.ContatoRequest;
import com.kassia.agendacontatos.entity.Contato;
import com.kassia.agendacontatos.exception.RecursoNaoEncontrado;
import com.kassia.agendacontatos.repository.ContatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContatoServiceImplTest {

    @Mock
    private ContatoRepository contatoRepository;

    @InjectMocks
    private ContatoServiceImpl contatoService;

    @Test
    void deveSalvarContatoERetornarOsDadosPersistidos() {
        var request = new ContatoRequest("João", "12345678901", "joao@example.com");
        var contatoSalvo = new Contato(request);
        ReflectionTestUtils.setField(contatoSalvo, "id", 1L);

        when(contatoRepository.save(any(Contato.class))).thenReturn(contatoSalvo);

        var response = contatoService.salvarContato(request);

        assertEquals(1L, response.id());
        assertEquals("João", response.nome());
        assertEquals("12345678901", response.telefone());
        assertEquals("joao@example.com", response.email());
    }

    @Test
    void deveAtualizarContatoExistenteERetornarContatoAtualizado() {
        var request = new ContatoRequest("Maria", "10987654321", "maria@example.com");
        var contatoExistente = new Contato(new ContatoRequest("Antiga", "11111111111", "antiga@example.com"));
        ReflectionTestUtils.setField(contatoExistente, "id", 2L);

        when(contatoRepository.findById(2L)).thenReturn(Optional.of(contatoExistente));
        when(contatoRepository.save(contatoExistente)).thenReturn(contatoExistente);

        var response = contatoService.atualizarContato(2L, request);

        assertEquals(2L, response.id());
        assertEquals("Maria", response.nome());
        assertEquals("10987654321", response.telefone());
        assertEquals("maria@example.com", response.email());
        verify(contatoRepository).save(contatoExistente);
    }

    @Test
    void deveLancarExcecaoQuandoTentarAtualizarContatoInexistente() {
        when(contatoRepository.findById(99L)).thenReturn(Optional.empty());

        var exception = assertThrows(RecursoNaoEncontrado.class,
                () -> contatoService.atualizarContato(99L, new ContatoRequest("Maria", "10987654321", "maria@example.com")));

        assertEquals("Contato não encontrado com id: 99", exception.getMessage());
    }

    @Test
    void deveBuscarContatoPorIdERetornarContatoCorrespondente() {
        var contato = new Contato(new ContatoRequest("Carlos", "22222222222", "carlos@example.com"));
        ReflectionTestUtils.setField(contato, "id", 3L);

        when(contatoRepository.findById(3L)).thenReturn(Optional.of(contato));

        var response = contatoService.buscarContatoPorId(3L);

        assertEquals(3L, response.id());
        assertEquals("Carlos", response.nome());
        assertEquals("22222222222", response.telefone());
        assertEquals("carlos@example.com", response.email());
    }

    @Test
    void deveLancarExcecaoQuandoBuscarContatoPorIdInexistente() {
        when(contatoRepository.findById(7L)).thenReturn(Optional.empty());

        var exception = assertThrows(RecursoNaoEncontrado.class, () -> contatoService.buscarContatoPorId(7L));

        assertEquals("Contato não encontrado com id: 7", exception.getMessage());
    }

    @Test
    void deveBuscarContatoPorEmailERetornarContatoCorrespondente() {
        var contato = new Contato(new ContatoRequest("Paula", "33333333333", "paula@example.com"));
        ReflectionTestUtils.setField(contato, "id", 4L);

        when(contatoRepository.findByEmail("paula@example.com")).thenReturn(contato);

        var response = contatoService.buscarContatoPorEmail("paula@example.com");

        assertEquals(4L, response.id());
        assertEquals("Paula", response.nome());
        assertEquals("33333333333", response.telefone());
        assertEquals("paula@example.com", response.email());
    }

    @Test
    void deveBuscarContatosPorNomeEConverterTodosOsResultados() {
        var primeiro = new Contato(new ContatoRequest("Ana", "44444444444", "ana@example.com"));
        ReflectionTestUtils.setField(primeiro, "id", 5L);
        var segundo = new Contato(new ContatoRequest("Ana Clara", "55555555555", "anaclara@example.com"));
        ReflectionTestUtils.setField(segundo, "id", 6L);

        when(contatoRepository.findContatoesByNomeContainingIgnoreCase("Ana")).thenReturn(List.of(primeiro, segundo));

        var responses = contatoService.buscarContatoPorNome("Ana");

        assertEquals(2, responses.size());
        assertEquals("Ana", responses.get(0).nome());
        assertEquals("anaclara@example.com", responses.get(1).email());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremContatosComONomeInformado() {
        when(contatoRepository.findContatoesByNomeContainingIgnoreCase("Inexistente")).thenReturn(List.of());

        var responses = contatoService.buscarContatoPorNome("Inexistente");

        assertEquals(0, responses.size());
    }

    @Test
    void deveListarTodosOsContatosCadastrados() {
        var primeiro = new Contato(new ContatoRequest("Bruno", "66666666666", "bruno@example.com"));
        ReflectionTestUtils.setField(primeiro, "id", 7L);
        var segundo = new Contato(new ContatoRequest("Bianca", "77777777777", "bianca@example.com"));
        ReflectionTestUtils.setField(segundo, "id", 8L);

        when(contatoRepository.findAll()).thenReturn(List.of(primeiro, segundo));

        var responses = contatoService.listarContatos();

        assertEquals(2, responses.size());
        assertEquals("Bruno", responses.get(0).nome());
        assertEquals("bianca@example.com", responses.get(1).email());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverContatosCadastrados() {
        when(contatoRepository.findAll()).thenReturn(List.of());

        var responses = contatoService.listarContatos();

        assertEquals(0, responses.size());
    }

    @Test
    void deveDeletarContatoPeloIdentificadorInformado() {
        when(contatoRepository.existsById(10L)).thenReturn(true);
        contatoService.deletarContato(10L);

        verify(contatoRepository).deleteById(10L);
    }

    @Test
    void deveLancarExcecaoAoDeletarContatoComIdInexente() {
        when(contatoRepository.existsById(10L)).thenReturn(false);

        var exception = assertThrows(RecursoNaoEncontrado.class, () -> contatoService.deletarContato(10L));

        assertEquals("Contato não encontrado com id: 10", exception.getMessage());

    }
}
