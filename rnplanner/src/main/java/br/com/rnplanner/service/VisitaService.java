package br.com.rnplanner.service;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.repository.PdvRepository;
import br.com.rnplanner.repository.VisitaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final PdvRepository pdvRepository;

    public VisitaService(VisitaRepository visitaRepository,
                         PdvRepository pdvRepository) {
        this.visitaRepository = visitaRepository;
        this.pdvRepository = pdvRepository;
    }

    public Visita iniciarVisita(Long pdvId) {

        Pdv pdv = pdvRepository.findById(pdvId)
                .orElseThrow(() -> new RuntimeException("PDV não encontrado"));

        Visita visita = new Visita();
        visita.setData(LocalDate.now());
        visita.setPdv(pdv);
        visita.setFinalizada(false);

        return visitaRepository.save(visita);
    }
}
