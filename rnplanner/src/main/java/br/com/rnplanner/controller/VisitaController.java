package br.com.rnplanner.controller;

import br.com.rnplanner.dto.*;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.model.PendenciaManual;
import br.com.rnplanner.service.VisitaService;
import br.com.rnplanner.repository.PendenciaManualRepository;
import br.com.rnplanner.repository.LancamentoManualRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/visitas")
@CrossOrigin(origins = "*")
public class VisitaController {

    private final VisitaService visitaService;
    private final PendenciaManualRepository pendenciaManualRepository;
    private final LancamentoManualRepository lancamentoManualRepository;

    public VisitaController(VisitaService visitaService,
                            PendenciaManualRepository pendenciaManualRepository,
                            LancamentoManualRepository lancamentoManualRepository) {
        this.visitaService = visitaService;
        this.pendenciaManualRepository = pendenciaManualRepository;
        this.lancamentoManualRepository = lancamentoManualRepository;
    }

    @PostMapping("/iniciar/{pdvId}")
    public ResponseEntity<Visita> iniciarVisita(@PathVariable Long pdvId) {
        Visita visita = visitaService.iniciarVisita(pdvId);
        return ResponseEntity.ok(visita);
    }

    @PutMapping(value = "/{id}/finalizar", consumes = "application/json")
    public ResponseEntity<Visita> finalizar(@PathVariable Long id, @RequestBody FinalizarVisitaDTO dto) {
        Visita visitaFinalizada = visitaService.finalizarVisita(
                id, dto.getAnotacao(), dto.getQtdTasks(), dto.getQtdOfertas(), dto.getQtdMissoes()
        );
        return ResponseEntity.ok(visitaFinalizada);
    }

    @GetMapping("/dashboard/{setor}")
    public ResponseEntity<DashboardDiaDTO> obterDashboardGeral(@PathVariable String setor) {
        DashboardDiaDTO dashboard = visitaService.obterDashboardDoDiaPorSetor(setor);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/dashboard/mes/{setor}")
    public ResponseEntity<ResumoMesDTO> obterDashboardMes(@PathVariable String setor) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fim = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // 1. Pega os dados das visitas oficiais
        ResumoMesDTO resumoVisitas = visitaService.obterResumoMes(setor);

        // 🔥 O IntelliJ vai adorar isso: Declaramos a variável sem valor inicial
        long tasksHub;
        try {
            // Tenta buscar do banco
            tasksHub = lancamentoManualRepository.sumTasksManuaisNoMes(inicio, fim, setor);
        } catch (Exception e) {
            // Se der erro (ex: banco vazio), aí sim ele vira zero
            tasksHub = 0L;
        }

        // 3. Junta tudo
        int totalTasks = resumoVisitas.getTotalTasksMes() + (int) tasksHub;

        return ResponseEntity.ok(new ResumoMesDTO(
                resumoVisitas.getDiasTrabalhados(),
                resumoVisitas.getProblemasResolvidos(),
                totalTasks,
                "Top 10 - CDD Belém"
        ));
    }

    @GetMapping
    public ResponseEntity<List<Visita>> listarTodas() {
        return ResponseEntity.ok(visitaService.listarTodas());
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<VisitaRelatorioDTO> obterResumo(@PathVariable Long id) {
        return ResponseEntity.ok(visitaService.obterResumo(id));
    }

    // Adicione esta rota ao seu VisitaController.java
    @GetMapping("/{id}/itens-pendentes")
    public ResponseEntity<List<String>> obterItens(@PathVariable Long id) {
        return ResponseEntity.ok(visitaService.obterItensPendentes(id));
    }

    // Substitua o metodo que deu erro por este (por volta da linha 100)
    @GetMapping("/{id}")
    public ResponseEntity<Visita> obterPorId(@PathVariable Long id) {
        // Chamamos o service em vez do repository direto
        return ResponseEntity.ok(visitaService.buscarPorId(id));
    }

    @GetMapping("/pendencias/{setor}")
    public ResponseEntity<List<PendenciaDTO>> listarPendenciasGlobais(@PathVariable String setor) {
        List<PendenciaDTO> pendencias = new ArrayList<>(visitaService.listarPendenciasGlobaisPorSetor(setor));
        List<PendenciaManual> manuais = pendenciaManualRepository.findBySetor(setor);

        for (PendenciaManual pm : manuais) {
            PendenciaDTO dto = new PendenciaDTO();
            dto.setId("MANUAL-" + pm.getId());
            dto.setPdvId(0L);
            dto.setPdvNome("Anotação Avulsa");
            dto.setTexto(pm.getTexto());
            dto.setStatus(pm.getStatus() != null ? pm.getStatus() : "PENDENTE");
            pendencias.add(dto);
        }
        return ResponseEntity.ok(pendencias);
    }
}