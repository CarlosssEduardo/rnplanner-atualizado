package br.com.rnplanner.controller;

import br.com.rnplanner.dto.*;
import br.com.rnplanner.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final VisitaRepository visitaRepository;
    private final LancamentoManualRepository lancamentoManualRepository;
    private final PendenciaManualRepository pendenciaManualRepository;

    public DashboardController(VisitaRepository v, LancamentoManualRepository l, PendenciaManualRepository p) {
        this.visitaRepository = v;
        this.lancamentoManualRepository = l;
        this.pendenciaManualRepository = p;
    }

    @GetMapping("/resumo-do-dia/setor/{setor}")
    public ResponseEntity<DashboardDiaDTO> obterResumoDoDia(@PathVariable String setor) {
        long tasks = visitaRepository.sumTasksByDataAndSetor(setor) + lancamentoManualRepository.sumTasksManuais(setor);
        long ofertas = visitaRepository.sumOfertasByDataAndSetor(setor) + lancamentoManualRepository.sumOfertasManuais(setor);
        long missoes = visitaRepository.sumMissoesByDataAndSetor(setor) + lancamentoManualRepository.sumMissoesManuais(setor);
        var pdvsIds = visitaRepository.findPdvIdsVisitadosHojePorSetor(setor);

        return ResponseEntity.ok(new DashboardDiaDTO(missoes, tasks, ofertas, pdvsIds.size(), pdvsIds));
    }

    @GetMapping("/resumo-mensal/setor/{setor}")
    public ResponseEntity<ResumoMesDTO> obterResumoMensal(@PathVariable String setor) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fim = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        int dias = (int) visitaRepository.countDiasTrabalhadosNoMesPorSetor(inicio, fim, setor);
        long totalTasks = visitaRepository.sumTasksNoMesPorSetor(inicio, fim, setor) + lancamentoManualRepository.sumTasksManuaisNoMes(inicio, fim, setor);
        int resolvidos = (int) (visitaRepository.countProblemasResolvidosNoMesPorSetor(inicio, fim, setor) +
                pendenciaManualRepository.findBySetorAndStatus(setor, "RESOLVIDO").size());

        return ResponseEntity.ok(new ResumoMesDTO(dias == 0 && totalTasks > 0 ? 1 : dias, resolvidos, (int) totalTasks, "Top 10 - CDD Belém"));
    }
}