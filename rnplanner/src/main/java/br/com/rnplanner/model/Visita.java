package br.com.rnplanner.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 🎯 RESPONSABILIDADE: Orquestrar o evento da visita em campo.
 * Agora com Contadores de Volume e Caderno Digital (CRM).
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    private boolean finalizada;

    // 🔥 NOVOS CAMPOS: Contadores Rápidos de Volume
    @Column(columnDefinition = "integer default 0")
    private int qtdTasks = 0;

    @Column(columnDefinition = "integer default 0")
    private int qtdOfertas = 0;

    @Column(columnDefinition = "integer default 0")
    private int qtdMissoes = 0;

    @ManyToOne
    @JoinColumn(name = "pdv_id")
    private Pdv pdv;

    // Nota de Arquiteto: Mantivemos a lista antiga para não quebrar
    // os seus Controllers velhos, mas o App novo não usará mais isso!
    @OneToMany(mappedBy = "visita", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Task> tasks;
}