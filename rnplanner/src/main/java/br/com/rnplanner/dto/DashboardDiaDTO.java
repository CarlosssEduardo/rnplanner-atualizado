package br.com.rnplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DashboardDiaDTO {
    private long missoesTotal;
    private long tasksTotal;
    private long ofertasTotal;
    private long pdvsVisitados;

    // 🔥 NOVO: A lista com os IDs dos clientes que já foram visitados hoje!
    private List<Long> pdvsVisitadosIds;
}