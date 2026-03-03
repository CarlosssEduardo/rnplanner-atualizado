package br.com.rnplanner.repository;

import br.com.rnplanner.model.SetorPermitido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetorPermitidoRepository extends JpaRepository<SetorPermitido, Long> {

    // O Spring Boot cria a query sozinho: "Tem esse setor na tabela?"
    boolean existsBySetor(String setor);
}