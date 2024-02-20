package com.inner.consulting.services;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Observable;
import com.hazelcast.shaded.org.json.JSONObject;
import com.inner.consulting.config.KafkaConfig;
import com.inner.consulting.repositories.EmpleadorRepository;
import com.inner.consulting.entities.Empleador;
import com.inner.consulting.utils.PdfUtils;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import net.sourceforge.tess4j.ITesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import com.inner.consulting.utils.EmpleadorUtils;


@Service
public class EmpleadorService {
    @Autowired
    private EmpleadorRepository empleadorRepository;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private ITesseract tesseract;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate; // Inyectado desde KafkaConfig
    @Autowired
    private PipelineService pipelineService;
    @Value("${minion.endpoint}")
    private String minionEndpoint;
    @Value("${minion.bucketName}")
    private String minionBucketName;
    public Empleador saveEmpleador(Empleador empleador, MultipartFile pdfFile) throws Exception {
        try {
            UUID empleadorId = UUID.randomUUID();
            String pdfName = empleadorId + "-" + pdfFile.getOriginalFilename();
            String jsonName = pdfName.replace(".pdf", ".json");
            String folderName = transformFolderName(empleador.getNombreComercial()+"-"+empleadorId.toString());
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(folderName).build());
            Path tempJsonPath = Files.createTempFile("temp-json", ".json");
            String inputString = PdfUtils.processPDFDocument(pdfFile.getInputStream());
            JSONObject jsonObject = new JSONObject();
            try (FileWriter fileWriter = new FileWriter(tempJsonPath.toFile())) {
                fileWriter.write( PdfUtils.convertToJSON(inputString) );
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(folderName)
                            .object(jsonName)
                            .stream(Files.newInputStream(tempJsonPath), Files.size(tempJsonPath), -1)
                            .contentType("application/json")
                            .build());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(folderName)
                            .object(pdfName)
                            .stream(pdfFile.getInputStream(), pdfFile.getSize(), -1)
                            .contentType(pdfFile.getContentType())
                            .build());

            String pdfUrl = minionEndpoint + "/" + minionBucketName + "/" + pdfName;
            String jsonUrl = minionEndpoint + "/" + minionBucketName + "/" + jsonName;
            String ocrResult = PdfUtils.processPDFDocument(pdfFile.getInputStream());
            Logger.getLogger(EmpleadorService.class.getName()).info("Texto extraído del PDF: " + ocrResult);
            Instant now = Instant.now();
            empleador.setId(empleadorId);
            empleador.setPdfUrl(pdfUrl);
            empleador.setMetadatosDocumento(ocrResult);
            EmpleadorUtils.setearAtributosEmpleador(empleador);
            empleadorRepository.save(empleador);
            // Enviar a Kafka
           // String kafkaMessage =  empleadorId.toString(); // Concatenar los argumentos
            String text =  ocrResult; // Concatenar los argumentos
            String id =  empleadorId.toString(); // Concatenar los argumentos


          kafkaTemplate.send("topic-pipeline", text);
            kafkaTemplate.send("topic-job", id);


        //    pipelineService.ejecutarPipeline(ocrResult, empleadorId);
            return empleador;
        } catch (Exception e) {
            Logger.getLogger("Error al procesar y guardar el empleador: " + e.getMessage());
            throw e;
        }
    }
    private String transformFolderName(String nombreComercial) {
        return nombreComercial.replaceAll("\\s+", "-").toLowerCase();
    }

    private String procesarPDF(InputStream pdfStream) throws Exception {
        try {Path tempPdfPath = Files.createTempFile("temp-pdf", ".pdf");
            Files.copy(pdfStream, tempPdfPath, StandardCopyOption.REPLACE_EXISTING);
            File pdfFile = tempPdfPath.toFile();
            String ocrResult = tesseract.doOCR(pdfFile);
            byte[] bytes = ocrResult.getBytes(StandardCharsets.UTF_8);
            ocrResult = new String(bytes, Charset.defaultCharset());
            Files.delete(tempPdfPath);
            return ocrResult;
        } catch (Exception e) {
            Logger.getLogger("Error al procesar el PDF: " + e.getMessage());
            throw e;
        }
    }
}



