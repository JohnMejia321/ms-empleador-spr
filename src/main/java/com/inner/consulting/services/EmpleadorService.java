package com.inner.consulting.services;

import com.hazelcast.shaded.org.json.JSONObject;
import com.inner.consulting.config.KafkaConfig;
import com.inner.consulting.repositories.EmpleadorRepository;
import com.inner.consulting.entities.Empleador;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import net.sourceforge.tess4j.ITesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.kafka.KafkaSinks;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.BatchStage;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

@Service
public class EmpleadorService {

    @Autowired
    private EmpleadorRepository empleadorRepository;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private ITesseract tesseract;
    @Autowired
    private KafkaConfig kafkaConfig;

    @Autowired
    private KafkaTemplate<String, Empleador> kafkaTemplate;

    @Value("${minion.endpoint}")
    private String minionEndpoint;

    @Value("${minion.bucketName}")
    private String minionBucketName;

    public Empleador saveEmpleador(Empleador empleador, MultipartFile pdfFile) throws Exception {
        try {
            UUID empleadorId = UUID.randomUUID();
            String pdfName = empleadorId + "-" + pdfFile.getOriginalFilename();
            String jsonName = pdfName.replace(".pdf", ".json");
            String folderName = transformFolderName(empleador.getNombreComercial());
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(folderName).build());

            // Crear un archivo JSON temporal
            Path tempJsonPath = Files.createTempFile("temp-json", ".json");
            // Escribir el contenido del JSON en el archivo temporal
            String inputString = procesarPDF(pdfFile.getInputStream());

            // Paso 1: Analizar la cadena para extraer los datos relevantes
            String[] parts = inputString.split("\n");
            String nombre = parts[0].split(": ")[1];
            String documento = parts[1].split(": ")[1];

            // Paso 2: Construir un objeto JSON
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nombre", nombre);
            jsonObject.put("documento", documento);

            // Paso 3: Convertir el objeto JSON en una cadena JSON
            String jsonString = jsonObject.toString();
            try (FileWriter fileWriter = new FileWriter(tempJsonPath.toFile())) {
                fileWriter.write( jsonString );
            }

            // Subir el archivo JSON temporal a MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(folderName)
                            .object(jsonName)
                            .stream(Files.newInputStream(tempJsonPath), Files.size(tempJsonPath), -1)
                            .contentType("application/json")
                            .build());

            // Subir el archivo PDF a MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(folderName)
                            .object(pdfName)
                            .stream(pdfFile.getInputStream(), pdfFile.getSize(), -1)
                            .contentType(pdfFile.getContentType())
                            .build());

            // Generar la URL del PDF
            String pdfUrl = minionEndpoint + "/" + minionBucketName + "/" + pdfName;

            // Obtener la URL del archivo JSON
            String jsonUrl = minionEndpoint + "/" + minionBucketName + "/" + jsonName;

            // Procesar el PDF con Tesseract
            String ocrResult = procesarPDF(pdfFile.getInputStream());
            Logger.getLogger(EmpleadorService.class.getName()).info("Texto extraído del PDF: " + ocrResult);

            Instant now = Instant.now();
            empleador.setId(empleadorId);
            empleador.setPdfUrl(pdfUrl);
            empleador.setMetadatosDocumento(ocrResult);
            empleador.setFechaSolicitud(empleador.getFechaSolicitud());
            empleador.setTipoInscripcion(empleador.getTipoInscripcion());
            empleador.setTipoEmpresa(empleador.getTipoEmpresa());
            empleador.setRuc(empleador.getRuc());
            empleador.setTipoDocumento(empleador.getTipoDocumento());
            empleador.setNumeroDocumento(empleador.getNumeroDocumento());
            empleador.setIdDocumento(empleador.getIdDocumento());
            empleador.setDigitoVerificacion(empleador.getDigitoVerificacion());
            empleador.setCasilla(empleador.getCasilla());
            empleador.setRazonSocial(empleador.getRazonSocial());
            empleador.setNombreComercial(empleador.getNombreComercial());
            empleador.setFechaInicioLabores(empleador.getFechaInicioLabores());
            empleador.setLocalizacionGeografica(empleador.getLocalizacionGeografica());
            empleador.setDireccionEstablecimiento(empleador.getDireccionEstablecimiento());
            empleador.setApartadoEstablecimiento(empleador.getApartadoEstablecimiento());
            empleador.setTelefonoPrincipal(empleador.getTelefonoPrincipal());
            empleador.setTelefonoAlterno(empleador.getTelefonoAlterno());
            empleador.setCelular(empleador.getCelular());
            empleador.setFax(empleador.getFax());
            empleador.setCorreoElectronico(empleador.getCorreoElectronico());
            empleador.setPaginaWeb(empleador.getPaginaWeb());
            empleador.setAgenciaSolicitudInscripcion(empleador.getAgenciaSolicitudInscripcion());
            empleador.setNumeroAvisoOperacion(empleador.getNumeroAvisoOperacion());

            empleadorRepository.save(empleador);
            ejecutarPipeline(ocrResult);
            return empleador;
        } catch (Exception e) {
            Logger.getLogger("Error al procesar y guardar el empleador: " + e.getMessage());
            throw e;
        }
    }

    private String transformFolderName(String nombreComercial) {
        return nombreComercial.replaceAll("\\s+", "-").toLowerCase();
    }

    private void ejecutarPipeline(String ocrResult) {
        Config config = new Config();
        config.getJetConfig().setEnabled(true);
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);

        Pipeline pipeline = Pipeline.create();
        BatchStage<AbstractMap.SimpleEntry<String, String>> jsonEntries = pipeline
                .readFrom(Sources.<String>list("sourceList"))
                .map(entry -> {
                    String[] parts = entry.split("\n");
                    StringBuilder json = new StringBuilder("{");
                    for (String part : parts) {
                        String[] keyValue = part.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim();
                            json.append(String.format("\"%s\":\"%s\",", key, value));
                        }
                    }
                    if (json.charAt(json.length() - 1) == ',') {
                        json.deleteCharAt(json.length() - 1);
                    }
                    json.append("}");
                    return new AbstractMap.SimpleEntry<>(entry, json.toString());
                })
                .setName("Map String to JSON Object")
                .setLocalParallelism(1);

        Properties props = kafkaConfig.producerProperties();
        jsonEntries.peek().writeTo(KafkaSinks.kafka(props, "my_topic"));
        jsonEntries.peek().writeTo(Sinks.observable("results"));
        jsonEntries.peek().writeTo(Sinks.logger());
        jsonEntries.writeTo(Sinks.map("jsonMap"));

        hz.getJet().newJob(pipeline);
        hz.getList("sourceList").add(ocrResult);
    }

    private String procesarPDF(InputStream pdfStream) throws Exception {
        try {
            Path tempPdfPath = Files.createTempFile("temp-pdf", ".pdf");
            Files.copy(pdfStream, tempPdfPath, StandardCopyOption.REPLACE_EXISTING);
            File pdfFile = tempPdfPath.toFile();
            String ocrResult = tesseract.doOCR(pdfFile);
            byte[] bytes = ocrResult.getBytes(StandardCharsets.UTF_8);
            ocrResult = new String(bytes, Charset.defaultCharset());

            // Aquí estaba la llamada a ejecutarPipeline(ocrResult), la eliminamos

            Files.delete(tempPdfPath);
            return ocrResult;
        } catch (Exception e) {
            Logger.getLogger("Error al procesar el PDF con Tesseract: " + e.getMessage());
            throw e;
        }
    }

}
