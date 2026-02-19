package br.com.rnplanner.repository;

// Importa a entidade Visita (tabela visita no banco)
import br.com.rnplanner.model.Visita;

// JpaRepository → CRUD automático (save, findAll, delete…)
import org.springframework.data.jpa.repository.JpaRepository;

// Marca como camada de acesso ao banco
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

// Diz ao Spring que isso acessa o banco
@Repository
public interface VisitaRepository extends JpaRepository<Visita, Long> {

    /*
     * Conta quantos PDVs foram FINALIZADOS hoje
     *
     * SELECT COUNT(*) FROM visita
     * WHERE data = hoje AND finalizada = true
     *
     * Usado no dashboard principal
     * → produtividade do dia
     */
    long countByDataAndFinalizadaTrue(LocalDate data);


    /*
     * Busca a visita mais recente de um PDV
     *
     * findFirst → pega só 1
     * OrderByDataDesc → a mais nova primeiro
     *
     * SELECT * FROM visita
     * WHERE pdv_id = ?
     * ORDER BY data DESC
     * LIMIT 1
     *
     * Retorna Optional porque pode não existir visita ainda
     */
    Optional<Visita> findFirstByPdvIdOrderByDataDesc(Long pdvId);
}
