package br.com.rnplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitaRelatorioDTO {
    private String pdvNome;
    private String anotacao;
    private int qtdTasks;
    private int qtdOfertas;
    private int qtdMissoes;
}