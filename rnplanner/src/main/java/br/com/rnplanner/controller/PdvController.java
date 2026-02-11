package br.com.rnplanner.controller;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.repository.PdvRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pdvs")
public class PdvController {

    private final PdvRepository repository;

    public PdvController(PdvRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Pdv criar(@RequestBody Pdv pdv) {
        return repository.save(pdv);
    }

    @GetMapping
    public List<Pdv> listar() {
        return repository.findAll();
    }
}
