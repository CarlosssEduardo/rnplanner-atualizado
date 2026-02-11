package br.com.rnplanner.repository;

import br.com.rnplanner.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VisitaRepository extends JpaRepository<Visita, Long> {

    List<Visita> findByData(LocalDate data);
}
