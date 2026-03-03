package br.com.rnplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class PendenciaManual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String setor;
    private LocalDate data;

    @Column(columnDefinition = "TEXT")
    private String texto;

    private String status = "PENDENTE"; // Pode ser PENDENTE ou RESOLVIDA
}