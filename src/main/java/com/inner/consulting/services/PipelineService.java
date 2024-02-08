package com.inner.consulting.services;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.kafka.KafkaSinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.BatchStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.inner.consulting.config.KafkaConfig;
import java.util.Properties;
import java.util.AbstractMap;
import java.util.logging.Logger;

@Service
public class PipelineService {

    @Autowired
    private KafkaConfig kafkaConfig;

    public void ejecutarPipeline(String ocrResult) {
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

            // Inicializar Hazelcast Jet
            // Obtener la lista "sourceList" y agregar datos
            Config config = new Config();
            config.getJetConfig().setEnabled(true);
            HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
            hz.getList("sourceList").add(ocrResult);
            hz.getJet().newJob(pipeline);
        } catch (Exception e) {
            Logger.getLogger(PipelineService.class.getName()).severe("Error al ejecutar el pipeline: " + e.getMessage());
            throw e;
        }
    }
}
