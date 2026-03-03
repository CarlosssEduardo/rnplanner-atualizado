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

    @Modifying
    @Transactional
    @Query("DELETE FROM Visita v WHERE v.pdv.setor IN :setores")
    void deleteByPdvSetorIn(@Param("setores") List<String> setores);

    long countByDataAndFinalizadaTrue(LocalDate data);

    Optional<Visita> findFirstByPdvIdAndDataOrderByIdDesc(Long pdvId, LocalDate data);

    Optional<Visita> findFirstByPdvIdOrderByIdDesc(Long pdvId);

    // ==========================================
    // QUERIES DO DIA (PARA A BARRA DE PROGRESSO)
    // ==========================================
    @Query("SELECT COALESCE(SUM(v.qtdTasks), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true AND v.pdv.setor = :setor")
    long sumTasksByDataAndSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(v.qtdOfertas), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true AND v.pdv.setor = :setor")
    long sumOfertasByDataAndSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(v.qtdMissoes), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true AND v.pdv.setor = :setor")
    long sumMissoesByDataAndSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT v.pdv.id FROM Visita v WHERE v.data = :data AND v.finalizada = true AND v.pdv.setor = :setor")
    List<Long> findPdvIdsVisitadosHojePorSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    // ==========================================
    // QUERIES DO MÊS (PARA O RESUMO DA JORNADA)
    // 🔥 Agora usando v.setor para ler direto da visita e evitar o zero!
    // ==========================================

    @Query("SELECT COUNT(DISTINCT v.data) FROM Visita v WHERE v.setor = :setor AND v.data >= :inicio AND v.data <= :fim AND v.finalizada = true")
    int countDiasTrabalhadosNoMesPorSetor(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(v.qtdTasks), 0) FROM Visita v WHERE v.setor = :setor AND v.data >= :inicio AND v.data <= :fim AND v.finalizada = true")
    long sumTasksNoMesPorSetor(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, @Param("setor") String setor);

    // 🔥 Conta quantos problemas foram resolvidos (status RESOLVIDO) no mês por setor
    @Query("SELECT COUNT(v) FROM Visita v WHERE v.setor = :setor AND v.pendenciaStatus = 'RESOLVIDO' AND v.data >= :inicio AND v.data <= :fim")
    long countProblemasResolvidosNoMesPorSetor(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, @Param("setor") String setor);

    @Query("SELECT v.observacao FROM Visita v WHERE v.setor = :setor AND v.data >= :inicio AND v.data <= :fim AND v.finalizada = true AND v.observacao IS NOT NULL")
    List<String> findAllObservacoesNoMesPorSetor(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, @Param("setor") String setor);

    // Queries Globais
    @Query("SELECT COALESCE(SUM(v.qtdTasks), 0) FROM Visita v WHERE v.data = :data AND v.finalizada = true")
    long sumTasksByData(@Param("data") LocalDate data);
}