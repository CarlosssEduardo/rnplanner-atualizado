package br.com.rnplanner.controller;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.repository.VisitaRepository;
import br.com.rnplanner.repository.LancamentoManualRepository; // 🔥 IMPORT NOVO
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final VisitaRepository visitaRepository;
    private final LancamentoManualRepository lancamentoManualRepository; // 🔥 REPOSITÓRIO NOVO

    // Construtor atualizado
    public DashboardController(VisitaRepository visitaRepository, LancamentoManualRepository lancamentoManualRepository) {
        this.visitaRepository = visitaRepository;
        this.lancamentoManualRepository = lancamentoManualRepository;
    }

    @GetMapping("/resumo-do-dia/setor/{setor}")
    public ResponseEntity<DashboardDiaDTO> obterResumoDoDia(@PathVariable String setor) {
        LocalDate hoje = LocalDate.now();

        // 🔥 TORNEIRA 1: Contagem das Visitas Oficiais
        long missoesVisita = visitaRepository.sumMissoesByDataAndSetor(hoje, setor);
        long tasksVisita = visitaRepository.sumTasksByDataAndSetor(hoje, setor);
        long ofertasVisita = visitaRepository.sumOfertasByDataAndSetor(hoje, setor);

        // 🔥 TORNEIRA 2: Contagem dos Lançamentos Manuais
        long missoesManuais = lancamentoManualRepository.sumMissoesManuais(hoje, setor);
        long tasksManuais = lancamentoManualRepository.sumTasksManuais(hoje, setor);
        long ofertasManuais = lancamentoManualRepository.sumOfertasManuais(hoje, setor);

        // 🔥 O FUNIL: Soma tudo para a barra de progresso!
        long missoesTotal = missoesVisita + missoesManuais;
        long tasksTotal = tasksVisita + tasksManuais;
        long ofertasTotal = ofertasVisita + ofertasManuais;

        // IDs dos clientes visitados
        List<Long> pdvsVisitadosIds = visitaRepository.findPdvIdsVisitadosHojePorSetor(hoje, setor);
        long pdvsVisitados = pdvsVisitadosIds.size();

        // Devolve o seu DTO intacto, mas agora super poderoso com os números somados!
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