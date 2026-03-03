package br.com.rnplanner.repository;

import br.com.rnplanner.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    // A busca rápida pro vendedor no aplicativo
    Optional<Entrega> findFirstByPdvId(Long pdvId);

    // Busca o histórico antigo para calcularmos o tempo real do PDV
    @Query("SELECT e FROM Entrega e WHERE e.pdvId = :pdvId AND e.status = 'CONCLUDED' AND e.arrivedAt IS NOT NULL AND e.finishedAt IS NOT NULL")
    List<Entrega> findHistoricoConcluidoByPdvId(@Param("pdvId") Long pdvId);
}