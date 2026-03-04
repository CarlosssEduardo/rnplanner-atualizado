package br.com.rnplanner.repository;

import br.com.rnplanner.model.LancamentoManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LancamentoManualRepository extends JpaRepository<LancamentoManual, Long> {

    List<LancamentoManual> findBySetorAndData(String setor, LocalDate data);

    @Query("SELECT COALESCE(SUM(l.tasks), 0) FROM LancamentoManual l WHERE l.setor = :setor AND l.data = :data")
    long sumTasksManuais(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(l.ofertas), 0) FROM LancamentoManual l WHERE l.setor = :setor AND l.data = :data")
    long sumOfertasManuais(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(l.missoes), 0) FROM LancamentoManual l WHERE l.setor = :setor AND l.data = :data")
    long sumMissoesManuais(@Param("data") LocalDate data, @Param("setor") String setor);

    @Query("SELECT COALESCE(SUM(l.tasks), 0) FROM LancamentoManual l WHERE l.setor = :setor AND l.data >= :inicio AND l.data <= :fim")
    long sumTasksManuaisNoMes(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, @Param("setor") String setor);
}