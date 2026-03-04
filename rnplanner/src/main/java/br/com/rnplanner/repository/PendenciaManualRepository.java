package br.com.rnplanner.repository;

import br.com.rnplanner.model.PendenciaManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendenciaManualRepository extends JpaRepository<PendenciaManual, Long> {

    // 🔥 Devolvendo a função que usamos para contar os resolvidos do Hub!
    List<PendenciaManual> findBySetorAndStatus(String setor, String status);

    List<PendenciaManual> findBySetor(String setor);
}