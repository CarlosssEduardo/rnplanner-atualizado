package br.com.rnplanner.controller;

import br.com.rnplanner.dto.*;
import br.com.rnplanner.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.ZoneId;

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
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        long tasks = visitaRepository.sumTasksByDataAndSetor(hoje, setor) + lancamentoManualRepository.sumTasksManuais(hoje, setor);
        long ofertas = visitaRepository.sumOfertasByDataAndSetor(hoje, setor) + lancamentoManualRepository.sumOfertasManuais(hoje, setor);
        long missoes = visitaRepository.sumMissoesByDataAndSetor(hoje, setor) + lancamentoManualRepository.sumMissoesManuais(hoje, setor);
        var pdvsIds = visitaRepository.findPdvIdsVisitadosHojePorSetor(hoje, setor);

        return ResponseEntity.ok(new DashboardDiaDTO(missoes, tasks, ofertas, pdvsIds.size(), pdvsIds));
    }

    @GetMapping("/resumo-mensal/setor/{setor}")
    public ResponseEntity<ResumoMesDTO> obterResumoMensal(@PathVariable String setor) {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());

        int dias = (int) visitaRepository.countDiasTrabalhadosNoMesPorSetor(inicio, fim, setor);
        long tasks = visitaRepository.sumTasksNoMesPorSetor(inicio, fim, setor) + lancamentoManualRepository.sumTasksManuaisNoMes(inicio, fim, setor);
        int resolvidos = (int) (visitaRepository.countProblemasResolvidosNoMesPorSetor(inicio, fim, setor) +
                pendenciaManualRepository.findBySetorAndStatus(setor, "RESOLVIDO").size());

        return ResponseEntity.ok(new ResumoMesDTO(dias == 0 && tasks > 0 ? 1 : dias, resolvidos, (int) tasks, "Top 10 - CDD Belém"));
    }
}