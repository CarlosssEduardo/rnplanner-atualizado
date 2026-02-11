package br.com.rnplanner.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;


@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private boolean concluida;

    @Enumerated(EnumType.STRING)
    private TipoTask tipo;

    @ManyToOne
    @JoinColumn(name = "visita_id")
    @JsonBackReference
    private Visita visita;


    public enum TipoTask {
        MISSAO,
        OFERTA,
        TASK,
        PONTO_EXTRA
    }

    // getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public TipoTask getTipo() {
        return tipo;
    }

    public void setTipo(TipoTask tipo) {
        this.tipo = tipo;
    }

    public Visita getVisita() {
        return visita;
    }

    public void setVisita(Visita visita) {
        this.visita = visita;
    }
}
