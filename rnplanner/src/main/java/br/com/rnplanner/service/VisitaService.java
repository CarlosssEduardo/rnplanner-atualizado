package br.com.rnplanner.service;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.dto.PendenciaDTO;
import br.com.rnplanner.dto.ResumoMesDTO;
import br.com.rnplanner.dto.VisitaRelatorioDTO;
import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.repository.PdvRepository;
import br.com.rnplanner.repository.VisitaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final PdvRepository pdvRepository;

    public VisitaService(VisitaRepository visitaRepository, PdvRepository pdvRepository) {
        this.visitaRepository = visitaRepository;
        this.pdvRepository = pdvRepository;
    }

    public Visita iniciarVisita(Long pdvId) {
        Pdv pdv = pdvRepository.findById(pdvId).orElseThrow();
        Visita visita = new Visita();
        visita.setPdv(pdv);
        visita.setData(LocalDate.now());
        visita.setDataExibicao(LocalDate.now());
        visita.setFinalizada(false);
        visita.setSetor(pdv.getSetor());
        return visitaRepository.save(visita);
    }

    public Visita finalizarVisita(Long id, String anotacao, int tasks, int ofertas, int missoes) {
        Visita visita = visitaRepository.findById(id).orElseThrow();
        visita.setObservacao(anotacao);
        visita.setQtdTasks(tasks);
        visita.setQtdOfertas(ofertas);
        visita.setQtdMissoes(missoes);
        visita.setFinalizada(true);

        if (anotacao != null && !anotacao.isEmpty()) {
            visita.setPendenciaStatus("PENDENTE");
        }

        return visitaRepository.save(visita);
    }

    public DashboardDiaDTO obterDashboardDoDiaPorSetor(String setor) {
        LocalDate hoje = LocalDate.now();
        long tasks = visitaRepository.sumTasksByDataAndSetor(hoje, setor);
        long ofertas = visitaRepository.sumOfertasByDataAndSetor(hoje, setor);
        long missoes = visitaRepository.sumMissoesByDataAndSetor(hoje, setor);
        List<Long> ids = visitaRepository.findPdvIdsVisitadosHojePorSetor(hoje, setor);

        return new DashboardDiaDTO(missoes, tasks, ofertas, ids.size(), ids);
    }

    // 🔥 CORREÇÃO DAS CONQUISTAS DO MÊS (PASSANDO O SETOR)
    public ResumoMesDTO obterResumoMes(String setor) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fim = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        int dias = visitaRepository.countDiasTrabalhadosNoMesPorSetor(inicio, fim, setor);
        int tasks = (int) visitaRepository.sumTasksNoMesPorSetor(inicio, fim, setor);
        int resolvidos = (int) visitaRepository.countProblemasResolvidosNoMesPorSetor(inicio, fim, setor);

        return new ResumoMesDTO(dias, resolvidos, tasks, "Top 10 - CDD Belém");
    }

    public List<Visita> listarTodas() {
        return visitaRepository.findAll();
    }

    public VisitaRelatorioDTO obterResumo(Long id) {
        Visita v = visitaRepository.findById(id).orElseThrow();
        return new VisitaRelatorioDTO(v.getPdv().getNome(), v.getObservacao(), v.getQtdTasks(), v.getQtdOfertas(), v.getQtdMissoes());
    }

    public List<PendenciaDTO> listarPendenciasGlobaisPorSetor(String setor) {
        return visitaRepository.findAll().stream()
                .filter(v -> v.getSetor() != null && v.getSetor().equals(setor))
                .filter(v -> "PENDENTE".equals(v.getPendenciaStatus()))
                .map(v -> {
                    PendenciaDTO dto = new PendenciaDTO();
                    dto.setId(v.getId().toString());
                    dto.setPdvId(v.getPdv().getId());
                    dto.setPdvNome(v.getPdv().getNome());
                    dto.setTexto(v.getObservacao());
                    dto.setStatus(v.getPendenciaStatus());
                    return dto;
                }).collect(Collectors.toList());
    }
}