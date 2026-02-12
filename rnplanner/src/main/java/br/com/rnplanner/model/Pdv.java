package br.com.rnplanner.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data // Cria Getters, Setters, equals, hashCode e toString automaticamente
@NoArgsConstructor // Cria o construtor vazio exigido pelo Hibernate
@AllArgsConstructor // Cria um construtor com todos os campos
public class Pdv {

    @Id
    // Sem @GeneratedValue para aceitar o ID 38725 da Ambev
    private Long id;

    private Integer codigo;

    private String nome;
}