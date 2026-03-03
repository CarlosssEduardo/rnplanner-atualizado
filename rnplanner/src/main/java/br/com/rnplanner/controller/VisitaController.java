package br.com.rnplanner.controller;

import br.com.rnplanner.dto.*;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.model.PendenciaManual;
import br.com.rnplanner.service.VisitaService;
import br.com.rnplanner.repository.PendenciaManualRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/visitas")
@CrossOrigin(origins = "*")
public class VisitaController {

    private final VisitaService visitaService;
    private final PendenciaManualRepository pendenciaManualRepository;

    public VisitaController(VisitaService visitaService, PendenciaManualRepository pendenciaManualRepository) {
        this.visitaService = visitaService;
        this.pendenciaManualRepository = pendenciaManualRepository;
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

    // 🔥 CORREÇÃO: Agora o endpoint do mês aceita o setor na URL
    @GetMapping("/dashboard/mes/{setor}")
    public ResponseEntity<ResumoMesDTO> obterDashboardMes(@PathVariable String setor) {
        return ResponseEntity.ok(visitaService.obterResumoMes(setor));
    }

    @GetMapping
    public ResponseEntity<List<Visita>> listarTodas() {
        return ResponseEntity.ok(visitaService.listarTodas());
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<VisitaRelatorioDTO> obterResumo(@PathVariable Long id) {
        return ResponseEntity.ok(visitaService.obterResumo(id));
    }

    @GetMapping("/pendencias/{setor}")
    public ResponseEntity<List<PendenciaDTO>> listarPendenciasGlobais(@PathVariable String setor) {
        List<PendenciaDTO> pendencias = new ArrayList<>(visitaService.listarPendenciasGlobaisPorSetor(setor));
        List<PendenciaManual> manuais = pendenciaManualRepository.findBySetorAndStatus(setor, "PENDENTE");

        for (PendenciaManual pm : manuais) {
            PendenciaDTO dto = new PendenciaDTO();
            dto.setId("MANUAL-" + pm.getId());
            dto.setPdvId(0L);
            dto.setPdvNome("Anotação Avulsa");
            dto.setTexto(pm.getTexto());
            dto.setStatus(pm.getStatus());
            pendencias.add(dto);
        }
        return ResponseEntity.ok(pendencias);
    }
}