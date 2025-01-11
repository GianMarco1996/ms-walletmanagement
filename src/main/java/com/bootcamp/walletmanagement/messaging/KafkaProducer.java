package com.bootcamp.walletmanagement.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${kafka.topic-name}")
    private String topicName;

    ObjectMapper objectMapper = new ObjectMapper();

    public void send(List<Transaction> transactions) throws JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(transactions);
        kafkaTemplate.send(topicName, jsonString);
    }
}