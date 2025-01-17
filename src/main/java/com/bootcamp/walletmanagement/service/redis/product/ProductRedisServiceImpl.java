package com.bootcamp.walletmanagement.service.redis.product;

import com.bootcamp.walletmanagement.model.redis.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRedisServiceImpl implements ProductRedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value(value = "${redis.product-key}")
    private String redisKey;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Product getProductRedis(String productId) {
        String message = redisTemplate.opsForValue().get(redisKey);
        return getMapper(message).stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(new Product());
    }

    private List<Product> getMapper(String message) {
        try {
            return objectMapper.readValue(message,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}