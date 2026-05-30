package com.feedorch1.feedorch1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration //means class uses beans. beans are objects that spring manages. we can inject them in other classes using @Autowired.
public class RedisConfig {
    
    @Bean // Creates a "Redis Remote Control" that we can use in our services
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // We set these to Strings so that when you check Redis CLI, 
        // you can read the IDs. Otherwise, it looks like gibberish.
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
