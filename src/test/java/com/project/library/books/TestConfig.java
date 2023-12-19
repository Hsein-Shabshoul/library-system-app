package com.project.library.books;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class TestConfig {

    @Bean
    public HashOperations<String, Long, Book> hashOperations(RedisTemplate<String, Book> redisTemplate) {
        return redisTemplate.opsForHash();
    }
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    //Creating RedisTemplate for Entity 'Book'
    @Bean
    public RedisTemplate<String, Book> redisTemplate(){
        RedisTemplate<String, Book> bookTemplate = new RedisTemplate<>();
        bookTemplate.setConnectionFactory(redisConnectionFactory());
        return bookTemplate;
    }
}

