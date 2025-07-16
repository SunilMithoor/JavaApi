package com.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static com.app.util.Utils.tagMethodName;

@Configuration
public class RedisConfig {

    private final LoggerService logger;
    private static final String TAG = "RedisConfig";

    @Autowired
    public RedisConfig(LoggerService logger) {
        this.logger = logger;
    }

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String methodName = "redisConnectionFactory";
        logger.info(tagMethodName(TAG, methodName), "Attempting to load Redis config factory: ");
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        config.setPassword(redisPassword); // Set password
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().build();
        logger.info(tagMethodName(TAG, methodName), "Return client config for Redis ");
        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        String methodName = "redisTemplate";
        logger.info(tagMethodName(TAG, methodName), "Attempting to load Redis template ");
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        logger.info(tagMethodName(TAG, methodName), "Return Redis template ");
        return template;
    }
}