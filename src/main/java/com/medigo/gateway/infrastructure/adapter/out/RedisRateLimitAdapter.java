package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.port.out.RateLimitPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Adaptador de rate limiting usando Redis con ventana deslizante de 1 minuto.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRateLimitAdapter implements RateLimitPort {

    private static final String KEY_PREFIX = "rl:";
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isAllowed(String key, int maxPerMinute) {
        try {
            String redisKey = KEY_PREFIX + key;
            Long count = redisTemplate.opsForValue().increment(redisKey);

            if (count != null && count == 1) {
                redisTemplate.expire(redisKey, Duration.ofMinutes(1));
            }

            boolean allowed = count != null && count <= maxPerMinute;
            if (!allowed) {
                log.warn("Rate limit excedido para key: {}", key);
            }
            return allowed;
        } catch (Exception ex) {
            log.warn("Redis no disponible, omitiendo rate limit para key: {}. Causa: {}", key, ex.getMessage());
            return true;
        }
    }
}
