package br.com.rnplanner.service;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.repository.PdvRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ImportacaoPdvService {

    private final PdvRepository pdvRepository;

    public ImportacaoPdvService(PdvRepository pdvRepository) {
        this.pdvRepository = pdvRepository;
    }

    public void importar(InputStream inputStream) throws Exception {

        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            Integer codigo = (int) row.getCell(0).getNumericCellValue();
            String nome = row.getCell(1).getStringCellValue();

            Pdv pdv = new Pdv();
            pdv.setCodigo(codigo);
            pdv.setNome(nome);

            pdvRepository.save(pdv);
        }

        workbook.close();
    }
}
