package br.com.rnplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumoMesDTO {
    private int diasTrabalhados;
    private int problemasResolvidos;
    private int totalTasksMes;
    private String ranking;
}