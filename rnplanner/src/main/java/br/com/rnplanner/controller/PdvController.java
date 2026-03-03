package br.com.rnplanner.controller;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.service.PdvService;
import br.com.rnplanner.repository.PdvRepository;
// 🔥 IMPORT NOVO AQUI (A Lista VIP)
import br.com.rnplanner.repository.SetorPermitidoRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pdvs")
@CrossOrigin(origins = "*")
public class PdvController {

    private final PdvService pdvService;
    private final PdvRepository pdvRepository;
    // 🔥 NOVO: Injetamos o Repositório da Lista VIP aqui!
    private final SetorPermitidoRepository setorPermitidoRepository;

    // 🔥 O Construtor agora recebe os 3 (Service, PdvRepo e SetorVIPRepo)
    public PdvController(PdvService pdvService, PdvRepository pdvRepository, SetorPermitidoRepository setorPermitidoRepository) {
        this.pdvService = pdvService;
        this.pdvRepository = pdvRepository;
        this.setorPermitidoRepository = setorPermitidoRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            pdvService.importarExcel(file);
            return ResponseEntity.ok("Planilha importada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao importar planilha: " + e.getMessage());
        }
    }

    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<Pdv>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(pdvService.listarPorDia(dia));
    }

    @GetMapping("/setor/{setor}")
    public ResponseEntity<List<Pdv>> listarPdvsPorSetor(@PathVariable String setor) {
        return ResponseEntity.ok(pdvRepository.findBySetor(setor));
    }

    // 🔥 A MÁGICA DAS DUAS PORTAS ACONTECE AQUI!
    @GetMapping("/verificar/{setor}")
    public ResponseEntity<Boolean> verificarSetor(@PathVariable String setor) {
        // Verifica se o setor está na tabela VIP nova
        boolean naListaVip = setorPermitidoRepository.existsBySetor(setor);

        // Verifica se já existem PDVs reais cadastrados para esse setor (a regra antiga)
        boolean temPdvCadastrado = pdvRepository.existsBySetor(setor);

        // Se ele estiver na lista VIP OU (||) já tiver PDV, ele entra!
        boolean acessoLiberado = naListaVip || temPdvCadastrado;

        return ResponseEntity.ok(acessoLiberado);
    }
}