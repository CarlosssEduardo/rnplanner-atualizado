package br.com.rnplanner.repository;

import br.com.rnplanner.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitaRepository extends JpaRepository<Visita, Long> {

    // 🔥 A MÁGICA DA BLINDAGEM: Apaga o histórico de visitas vinculadas aos setores do Excel
    // Isso impede o erro de integridade (Foreign Key) quando o sistema for apagar o PDV.
    @Modifying
    @Transactional
    @Query("DELETE FROM Visita v WHERE v.pdv.setor IN :setores")
    void deleteByPdvSetorIn(@Param("setores") List<String> setores);

    long countByDataAndFinalizadaTrue(LocalDate data);

    // 🔥 BUSCA ESSENCIAL: Encontra a visita de hoje para não zerar os contadores
    Optional<Visita> findFirstByPdvIdAndDataOrderByIdDesc(Long pdvId, LocalDate data);

    // Busca o histórico mais recente (de qualquer data) para as observações
    Optional<Visita> findFirstByPdvIdOrderByIdDesc(Long pdvId);

    // Queries do Dia
    @Query("SELECT COALESCE(SUM(v.qtdTasks), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true")
    long sumTasksByData(@Param("data") LocalDate data);

    @Query("SELECT COALESCE(SUM(v.qtdOfertas), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true")
    long sumOfertasByData(@Param("data") LocalDate data);

    @Query("SELECT COALESCE(SUM(v.qtdMissoes), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true")
    long sumMissoesByData(@Param("data") LocalDate data);

    @Query("SELECT v.pdv.id FROM Visita v WHERE v.data = :data AND v.finalizada = true")
    List<Long> findPdvIdsVisitadosHoje(@Param("data") LocalDate data);

    // Queries do Mês
    @Query("SELECT COUNT(DISTINCT v.data) FROM Visita v WHERE v.data >= :inicio AND v.data <= :fim AND v.finalizada = true")
    int countDiasTrabalhadosNoMes(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT COALESCE(SUM(v.qtdTasks), 0) FROM Visita v WHERE v.data >= :inicio AND v.data <= :fim AND v.finalizada = true")
    int sumTasksNoMes(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT v.observacao FROM Visita v WHERE v.data >= :inicio AND v.data <= :fim AND v.finalizada = true AND v.observacao IS NOT NULL")
    List<String> findAllObservacoesNoMes(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // 🔥 Queries do Dia (Agora blindadas por Setor)
    @Query("SELECT COALESCE(SUM(v.qtdTasks), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true AND v.pdv.setor = :setor")
    long sumTasksByDataAndSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(v.qtdOfertas), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true AND v.pdv.setor = :setor")
    long sumOfertasByDataAndSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(v.qtdMissoes), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true AND v.pdv.setor = :setor")
    long sumMissoesByDataAndSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT v.pdv.id FROM Visita v WHERE v.data = :data AND v.finalizada = true AND v.pdv.setor = :setor")
    List<Long> findPdvIdsVisitadosHojePorSetor(@Param("data") LocalDate data, @Param("setor") String setor);
}