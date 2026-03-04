package br.com.rnplanner.controller;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.dto.ResumoMesDTO;
import br.com.rnplanner.model.PendenciaManual;
import br.com.rnplanner.repository.VisitaRepository;
import br.com.rnplanner.repository.LancamentoManualRepository;
import br.com.rnplanner.repository.PendenciaManualRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final VisitaRepository visitaRepository;
    private final LancamentoManualRepository lancamentoManualRepository;
    private final PendenciaManualRepository pendenciaManualRepository; // 🔥 INJEÇÃO NOVA

    public DashboardController(VisitaRepository visitaRepository,
                               LancamentoManualRepository lancamentoManualRepository,
                               PendenciaManualRepository pendenciaManualRepository) {
        this.visitaRepository = visitaRepository;
        this.lancamentoManualRepository = lancamentoManualRepository;
        this.pendenciaManualRepository = pendenciaManualRepository;
    }

    @GetMapping("/resumo-do-dia/setor/{setor}")
    public ResponseEntity<DashboardDiaDTO> obterResumoDoDia(@PathVariable String setor) {
        LocalDate hoje = LocalDate.now();

        long missoesVisita = visitaRepository.sumMissoesByDataAndSetor(hoje, setor);
        long tasksVisita = visitaRepository.sumTasksByDataAndSetor(hoje, setor);
        long ofertasVisita = visitaRepository.sumOfertasByDataAndSetor(hoje, setor);

        long missoesManuais = 0;
        long tasksManuais = 0;
        long ofertasManuais = 0;

        // Proteção contra valores nulos se o Hub estiver vazio
        try {
            missoesManuais = lancamentoManualRepository.sumMissoesManuais(hoje, setor);
            tasksManuais = lancamentoManualRepository.sumTasksManuais(hoje, setor);
            ofertasManuais = lancamentoManualRepository.sumOfertasManuais(hoje, setor);
        } catch (Exception e) {}

        long missoesTotal = missoesVisita + missoesManuais;
        long tasksTotal = tasksVisita + tasksManuais;
        long ofertasTotal = ofertasVisita + ofertasManuais;

        List<Long> pdvsVisitadosIds = visitaRepository.findPdvIdsVisitadosHojePorSetor(hoje, setor);
        long pdvsVisitados = pdvsVisitadosIds.size();

        DashboardDiaDTO resumo = new DashboardDiaDTO(
                missoesTotal,
                tasksTotal,
                ofertasTotal,
                pdvsVisitados,
                pdvsVisitadosIds
        );

        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/resumo-mensal/setor/{setor}")
    public ResponseEntity<ResumoMesDTO> obterResumoMensal(@PathVariable String setor) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fim = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // 1. Soma das Tasks
        long tasksVisitas = visitaRepository.sumTasksNoMesPorSetor(inicio, fim, setor);
        long tasksHub = 0;
        try {
            tasksHub = lancamentoManualRepository.sumTasksManuaisNoMes(inicio, fim, setor);
        } catch (Exception e) {}
        int totalTasksMes = (int) (tasksVisitas + tasksHub);

        // 2. Dias Trabalhados
        int diasTrabalhados = (int) visitaRepository.countDiasTrabalhadosNoMesPorSetor(inicio, fim, setor);

        // 🔥 A MÁGICA DO DIA: Se você não fez visita oficial, mas lançou algo no Hub, ele conta 1 dia trabalhado!
        if (diasTrabalhados == 0 && totalTasksMes > 0) {
            diasTrabalhados = 1;
        }

        // 3. Problemas Resolvidos (Visitas + Hub)
        int problemasVisita = (int) visitaRepository.countProblemasResolvidosNoMesPorSetor(inicio, fim, setor);
        int problemasHub = 0;
        try {
            // Conta quantas pendências avulsas foram marcadas como RESOLVIDO no mês
            List<PendenciaManual> hubResolvidos = pendenciaManualRepository.findBySetorAndStatus(setor, "RESOLVIDO");
            problemasHub = hubResolvidos.size();
        } catch (Exception e) {}

        int problemasResolvidosTotal = problemasVisita + problemasHub;

        String ranking = "Top 10 - CDD Belém";

        ResumoMesDTO resumoMensal = new ResumoMesDTO(
                diasTrabalhados,
                problemasResolvidosTotal,
                totalTasksMes,
                ranking
        );

        return ResponseEntity.ok(resumoMensal);
    }
}