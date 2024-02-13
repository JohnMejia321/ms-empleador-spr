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

    // Inicializar HazelcastInstance como un campo para usarlo en todo el servicio
    private HazelcastInstance hazelcastInstance;

    // Constructor para inicializar HazelcastInstance
    public PipelineService() {
        Config config = new Config();
        config.getJetConfig().setEnabled(true);
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }
    public void ejecutarPipeline(String ocrResult) throws InterruptedException {
        try {
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
                        UUID messageIdJson = UUID.randomUUID();
                        json.append(String.format(",\"Id solicitud\":\"%s\"", messageIdJson.toString()));
                        json.append("}");
                        String messageId = messageIdJson.toString();
                        return new AbstractMap.SimpleEntry<>(messageId, json.toString());
                    })
                    .setName("Map String to JSON Object")
                    .setLocalParallelism(1);
            Properties props = kafkaConfig.producerProperties();
            jsonEntries.writeTo(KafkaSinks.kafka(props,
                    "my_topic",
                    entry -> entry.getKey(),
                    entry -> entry.getValue()
            ));
            jsonEntries.writeTo(Sinks.observable("results"));
            jsonEntries.writeTo(Sinks.logger());
            jsonEntries.writeTo(Sinks.map("jsonMap"));
            hazelcastInstance.getList("sourceList").clear();
            hazelcastInstance.getList("sourceList").add(ocrResult);
            hazelcastInstance.getJet().newJob(pipeline);
        } catch (Exception e) {
            Logger.getLogger(PipelineService.class.getName()).severe("Error al ejecutar el pipeline: " + e.getMessage());
            throw e;
        }
    }
}
