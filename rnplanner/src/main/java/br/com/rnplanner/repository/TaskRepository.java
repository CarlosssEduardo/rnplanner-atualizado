package br.com.rnplanner.repository;

// Importa a entidade Task (tabela do banco)
import br.com.rnplanner.model.Task;

// Importa o ENUM que define o tipo da task (MISSAO, TASK, OFERTA)
import br.com.rnplanner.model.Task.TipoTask;

// JpaRepository → CRUD automático
import org.springframework.data.jpa.repository.JpaRepository;

// Marca como camada de acesso ao banco
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Diz ao Spring que isso é um Repository
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /*
     * Busca todas as tasks de uma visita específica
     * SELECT * FROM task WHERE visita_id = ?
     */
    List<Task> findByVisitaId(Long visitaId);


    /*
     * Conta quantas tasks existem numa visita por tipo
     * Ex:
     * quantas MISSAO nessa visita
     * quantas OFERTA nessa visita
     */
    long countByVisitaIdAndTipo(Long visitaId, TipoTask tipo);


    /*
     * Contagem ACUMULADA do dia inteiro
     *
     * O Spring faz JOIN automático:
     * Task → Visita → Data
     *
     * Ex:
     * quantas MISSAO hoje no sistema inteiro
     * quantas TASK hoje
     * quantas OFERTA hoje
     */
    long countByTipoAndVisitaData(TipoTask tipo, LocalDate data);
}
