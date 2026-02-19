package br.com.rnplanner.repository;

// Importa a entidade Pdv (a tabela do banco)
import br.com.rnplanner.model.Pdv;

// Importa o JpaRepository que já traz CRUD pronto
import org.springframework.data.jpa.repository.JpaRepository;

// Diz para o Spring que essa ‘interface’ é um Repository (camada de dados)
import org.springframework.stereotype.Repository;

import java.util.List;

// Marca como componente de acesso ao banco
@Repository
public interface PdvRepository extends JpaRepository<Pdv, Long> {

    /*
     * JpaRepository<Pdv, Long> já entrega:
     * save()
     * findById()
     * findAll()
     * delete()
     * etc…
     */

    // 👇 Metodo customizado criado só pelo nome
    // O Spring lê o nome e monta automaticamente o SQL:
    // SELECT * FROM pdv WHERE dia_semana = ?
    // IgnoreCase → ignora maiúsculo/minúsculo
    List<Pdv> findByDiaSemanaIgnoreCase(String diaSemana);

}
