package com.example.catalogservice.messagequeue;

import com.example.catalogservice.entity.CatalogEntity;
import com.example.catalogservice.repostitory.CatalogRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    CatalogRepository catalogRepository;

    @KafkaListener(topics = "example-catalog-topic")
    public void updateQty(String kafkaMessage){
        log.info("Kafka Message: -> " + kafkaMessage);
        Map<Object,Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        CatalogEntity entity = catalogRepository.findByProductId((String) map.get("productId"));

        if(Objects.nonNull(entity)){
            entity.setStock(entity.getStock() - (Integer)map.get("qty"));
        }
    }
}
