package br.com.rnplanner.service;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.repository.PdvRepository;
import br.com.rnplanner.repository.VisitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdvService {

    private final PdvRepository pdvRepository;
    private final VisitaRepository visitaRepository; // 🔥 Trazido de volta para blindar o banco!

    @Transactional
    public void importarExcel(MultipartFile file) throws Exception {

        List<Pdv> todosPdvs = new ArrayList<>();
        // 🔥 O CADERNINHO TÁTICO: Vai anotar quais setores estão neste Excel específico
        Set<String> setoresNoArquivo = new HashSet<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

                Sheet sheet = workbook.getSheetAt(i);
                String diaSemana = sheet.getSheetName();

                log.info("Processando aba: {}", diaSemana);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    Pdv pdv = processarLinha(row, diaSemana);

                    if (pdv != null) {
                        todosPdvs.add(pdv);
                        setoresNoArquivo.add(pdv.getSetor()); // Anota o setor na lista
                    }
                }
            }

            if (!todosPdvs.isEmpty()) {
                // Transforma o caderninho em uma lista para o banco de dados
                List<String> setoresParaAtualizar = new ArrayList<>(setoresNoArquivo);
                log.info("Atualizando inteligentemente os setores: {}", setoresParaAtualizar);

                // 🔥 PASSO 1: Limpa as VISITAS antigas apenas dos setores que estão sendo atualizados (Evita erro 23503)
                visitaRepository.deleteByPdvSetorIn(setoresParaAtualizar);

                // 🔥 PASSO 2: Limpa os PDVS antigos apenas dos setores que estão sendo atualizados
                pdvRepository.deleteBySetorIn(setoresParaAtualizar);

                // 🔥 PASSO 3: Salva os dados novos! (O 501 e 503 continuam intactos se não estiverem na lista)
                pdvRepository.saveAll(todosPdvs);
                log.info("Sucesso! {} PDVs atualizados com Padrão Ouro.", todosPdvs.size());
            }
        }
    }

    private Pdv processarLinha(Row row, String diaSemana) {

        try {
            DataFormatter formatter = new DataFormatter();

            String setorStr = formatter.formatCellValue(row.getCell(0)).trim();
            String idStr = formatter.formatCellValue(row.getCell(1)).trim();
            String nomeStr = formatter.formatCellValue(row.getCell(2)).trim();

            if (idStr.isEmpty()) return null;

            Pdv pdv = new Pdv();
            pdv.setId(Long.parseLong(idStr));
            pdv.setNome(nomeStr);
            pdv.setSetor(setorStr);
            pdv.setDiaSemana(diaSemana);

            try {
                pdv.setCodigo(Integer.parseInt(idStr));
            } catch (Exception ignored) {}

            return pdv;

        } catch (Exception e) {
            log.error("Erro na linha {} da aba {}: {}", row.getRowNum() + 1, diaSemana, e.getMessage());
            return null;
        }
    }

    public List<Pdv> listarPorDia(String dia) {
        return pdvRepository.findByDiaSemanaIgnoreCase(dia);
    }
}