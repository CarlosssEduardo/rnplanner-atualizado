package br.com.rnplanner.controller;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.repository.VisitaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final VisitaRepository visitaRepository;

    public DashboardController(VisitaRepository visitaRepository) {
        this.visitaRepository = visitaRepository;
    }

    /*
     * GET /dashboard/resumo-do-dia/setor/{setor}
     * Alimenta as 3 janelas do app e manda os IDs para substituir o #
     */
    @GetMapping("/resumo-do-dia/setor/{setor}")
    public ResponseEntity<DashboardDiaDTO> obterResumoDoDia(@PathVariable String setor) {
        LocalDate hoje = LocalDate.now();

        // 1. Busca as contagens blindadas pelo setor
        long missoesTotal = visitaRepository.sumMissoesByDataAndSetor(hoje, setor);
        long tasksTotal = visitaRepository.sumTasksByDataAndSetor(hoje, setor);
        long ofertasTotal = visitaRepository.sumOfertasByDataAndSetor(hoje, setor);

        // 2. Busca os IDs dos clientes visitados hoje para o app mostrar os códigos
        List<Long> pdvsVisitadosIds = visitaRepository.findPdvIdsVisitadosHojePorSetor(hoje, setor);
        long pdvsVisitados = pdvsVisitadosIds.size();

        // 3. Monta o envelope (DTO) que está lá na sua pasta DTO
        DashboardDiaDTO resumo = new DashboardDiaDTO(
                missoesTotal,
                tasksTotal,
                ofertasTotal,
                pdvsVisitados,
                pdvsVisitadosIds
        );

        return ResponseEntity.ok(resumo);
    }
}