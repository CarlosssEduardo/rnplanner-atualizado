package br.com.rnplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
 * DTO do Dashboard do Dia
 *
 * Serve para enviar ao app os números gerais do dia
 * Não é tabela do banco
 * Não é entidade
 * É só resposta da API
 */
@Data // Gera getters e setters automaticamente
@AllArgsConstructor // Cria construtor com todos os campos
public class DashboardDiaDTO {

    /*
     * Quantas MISSÕES foram feitas hoje
     */
    private long missoesTotal;

    /*
     * Quantas TASKS foram feitas
     */
    private long tasksTotal;

    /*
     * Quantas OFERTAS registradas
     */
    private long ofertasTotal;

    /*
     * Quantos PDVs foram finalizados hoje
     */
    private long pdvsVisitados;
}
