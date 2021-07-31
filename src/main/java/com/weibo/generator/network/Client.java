package com.weibo.generator.network;


import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * kafka生产配置
 *
 * @author Lvjiapeng
 */
@Configuration
@EnableKafka
public class Client {
    @Value("${kafka.producer.servers}")
    private String servers;
    @Value("${kafka.producer.retries}")
    private int retries;
    @Value("${kafka.producer.batch.size}")
    private int batchSize;
    @Value("${kafka.producer.linger}")
    private int linger;
    @Value("${kafka.producer.buffer.memory}")
    private int bufferMemory;
    @Value("${kafka.producer.topic.data}")
    private String dataTopic;
    @Value("${kafka.producer.topic.control}")
    private String controlTopic;

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    private ProducerFactory<String, String> producerFactory() {
        this.createTopics();
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    private void createTopics() {
        AdminClient adminClient = this.getKafkaAdminClient();
        ArrayList<String> topics;
        try {
            topics = (ArrayList<String>) this.getAllTopic(adminClient);
        } catch (ExecutionException | InterruptedException exception) {
            topics = new ArrayList<>();
        }

        if (!topics.contains(dataTopic)) {
            NewTopic topic = new NewTopic(dataTopic, 5, (short) 1);
            adminClient.createTopics(Collections.singletonList(topic));
        }

        if (!topics.contains(controlTopic)) {
            NewTopic topic = new NewTopic(controlTopic, 5, (short) 1);
            adminClient.createTopics(Collections.singletonList(topic));
        }
    }

    @Bean
    public KafkaTemplate<String, String> kafkaClient() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(dataTopic);
        return kafkaTemplate;
    }

    private AdminClient getKafkaAdminClient() {
        Map<String, Object> props = new HashMap<>(1);
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        return KafkaAdminClient.create(props);
    }

    private Collection<String> getAllTopic(AdminClient client) throws InterruptedException, ExecutionException {
        return client.listTopics().listings().get().stream().map(TopicListing::name).collect(Collectors.toList());
    }

    public String getDataTopic() {
        return dataTopic;
    }

    public String getControlTopic() {
        return controlTopic;
    }
}