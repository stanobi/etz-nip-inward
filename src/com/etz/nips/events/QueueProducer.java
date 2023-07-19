/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.etz.nips.events;

import com.etz.nips.util.Misc;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 *
 * @author stanlee
 */
public class QueueProducer {
    
    private KafkaProducer<String, String> kafkaProducer;
    private ProducerRecord<String, String> producerRecord;
    private Misc misc;

    public QueueProducer()  {
        misc = new Misc();
        Map<String, Object> configMap = new HashMap<String, Object>();
        configMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, misc.getPropertyValue("KAFKA_BOOTSTRAP_SERVERS"));
        configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducer = new KafkaProducer<String, String>(configMap);
        
    }
    
    public void sendSession(String sessionId) {
        producerRecord = new ProducerRecord<String, String>(misc.getPropertyValue("KAFKA_TOPIC"), sessionId);
        kafkaProducer.send(producerRecord);
        kafkaProducer.flush();
        kafkaProducer.close();
    }
}
