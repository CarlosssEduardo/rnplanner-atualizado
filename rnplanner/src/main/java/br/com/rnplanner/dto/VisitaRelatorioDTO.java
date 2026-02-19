package br.com.rnplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
 * DTO de RELATÓRIO DA VISITA
 *
 * Serve para mostrar o progresso de um PDV específico
 * Não é tabela
 * Não salva no banco
 * É só resposta da API
 */
@Data // Cria getters e setters automaticamente
@AllArgsConstructor // Construtor com todos os campos
public class VisitaRelatorioDTO {

    /*
     * Quantas missões foram feitas nessa visita
     */
    private long missoes;

    /*
     * Quantas tasks normais
     */
    private long tasks;

    /*
     * Quantas ofertas registradas
     */
    private long ofertas;

    /*
     * Observação da última visita
     * (o famoso caderno digital)
     */
    private String observacaoAnterior; // Para o feedback
}
