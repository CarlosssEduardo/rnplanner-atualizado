package br.com.rnplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class LancamentoManual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String setor;
    private LocalDate data;

    private Integer tasks = 0;
    private Integer ofertas = 0;
    private Integer missoes = 0;
}
