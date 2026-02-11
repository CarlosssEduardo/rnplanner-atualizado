package br.com.rnplanner.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;

    private String observacao;

    private boolean finalizada;

    @ManyToOne
    @JoinColumn(name = "pdv_id")
    private Pdv pdv;

    @OneToMany(mappedBy = "visita", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Task> tasks;


    // getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    public Pdv getPdv() {
        return pdv;
    }

    public void setPdv(Pdv pdv) {
        this.pdv = pdv;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
