package com.kassia.agendacontatos.entity;

import com.kassia.agendacontatos.dto.ContatoRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contatos")
@Getter
@NoArgsConstructor
public class Contato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String nome;
    @Setter
    private String email;
    @Setter
    private String telefone;

    public Contato(ContatoRequest contatoRequest) {
        this.nome = contatoRequest.nome();
        this.email = contatoRequest.email();
        this.telefone = contatoRequest.telefone();
    }
}
