package br.com.rnplanner.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Setor_permitido")
public class SetorPermitido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A chave da porta! Ex: "501", "502", "503"
    @Column(unique = true, nullable = false)
    private String setor;
}