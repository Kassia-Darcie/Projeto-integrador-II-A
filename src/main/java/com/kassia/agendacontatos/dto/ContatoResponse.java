package com.kassia.agendacontatos.dto;

import com.kassia.agendacontatos.entity.Contato;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContatoResponse(
        Long id,
        String nome,
        String telefone,
        String email
) {
        public ContatoResponse(Contato contato) {
            this(contato.getId(), contato.getNome(), contato.getTelefone(), contato.getEmail());
        }
}
