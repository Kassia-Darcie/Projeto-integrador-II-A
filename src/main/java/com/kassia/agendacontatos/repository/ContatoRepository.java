package com.kassia.agendacontatos.repository;

import com.kassia.agendacontatos.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Long> {
    Contato findByEmail(String email);

    List<Contato> findContatoesByNomeContainingIgnoreCase(String nome);
}
