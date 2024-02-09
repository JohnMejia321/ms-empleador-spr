package com.inner.consulting.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;

public class PdfUtils {

    public static String processPDFDocument(InputStream pdfStream) throws IOException {
        // Cargar el archivo PDF
        PDDocument document = PDDocument.load(pdfStream);

        // Crear un objeto PDFTextStripper
        PDFTextStripper pdfStripper = new PDFTextStripper();

        // Extraer texto
        StringWriter textWriter = new StringWriter();
        pdfStripper.writeText(document, textWriter);
        String text = textWriter.toString();

        // Dividir el texto en líneas
        String[] lines = text.split("\\r?\\n");

        // Recorrer cada línea y aplicar los reemplazos necesarios
        StringBuilder formattedText = new StringBuilder();
        for (String line : lines) {
            line = line.replaceAll("\\s+", " ");
            line = line.replace("Tipo Inscripción", "Tipo Inscripcion: ");
            line = line.replace("Tipo de empresa", "Tipo de empresa: ");
            line = line.replace("RUC", "RUC: ");
            line = line.replace("Tipo de documento", "Tipo de documento: ");
            line = line.replace("Número de documento", "Numero de documento: ");
            line = line.replace("ID de documento", "ID de documento: ");
            line = line.replace("Fecha Solicitud", "Fecha Solicitud: ");
            line = line.replace("Digito verificación", "Digito verificacion: ");
            line = line.replace("Casilla si pertenece a un grupo empresarial", "Casilla si pertenece a un grupo empresarial: ");
            line = line.replace("Razón social", "Razon social: ");
            line = line.replace("Nombre comercial del establecimiento", "Nombre comercial del establecimiento: ");
            line = line.replace("Fecha Inicio de labores", "Fecha Inicio de labores: ");
            line = line.replace("Localización geográfica", "Localizacion geografica: ");
            line = line.replace("Dirección del establecimiento comercial", "Direccion del establecimiento comercial: ");
            line = line.replace("Apartado del establecimiento comercial", "Apartado del establecimiento comercial: ");
            line = line.replace("Teléfono del establecimiento comercial", "Telefono del establecimiento comercial: ");
            line = line.replace("Teléfono alterno del establecimiento", "Telefono alterno del establecimiento: ");
            line = line.replace("Celular", "Celular: ");
            line = line.replace("Fax", "Fax: ");
            line = line.replace("Correo electrónico", "Correo electronico: ");
            line = line.replace("Página WEB", "Pagina WEB: ");
            line = line.replace("Agencia Solicitud", "Agencia Solicitud inscripcion: ");
            line = line.replace("Número aviso operación", "Numero aviso operacion: ");
            formattedText.append(line).append(",\n");
        }

        // Cerrar el documento
        document.close();

        return formattedText.toString();
    }
}
