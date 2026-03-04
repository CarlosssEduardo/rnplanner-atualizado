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
    @Query("DELETE FROM Visita v WHERE v.setor IN :setores")
    void deleteByPdvSetorIn(@Param("setores") List<String> setores);

    // Troque as queries do dia por estas (exemplo da Task):
    @Query("SELECT COALESCE(SUM(v.qtdTasks), 0) FROM Visita v WHERE v.setor = :setor AND v.data = :data AND v.finalizada = true")
    long sumTasksByDataAndSetor(@Param("data") LocalDate data, @Param("setor") String setor);
    // Repita para Ofertas e Missões usando o parâmetro :data

    @Query("SELECT COALESCE(SUM(v.qtdOfertas), 0) FROM Visita v WHERE v.setor = :setor AND v.data = :data AND v.finalizada = true")
    long sumOfertasByDataAndSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(v.qtdMissoes), 0) FROM Visita v WHERE v.setor = :setor AND v.data = :data AND v.finalizada = true")
    long sumMissoesByDataAndSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT v.pdv.id FROM Visita v WHERE v.setor = :setor AND v.data = :data AND v.finalizada = true")
    List<Long> findPdvIdsVisitadosHojePorSetor(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT COUNT(DISTINCT v.data) FROM Visita v WHERE v.setor = :setor AND v.data >= :inicio AND v.data <= :fim AND v.finalizada = true")
    long countDiasTrabalhadosNoMesPorSetor(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(v.qtdTasks), 0) FROM Visita v WHERE v.setor = :setor AND v.data >= :inicio AND v.data <= :fim AND v.finalizada = true")
    long sumTasksNoMesPorSetor(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, @Param("setor") String setor);

    @Query("SELECT COUNT(v) FROM Visita v WHERE v.setor = :setor AND v.pendenciaStatus = 'RESOLVIDO' AND v.data >= :inicio AND v.data <= :fim")
    long countProblemasResolvidosNoMesPorSetor(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, @Param("setor") String setor);

    // 🔥 Adicione esta linha dentro do seu VisitaRepository
    Optional<Visita> findFirstByPdvIdAndDataOrderByIdDesc(Long pdvId, LocalDate data);
}