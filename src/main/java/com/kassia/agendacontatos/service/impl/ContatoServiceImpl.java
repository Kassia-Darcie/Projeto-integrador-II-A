package com.kassia.agendacontatos.service.impl;

import com.kassia.agendacontatos.dto.ContatoRequest;
import com.kassia.agendacontatos.dto.ContatoResponse;
import com.kassia.agendacontatos.entity.Contato;
import com.kassia.agendacontatos.exception.RecursoNaoEncontrado;
import com.kassia.agendacontatos.repository.ContatoRepository;
import com.kassia.agendacontatos.service.ContatoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContatoServiceImpl implements ContatoService {
    private ContatoRepository contatoRepository;

    @Override
    public ContatoResponse salvarContato(ContatoRequest contatoRequest) {
        var contatoSalvo = contatoRepository.save(new Contato(contatoRequest));
        return new ContatoResponse(contatoSalvo);
    }

    @Override
    public ContatoResponse atualizarContato(Long id, ContatoRequest contatoRequest) {
        Contato contatoParaAtualizar = contatoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontrado("Contato não encontrado com id: " + id));
        contatoParaAtualizar.setNome(contatoRequest.nome());
        contatoParaAtualizar.setEmail(contatoRequest.email());
        contatoParaAtualizar.setTelefone(contatoRequest.telefone());

        var response = contatoRepository.save(contatoParaAtualizar);
        return new ContatoResponse(response);
    }

    @Override
    public ContatoResponse buscarContatoPorId(Long id) {
        return contatoRepository.findById(id)
                .map(ContatoResponse::new)
                .orElseThrow(() -> new RecursoNaoEncontrado("Contato não encontrado com id: " + id));
    }

    @Override
    public ContatoResponse buscarContatoPorEmail(String email) {
        var contato = contatoRepository.findByEmail(email);
        return new ContatoResponse(contato);
    }

    @Override
    public List<ContatoResponse> buscarContatoPorNome(String nome) {
        var contato = contatoRepository.findContatoesByNomeContainingIgnoreCase(nome);
        return contato.stream()
                .map(ContatoResponse::new)
                .toList();
    }

    @Override
    public List<ContatoResponse> listarContatos() {
        return contatoRepository.findAll().stream()
                .map(ContatoResponse::new)
                .toList();
    }

    @Override
    public void deletarContato(Long id) {
        if (!contatoRepository.existsById(id)) {
            throw new RecursoNaoEncontrado("Contato não encontrado com id: " + id);
        }
        contatoRepository.deleteById(id);
    }
}
