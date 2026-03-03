package br.com.rnplanner.dto;

import lombok.Data;

@Data
public class FinalizarVisitaDTO {
    private String anotacao;
    private int qtdTasks;
    private int qtdOfertas;
    private int qtdMissoes;
}