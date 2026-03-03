package br.com.rnplanner.controller;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.dto.ResumoMesDTO;
import br.com.rnplanner.repository.VisitaRepository;
import br.com.rnplanner.repository.LancamentoManualRepository;
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

    public DashboardController(VisitaRepository visitaRepository, LancamentoManualRepository lancamentoManualRepository) {
        this.visitaRepository = visitaRepository;
        this.lancamentoManualRepository = lancamentoManualRepository;
    }

    // 📍 RESUMO DIÁRIO (PARA A BARRA DE PROGRESSO COM FOGUINHO)
    @GetMapping("/resumo-do-dia/setor/{setor}")
    public ResponseEntity<DashboardDiaDTO> obterResumoDoDia(@PathVariable String setor) {
        LocalDate hoje = LocalDate.now();

        // 🔥 TORNEIRA 1: Contagem das Visitas Oficiais do Setor
        long missoesVisita = visitaRepository.sumMissoesByDataAndSetor(hoje, setor);
        long tasksVisita = visitaRepository.sumTasksByDataAndSetor(hoje, setor);
        long ofertasVisita = visitaRepository.sumOfertasByDataAndSetor(hoje, setor);

        // 🔥 TORNEIRA 2: Contagem dos Lançamentos Manuais (Hub) do Setor
        long missoesManuais = lancamentoManualRepository.sumMissoesManuais(hoje, setor);
        long tasksManuais = lancamentoManualRepository.sumTasksManuais(hoje, setor);
        long ofertasManuais = lancamentoManualRepository.sumOfertasManuais(hoje, setor);

        // 🔥 O FUNIL: Soma tudo para a performance individual
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

    // 🏆 NOVO: RESUMO DAS CONQUISTAS DO MÊS (INDIVIDUAL POR SETOR)
    @GetMapping("/resumo-mensal/setor/{setor}")
    public ResponseEntity<ResumoMesDTO> obterResumoMensal(@PathVariable String setor) {
        // Define o intervalo do mês atual
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fim = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // 🔥 A BLINDAGEM PARA A AZURE ESTÁ AQUI: Adicionado o cast (int)
        int diasTrabalhados = (int) visitaRepository.countDiasTrabalhadosNoMesPorSetor(inicio, fim, setor);

        // 🔥 E AQUI: Adicionado o cast (int)
        int problemasResolvidos = (int) visitaRepository.countProblemasResolvidosNoMesPorSetor(inicio, fim, setor);

        // ⚡ 3. Total de Tasks no Mês (Funil: Visitas Oficiais + Hub de Execução)
        long tasksVisitas = visitaRepository.sumTasksNoMesPorSetor(inicio, fim, setor);
        long tasksHub = lancamentoManualRepository.sumTasksManuaisNoMes(inicio, fim, setor);
        int totalTasksMes = (int) (tasksVisitas + tasksHub);

        // 🏅 4. Ranking (Individualizado para o contexto do setor)
        String ranking = "Top 10 - CDD Belém";

        ResumoMesDTO resumoMensal = new ResumoMesDTO(
                diasTrabalhados,
                problemasResolvidos,
                totalTasksMes,
                ranking
        );

        return ResponseEntity.ok(resumoMensal);
    }
}