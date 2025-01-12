package com.bootcamp.walletmanagement.service.redis;

import com.bootcamp.walletmanagement.model.redis.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value(value = "${redis.key}")
    private String redisKey;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Transaction> findAllTransactionsRedis(String mobile) {
        String redis = redisTemplate.opsForValue().get(redisKey);
        return getMapper(redis).stream()
                .filter(transaction -> transaction.getMobile().equals(mobile)).toList();
    }

    private List<Transaction> getMapper(String message) {
        try {
            return objectMapper.readValue(message,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Transaction.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}