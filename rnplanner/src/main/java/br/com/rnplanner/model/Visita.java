package br.com.rnplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Visita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pdv_id")
    private Pdv pdv;

    private LocalDate data;

    // 🔥 AS GAVETAS QUE ESTÃO FALTANDO:
    private LocalDate dataExibicao;
    private String setor;
    private String pendenciaStatus; // "PENDENTE" ou "RESOLVIDO"

    private String observacao;
    private int qtdTasks;
    private int qtdOfertas;
    private int qtdMissoes;
    private boolean finalizada;
}