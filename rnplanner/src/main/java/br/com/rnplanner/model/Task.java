package br.com.rnplanner.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🎯 RESPONSABILIDADE: Gerenciar as ações operacionais de uma visita.
 * Esta classe é o "check-list" do vendedor. Ela define o que deve ser feito,
 * o status da tarefa e a qual visita ela pertence.
 */
@Entity
@Data
@NoArgsConstructor // 🛠️ Essencial para o Hibernate criar o objeto antes de preencher os dados.
@AllArgsConstructor
@Builder // 🏗️ Permite criar Tasks de forma fluida: Task.builder().descricao("ABC").build();
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** * 🆔 CHAVE PRIMÁRIA: O banco gera o número automaticamente (1, 2, 3...).
     * Diferente do PDV, aqui deixamos o banco controlar para evitar conflitos.
     */
    private Long id;

    private String descricao;

    private boolean concluida;

    @Enumerated(EnumType.STRING)
    /**
     * 🏷️ CATEGORIZAÇÃO: Salva o NOME do tipo (ex: "OFERTA") no banco em vez de um número.
     * Isso facilita a leitura direta no banco de dados durante o debug.
     */
    private TipoTask tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visita_id")
    @JsonBackReference
    /**
     * 🔗 O ELO COM A VISITA:
     * 1. ManyToOne: Muitas tarefas para uma única visita.
     * 2. LAZY: "Estratégia Preguiçosa" - Só carrega a visita se o código pedir. Economiza memória.
     * 3. JoinColumn: Cria a coluna 'visita_id' no banco, que é o "crachá" de identificação do pai.
     * 4. JsonBackReference: O "Escudo" que impede o app de entrar em loop infinito no JSON.
     */
    private Visita visita;

    /**
     * 🚦 REGRAS DE NEGÓCIO: Define os únicos tipos de tarefas aceitos pelo sistema.
     */
    public enum TipoTask {
        TASK,
        OFERTA,
        MISSAO
    }
}