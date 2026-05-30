package com.feedorch1.feedorch1.consumer;

import com.feedorch1.feedorch1.config.RabbitMQConfig;
import com.feedorch1.feedorch1.dto.StoryInteractionEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionConsumer {

    private final StringRedisTemplate redisTemplate;
    //background daemon listening to interaction queue
    @RabbitListener(bindings = { @QueueBinding(
        value = @Queue(value = RabbitMQConfig.INTERACTION_QUEUE, durable = "true"),
        exchange = @Exchange(value = RabbitMQConfig.INTERACTION_EXCHANGE, type = "topic"),
        key = RabbitMQConfig.INTERACTION_ROUTING_KEY
    )})
    public void consumeInteractionEvent(StoryInteractionEvent event) {
        log.info("pulled signal from broker -> user: {}, story: {}, creator: {}", event.userId(), event.storyId(), event.authorId());

        //stores the values of telemetry
        int delta = calculateScoreDelta(event.interactionType(), event.durationSeconds());
        log.info("Evaluated metric change: {} for signal action: [{} ({} seconds)]", 
                delta, event.interactionType(), event.durationSeconds());
    
        //next step, commit values to redis.
        updateAffinityScore(event.userId(), event.authorId(), delta);
        }

        private void updateAffinityScore(Long userId, Long authorId, int delta) {

            String redisKey = "user:affinity:" + userId;
            String hashField = authorId.toString();

            //read existing score from redis
            Object existingScoreObj = redisTemplate.opsForHash().get(redisKey, hashField); //what this line is doing is fetching the current affinity score for the user-author pair from Redis. It constructs a Redis key in the format "user:affinity:{userId}" and uses the authorId as the hash field to retrieve the existing score. The result is returned as an Object, which will be null if no score exists yet for that pair.
            int currentScore = 0;

            if (existingScoreObj != null) {
             try {
                currentScore = Integer.parseInt(existingScoreObj.toString()); //we need to parse it to int from object 
                }catch(NumberFormatException e) {
                    log.error("Failed to parse existing affinity score for user {} and author {}: {}", userId, authorId, e.getMessage());
                    currentScore = 0; // default to 0 if parsing fails
                }
            }
            int newScore = currentScore + delta;
            if (newScore > 20) newScore = 20;
            else if (newScore < -10) newScore = -10;

            redisTemplate.opsForHash().put(redisKey, hashField, String.valueOf(newScore));
            log.info("Updated affinity score for user {} and author {}: {} -> {}", userId, authorId, currentScore, newScore);

        }
        private int calculateScoreDelta(String interactionType, double duration) {
            if (interactionType == null) return 0;

            String type = interactionType.toUpperCase();

            switch(type) {
                case "PROFILE_CLICK" : return 7;
                case "COMMENT" : return 6;
                case "LIKE" : return 5;
                case "WATCH_COMPLETE" : return 4;
                case "SWIPE" : 
                    if (duration < 1.5) return -3;
                    else if (duration >= 1.5 && duration < 4.0) return 1;
                    else if (duration >= 4.0 && duration < 10.0) return 2;
                    else return 3;
                default : log.warn("Encountered unknown or unmapped telemetry header: {}", interactionType);
                return 0;
            }
        }
}

