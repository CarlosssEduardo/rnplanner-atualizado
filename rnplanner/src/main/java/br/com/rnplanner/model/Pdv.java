package br.com.rnplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🎯 RESPONSABILIDADE: Representar o Ponto de Venda (PDV) no ecossistema digital.
 * Esta classe é o espelho das planilhas de rotas. Ela transforma os dados
 * estáticos do Excel em objetos dinâmicos que o sistema consegue gerenciar.
 */
@Entity
@Data // ⚙️ Gera Getters, Setters e métodos auxiliares para manter o código limpo.
@NoArgsConstructor // 🛠️ Construtor padrão exigido pelo JPA para criar o objeto ao consultar o banco.
@AllArgsConstructor // 🏗️ Facilita a criação rápida de objetos: new Pdv(1L, 123, "Bar do Zé", "Segunda");
public class Pdv {

    @Id
    /**
     * 🆔 CHAVE PRIMÁRIA: identificador exclusivo do PDV.
     * Como você está migrando dados de uma base externa (Excel), usamos este campo
     * para garantir que não existam dois pontos de venda com o mesmo ID no sistema.
     */
    private Long id;

    /**
     * 🔢 CÓDIGO LOGÍSTICO: O número de identificação do cliente na base da Ambev.
     */
    private Integer codigo;

    /**
     * 🏢 NOME COMERCIAL: Nome fantasia do estabelecimento que o vendedor visualizará no app.
     */
    private String nome;

    /**
     * 🗓️ INTELIGÊNCIA DE ROTA: Define em qual dia da semana este PDV deve ser visitado.
     * É o atributo principal para o filtro de planejamento diário do representante.
     */
    private String diaSemana;
}