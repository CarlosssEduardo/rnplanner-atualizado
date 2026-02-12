package br.com.rnplanner.service;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.repository.PdvRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdvService {

    @Autowired
    private PdvRepository pdvRepository;

    public void importarExcel(MultipartFile file) throws Exception {
        List<Pdv> pdvs = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // Pula o cabeçalho (Linha 0: PDV | Nome)
                if (row.getRowNum() == 0) continue;

                Pdv pdv = new Pdv();

                Cell cellId = row.getCell(0);   // Coluna A (ID do PDV)
                Cell cellNome = row.getCell(1); // Coluna B (Nome)

                if (cellId != null && cellNome != null) {
                    try {
                        // TRATAMENTO DO ID: Lê se for número ou se o Excel salvou como texto
                        if (cellId.getCellType() == CellType.NUMERIC) {
                            pdv.setId((long) cellId.getNumericCellValue());
                        } else if (cellId.getCellType() == CellType.STRING) {
                            pdv.setId(Long.parseLong(cellId.getStringCellValue().trim()));
                        }

                        // TRATAMENTO DO NOME
                        pdv.setNome(cellNome.getStringCellValue());

                        pdvs.add(pdv);

                        // Log para você acompanhar no console do IntelliJ
                        System.out.println("Importando: " + pdv.getId() + " - " + pdv.getNome());

                    } catch (Exception e) {
                        System.err.println("Erro na linha " + (row.getRowNum() + 1) + ": " + e.getMessage());
                    }
                }
            }

            if (!pdvs.isEmpty()) {
                // Salva todos os PDVs com os IDs originais da Ambev no banco
                pdvRepository.saveAll(pdvs);
                System.out.println("Sucesso! " + pdvs.size() + " PDVs foram salvos.");
            } else {
                System.out.println("Nenhum dado válido encontrado no Excel.");
            }
        }
    }
}