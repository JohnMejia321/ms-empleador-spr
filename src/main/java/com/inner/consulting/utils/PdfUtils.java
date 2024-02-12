package com.inner.consulting.utils;

import com.hazelcast.shaded.org.json.JSONObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.List;


public class PdfUtils {

    public static String processPDFDocument(InputStream pdfStream) throws IOException {
        StringBuilder result = new StringBuilder();

        // Cargar el archivo PDF
        PDDocument document = PDDocument.load(pdfStream);

        // Crear un objeto ObjectExtractor
        ObjectExtractor oe = new ObjectExtractor(document);
        PageIterator iterator = oe.extract();

        // Crear un objeto BasicExtractionAlgorithm
        BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();

        // Iterar sobre las páginas
        while (iterator.hasNext()) {
            Page page = iterator.next();

            // Extraer las tablas de la página
            List<Table> tables = bea.extract(page);

            // Iterar sobre las tablas
            for (Table table : tables) {
                int rowCounter = 0;
                // Iterar sobre las filas de la tabla
                for (List<RectangularTextContainer> row : table.getRows()) {
                    // Omitir las dos primeras filas
                    if (rowCounter < 2) {
                        rowCounter++;
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    // Iterar sobre las celdas de la fila
                    for (RectangularTextContainer cell : row) {
                        // Remover las tildes del texto
                        String normalizedText = Normalizer.normalize(cell.getText(), Normalizer.Form.NFD);
                        normalizedText = normalizedText.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                        sb.append(normalizedText).append(",");
                    }
                 //   String finalText = sb.toString().replace(",,", "::").replace(",", ":").replace("::", ",,");
                    String finalText = sb.toString().replace(",,", "").replace(",", ":");
                    result.append(finalText).append("\n");
                }
            }

            // Cerrar el documento PDF
            document.close();
        }

        return result.toString();
    }

    public static String convertToJSON(String keyValueString) {
        JSONObject json = new JSONObject();
        String[] lines = keyValueString.split("\n");
        for (String line : lines) {
            String[] keyValue = line.split(":");
            if (keyValue.length == 2) {
                // String key = keyValue[0].trim().replace(" ", "_");
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                json.put(key, value);
            }
        }
        return json.toString();
    }
}
