package br.com.rnplanner.repository;

import br.com.rnplanner.model.Pdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdvRepository extends JpaRepository<Pdv, Long> {

    // Busca clientes de um dia específico (Ignorando maiúsculas/minúsculas)
    List<Pdv> findByDiaSemanaIgnoreCase(String diaSemana);

    // Busca apenas os clientes do setor específico
    List<Pdv> findBySetor(String setor);

    // 🔥 O COMANDO CIRÚRGICO: Apaga apenas os PDVs dos setores que estão sendo atualizados no Excel
    void deleteBySetorIn(List<String> setores);

    // 🔥 O VERIFICADOR DE PORTA
    boolean existsBySetor(String setor);

}