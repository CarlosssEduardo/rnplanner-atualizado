package br.com.rnplanner.service;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.repository.PdvRepository;
import br.com.rnplanner.repository.VisitaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final PdvRepository pdvRepository;

    public VisitaService(VisitaRepository visitaRepository,
                         PdvRepository pdvRepository) {
        this.visitaRepository = visitaRepository;
        this.pdvRepository = pdvRepository;
    }

    // ✅ Iniciar uma nova visita (Onde você usa o ID 38725 da Ambev)
    public Visita iniciarVisita(Long pdvId) {
        Pdv pdv = pdvRepository.findById(pdvId)
                .orElseThrow(() -> new RuntimeException("PDV não encontrado com o ID: " + pdvId));

        Visita visita = new Visita();
        visita.setData(LocalDate.now());
        visita.setPdv(pdv);
        visita.setFinalizada(false);

        return visitaRepository.save(visita);
    }

    // ✅ NOVO: Buscar visita por ID (Resolve o erro 404 do GET /visitas/1)
    public Visita buscarPorId(Long id) {
        return visitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visita não encontrada com o ID: " + id));
    }

    // ✅ NOVO: Listar todas as visitas (Útil para o seu relatório de PFE)
    public List<Visita> listarTodas() {
        return visitaRepository.findAll();
    }
}