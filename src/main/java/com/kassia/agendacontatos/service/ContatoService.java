package com.kassia.agendacontatos.service;

import com.kassia.agendacontatos.dto.ContatoRequest;
import com.kassia.agendacontatos.dto.ContatoResponse;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface ContatoService {
    ContatoResponse salvarContato(@Validated ContatoRequest contato);
    ContatoResponse atualizarContato(Long id, @Validated ContatoRequest contato);
    ContatoResponse buscarContatoPorId(Long id);
    ContatoResponse buscarContatoPorEmail(String email);
    List<ContatoResponse> buscarContatoPorNome(String nome);
    List<ContatoResponse> listarContatos();
    void deletarContato(Long id);
}
