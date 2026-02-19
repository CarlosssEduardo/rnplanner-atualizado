package br.com.rnplanner.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 🎯 RESPONSABILIDADE: Orquestrar o evento da visita em campo.
 * Esta classe é o "coração" da aplicação; ela vincula um Ponto de Venda (PDV)
 * a uma data específica e a uma lista de tarefas que devem ser executadas.
 */
@Entity
@Data
@NoArgsConstructor // 🛠️ Essencial para o Hibernate instanciar a classe ao buscar do banco.
@AllArgsConstructor
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** * 🆔 Esse campo é o identificador único da visita e o banco gera ele automaticamente quando você salva.
     */
    private Long id;

    /**
     * 📅 CONTROLE TEMPORAL: Armazena a data em que a visita foi ou será realizada.
     */
    private LocalDate data;

    @Column(columnDefinition = "TEXT")
    /**
     * 📝 FEEDBACK DO CAMPO: Campo de texto longo para observações detalhadas
     * sobre o que aconteceu durante a visita (ex: "Cliente não estava no local").
     */
    private String observacao;

    /**
     * ✅ STATUS DO CICLO DE VIDA: Indica se o atendimento foi concluído pelo representante.
     */
    private boolean finalizada;

    @ManyToOne
    @JoinColumn(name = "pdv_id")
    /**
     * 📍 LOCALIZAÇÃO: Relacionamento Muitos-para-Um.
     * Indica em qual PDV esta visita específica está ocorrendo.
     */
    private Pdv pdv;

    @OneToMany(mappedBy = "visita", cascade = CascadeType.ALL)
    @JsonManagedReference
    /**
     * 📋 COMPOSIÇÃO DE TAREFAS:
     * 1. OneToMany: Uma visita pode ter várias tarefas vinculadas.
     * 2. mappedBy: Indica que o controle do relacionamento está no campo 'visita' da classe Task.
     * 3. CascadeType.ALL: Garante a integridade; se eu salvar uma Visita, o Spring salva todas as suas Tasks automaticamente.
     * 4. JsonManagedReference: É o "Lado Mestre" do relacionamento JSON. Ele permite que as tarefas sejam listadas dentro da visita.
     */
    private List<Task> tasks;
}