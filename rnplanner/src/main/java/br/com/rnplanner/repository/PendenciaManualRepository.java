package br.com.rnplanner.repository;

import br.com.rnplanner.model.PendenciaManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendenciaManualRepository extends JpaRepository<PendenciaManual, Long> {

    // Traz todas as pendências avulsas daquele setor que ainda estão ativas
    List<PendenciaManual> findBySetorAndStatus(String setor, String status);
}