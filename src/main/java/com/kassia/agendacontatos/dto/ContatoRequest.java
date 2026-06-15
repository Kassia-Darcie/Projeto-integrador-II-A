package com.kassia.agendacontatos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContatoRequest(
        @NotBlank
        String nome,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "O telefone deve conter apenas números e ter 11 dígitos")
        String telefone,
        @NotBlank @Email
        String email
) {
}
