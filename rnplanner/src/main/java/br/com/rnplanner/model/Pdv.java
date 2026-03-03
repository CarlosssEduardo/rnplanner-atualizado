package br.com.rnplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pdv {

    @Id
    private Long id;

    private Integer codigo;

    private String nome;

    private String diaSemana;

    // 👉 NOVO: O CARIMBO DO SETOR (Ex: "503")
    private String setor;
}