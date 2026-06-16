package com.kassia.agendacontatos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContatoRequest(
        @NotBlank(message = "O nome não pode estar vazio")
        String nome,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "O telefone deve conter apenas números e ter 11 dígitos")
        String telefone,
        @NotBlank(message = "O email não pode estar vazio") @Email(message = "Formato de email inválido")
        String email
) {
}
