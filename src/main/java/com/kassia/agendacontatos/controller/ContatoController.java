package com.kassia.agendacontatos.controller;

import com.kassia.agendacontatos.dto.ContatoRequest;
import com.kassia.agendacontatos.dto.ContatoResponse;
import com.kassia.agendacontatos.entity.Contato;
import com.kassia.agendacontatos.service.ContatoService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contatos")
@AllArgsConstructor
public class ContatoController {
    private ContatoService contatoService;

    @PostMapping
    @Transactional
    public ResponseEntity<ContatoResponse> cadastrarContato(@RequestBody @Valid ContatoRequest contato) {
        var response = contatoService.salvarContato(contato);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContatoResponse>> listarContatos(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "nome", required = false) String nome
    ) {
        if(email != null) {
            var response = contatoService.buscarContatoPorEmail(email);
            return ResponseEntity.ok(List.of(response));
        }
        if(nome != null) {
            var response = contatoService.buscarContatoPorNome(nome);
            return ResponseEntity.ok(response);
        }
        var response = contatoService.listarContatos();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ContatoResponse> buscarContatoPorId(@PathVariable Long id) {
        var response = contatoService.buscarContatoPorId(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ContatoResponse> atualizarContato(@PathVariable Long id, @RequestBody @Valid ContatoRequest contato) {
        var response = contatoService.atualizarContato(id, contato);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity deletarContato(@PathVariable Long id) {
        contatoService.deletarContato(id);
        return ResponseEntity.noContent().build();
    }
}
