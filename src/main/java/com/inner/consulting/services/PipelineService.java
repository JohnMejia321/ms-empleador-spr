package com.inner.consulting.services;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.kafka.KafkaSinks;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.BatchStage;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.inner.consulting.config.KafkaConfig;

import java.util.Map;
import java.util.Properties;
import java.util.AbstractMap;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PipelineService {

    @Autowired
    private KafkaConfig kafkaConfig;

    public void ejecutarPipeline(String ocrResult) throws InterruptedException {
        try {
            Pipeline pipeline = Pipeline.create();
            BatchStage<AbstractMap.SimpleEntry<String, String>> jsonEntries = pipeline
                    .readFrom(Sources.<String>list("sourceList"))
                    .map(entry -> {
                        // Parsear la entrada JSON
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
                        UUID messageIdJson = UUID.randomUUID();
                        json.append(String.format(",\"Id solicitud\":\"%s\"", messageIdJson.toString()));
                        json.append("}");

                        // Generar un UUID único para la clave
                        String messageId = messageIdJson.toString();

                        // Crear la entrada con la clave y el valor
                        return new AbstractMap.SimpleEntry<>(messageId, json.toString());
                    })
                    .setName("Map String to JSON Object")
                    .setLocalParallelism(1);

            Properties props = kafkaConfig.producerProperties();

            // Escribir los datos en el tópico Kafka con la clave y el valor separados
            jsonEntries.writeTo(KafkaSinks.kafka(props,
                    "my_topic",
                    entry -> entry.getKey(),
                    entry -> entry.getValue() 
            ));

            jsonEntries.writeTo(Sinks.observable("results"));
            jsonEntries.writeTo(Sinks.logger());
            jsonEntries.writeTo(Sinks.map("jsonMap"));

            // Inicializar Hazelcast Jet y ejecutar el pipeline
            Config config = new Config();
            config.getJetConfig().setEnabled(true);
            HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
            hz.getList("sourceList").add(ocrResult);
            hz.getJet().newJob(pipeline);
            Thread.sleep(2000); // Espera 2 segundos (ajusta el tiempo según sea necesario)
            IMap<String, String> jsonMap = hz.getMap("jsonMap");

            // Imprimir el contenido del mapa
            for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
                System.out.println("Clave desde  mapa: " + entry.getKey() + ", Valor: " + entry.getValue());
            }
        } catch (Exception e) {
            Logger.getLogger(PipelineService.class.getName()).severe("Error al ejecutar el pipeline: " + e.getMessage());
            throw e;
        }
    }
}
