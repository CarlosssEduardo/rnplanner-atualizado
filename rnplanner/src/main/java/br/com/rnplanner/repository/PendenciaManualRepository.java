package br.com.rnplanner.repository;

import br.com.rnplanner.model.PendenciaManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendenciaManualRepository extends JpaRepository<PendenciaManual, Long> {

    // 🔥 Essa é a linha que o Java sentiu falta e bloqueou tudo!
    List<PendenciaManual> findBySetorAndStatus(String setor, String status);

    List<PendenciaManual> findBySetor(String setor);
}