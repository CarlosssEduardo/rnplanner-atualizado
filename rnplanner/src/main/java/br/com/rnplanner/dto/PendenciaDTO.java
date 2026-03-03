package br.com.rnplanner.dto;

import lombok.Data;

@Data
public class PendenciaDTO {
    private String id;
    private Long pdvId;
    private String pdvNome;
    private String texto;
    private String status;
}