package br.com.rnplanner.service;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.dto.PendenciaDTO;
import br.com.rnplanner.dto.ResumoMesDTO;
import br.com.rnplanner.dto.VisitaRelatorioDTO;
import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.repository.PdvRepository;
import br.com.rnplanner.repository.VisitaRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final PdvRepository pdvRepository;

    @Transactional
    public Visita iniciarVisita(Long pdvId) {
        Pdv pdv = pdvRepository.findById(pdvId)
                .orElseThrow(() -> new RuntimeException("PDV não encontrado."));

        LocalDate hoje = LocalDate.now();

        return visitaRepository.findFirstByPdvIdAndDataOrderByIdDesc(pdvId, hoje)
                .orElseGet(() -> {
                    String historico = visitaRepository
                            .findFirstByPdvIdOrderByIdDesc(pdvId)
                            .map(Visita::getObservacao)
                            .orElse("");

                    Visita novaVisita = new Visita();
                    novaVisita.setData(hoje);
                    novaVisita.setPdv(pdv);
                    novaVisita.setFinalizada(false);
                    novaVisita.setObservacao(historico);
                    novaVisita.setQtdTasks(0);
                    novaVisita.setQtdOfertas(0);
                    novaVisita.setQtdMissoes(0);

                    log.info("Iniciando nova visita para o PDV: {}", pdv.getNome());
                    return visitaRepository.save(novaVisita);
                });
    }

    @Transactional
    public Visita finalizarVisita(Long visitaId, String anotacao, int qtdTasks, int qtdOfertas, int qtdMissoes) {
        Visita visita = buscarPorId(visitaId);

        visita.setObservacao(anotacao);
        visita.setQtdTasks(qtdTasks);
        visita.setQtdOfertas(qtdOfertas);
        visita.setQtdMissoes(qtdMissoes);
        visita.setFinalizada(true);

        log.info("Visita {} finalizada com sucesso.", visitaId);
        return visitaRepository.save(visita);
    }

    // 🔥 ERRO 2 RESOLVIDO AQUI: Construtor cheio acionado!
    public DashboardDiaDTO obterDashboardDoDiaPorSetor(String setor) {
        LocalDate hoje = LocalDate.now();

        long tasks = visitaRepository.sumTasksByDataAndSetor(hoje, setor);
        long ofertas = visitaRepository.sumOfertasByDataAndSetor(hoje, setor);
        long missoes = visitaRepository.sumMissoesByDataAndSetor(hoje, setor);
        List<Long> visitados = visitaRepository.findPdvIdsVisitadosHojePorSetor(hoje, setor);

        long pdvs = visitados.size(); // Conta a quantidade de clientes visitados do setor

        // Envia tudo na ordem que a sua classe DTO exige: (missoes, tasks, ofertas, pdvs, lista)
        return new DashboardDiaDTO(missoes, tasks, ofertas, pdvs, visitados);
    }

    public Visita buscarPorId(Long id) {
        return visitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visita não encontrada."));
    }

    public List<Visita> listarTodas() {
        return visitaRepository.findAll();
    }

    public VisitaRelatorioDTO obterResumo(Long visitaId) {
        Visita v = buscarPorId(visitaId);
        return new VisitaRelatorioDTO(
                v.getQtdMissoes(),
                v.getQtdTasks(),
                v.getQtdOfertas(),
                v.getObservacao()
        );
    }

    public ResumoMesDTO obterResumoMes() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        int diasTrabalhados = visitaRepository.countDiasTrabalhadosNoMes(inicioMes, fimMes);
        int totalTasks = visitaRepository.sumTasksNoMes(inicioMes, fimMes);
        List<String> observacoes = visitaRepository.findAllObservacoesNoMes(inicioMes, fimMes);

        int problemasResolvidos = 0;
        for (String obs : observacoes) {
            if (obs != null && obs.contains("\"status\":\"RESOLVIDO\"")) {
                problemasResolvidos += obs.split("\"status\":\"RESOLVIDO\"").length - 1;
            }
        }

        String ranking = "Top 10 - CDD Belém";
        return new ResumoMesDTO(diasTrabalhados, problemasResolvidos, totalTasks, ranking);
    }

    public List<PendenciaDTO> listarPendenciasGlobaisPorSetor(String setor) {
        List<PendenciaDTO> listaGlobal = new ArrayList<>();

        List<Pdv> pdvsDoSetor = pdvRepository.findBySetor(setor);
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        for (Pdv pdv : pdvsDoSetor) {
            visitaRepository.findFirstByPdvIdOrderByIdDesc(pdv.getId()).ifPresent(v -> {
                if (v.getObservacao() != null && v.getObservacao().trim().startsWith("[")) {
                    try {
                        PendenciaDTO[] pendencias = mapper.readValue(v.getObservacao(), PendenciaDTO[].class);
                        for (PendenciaDTO p : pendencias) {
                            p.setPdvId(pdv.getId());
                            p.setPdvNome(pdv.getNome());
                            listaGlobal.add(p);
                        }
                    } catch (Exception e) {
                        log.error("Erro ao processar JSON da Visita {}: {}", v.getId(), e.getMessage());
                    }
                }
            });
        }
        return listaGlobal;
    }
}