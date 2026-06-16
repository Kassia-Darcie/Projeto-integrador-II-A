package com.kassia.agendacontatos.controller;

import com.kassia.agendacontatos.dto.ContatoRequest;
import com.kassia.agendacontatos.dto.ContatoResponse;
import com.kassia.agendacontatos.service.ContatoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContatoController.class)
class ContatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContatoService contatoService;

    @Test
    void deveCadastrarContatoComSucesso() throws Exception {
        var request = new ContatoRequest("João", "12345678901", "joao@example.com");
        var response = new ContatoResponse(1L, "João", "12345678901", "joao@example.com");

        when(contatoService.salvarContato(any(ContatoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    void deveListarTodosOsContadosAoFazerARequisicaoSemFiltros() throws Exception {
        var c1 = new ContatoResponse(1L, "Ana", "11111111111", "ana@example.com");
        var c2 = new ContatoResponse(2L, "Bruno", "22222222222", "bruno@example.com");

        when(contatoService.listarContatos()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/contatos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Ana"))
                .andExpect(jsonPath("$[1].email").value("bruno@example.com"));
    }

    @Test
    void deveListarContatoPorEmailAoInformarOParametroEmail() throws Exception {
        var resp = new ContatoResponse(3L, "Carlos", "33333333333", "carlos@example.com");

        when(contatoService.buscarContatoPorEmail("carlos@example.com")).thenReturn(resp);

        mockMvc.perform(get("/contatos").param("email", "carlos@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].email").value("carlos@example.com"));
    }

    @Test
    void deveListarContatosPorNomeAoInformarOParametroNome() throws Exception {
        var resp1 = new ContatoResponse(4L, "Diego", "44444444444", "d1@example.com");
        var resp2 = new ContatoResponse(5L, "Diego", "55555555555", "d2@example.com");

        when(contatoService.buscarContatoPorNome("Diego")).thenReturn(List.of(resp1, resp2));

        mockMvc.perform(get("/contatos").param("nome", "Diego"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Diego"));
    }

    @Test
    void devePriorizarOEmailQuandoEmailENomeForemInformados() throws Exception {
        var resp = new ContatoResponse(6L, "Elaine", "66666666666", "elaine@example.com");

        when(contatoService.buscarContatoPorEmail("elaine@example.com")).thenReturn(resp);
        when(contatoService.buscarContatoPorNome("Elaine")).thenReturn(List.of());

        mockMvc.perform(get("/contatos").param("email", "elaine@example.com").param("nome", "Elaine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("elaine@example.com"));

        verify(contatoService, times(1)).buscarContatoPorEmail("elaine@example.com");
        verify(contatoService, never()).buscarContatoPorNome("Elaine");
    }

    @Test
    void deveBuscarContatoPorIdComSucesso() throws Exception {
        var resp = new ContatoResponse(7L, "Felipe", "77777777777", "felipe@example.com");

        when(contatoService.buscarContatoPorId(7L)).thenReturn(resp);

        mockMvc.perform(get("/contatos/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.nome").value("Felipe"));
    }

    @Test
    void deveAtualizarContatoComSucesso() throws Exception {
        var request = new ContatoRequest("Gabriela", "88888888888", "gabi@example.com");
        var updated = new ContatoResponse(8L, "Gabriela", "88888888888", "gabi@example.com");

        when(contatoService.atualizarContato(anyLong(), any(ContatoRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/contatos/8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.nome").value("Gabriela"));
    }

    @Test
    void deveDeletarContatoComSucesso() throws Exception {
        // service.deletarContato does not return; just ensure controller returns 204
        mockMvc.perform(delete("/contatos/9"))
                .andExpect(status().isNoContent());

        verify(contatoService, times(1)).deletarContato(9L);
    }

    @Test
    void deveRetornarBadRequestQuandoFormatoInvalido() throws Exception {
        var invalid = new ContatoRequest("Hugo", "123", "hugo.example.com");

        mockMvc.perform(post("/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.telefone").exists())
                .andExpect(jsonPath("$.erros.email").exists());

        verify(contatoService, never()).salvarContato(invalid);
    }

    @Test
    void deveRetornarBadRequestQuandoCamposForemNulosOuVazios() throws Exception {
        var invalid = new ContatoRequest(null, "", "");

        mockMvc.perform(post("/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.erros.nome").exists())
               .andExpect(jsonPath("$.erros.telefone").exists())
               .andExpect(jsonPath("$.erros.email").exists());

        verify(contatoService, never()).salvarContato(invalid);
    }
}

