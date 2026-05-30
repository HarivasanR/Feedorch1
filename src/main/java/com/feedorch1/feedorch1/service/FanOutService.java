package com.feedorch1.feedorch1.service;

import com.feedorch1.feedorch1.config.RabbitMQConfig;
import com.feedorch1.feedorch1.dto.PostFanOutEvent;
import com.feedorch1.feedorch1.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FanOutService {

    // This is our "Remote Control" for Redis that we configured in RedisConfig
    private final RedisTemplate<String, String> redisTemplate;
    
    // We need this to find out WHO to push the content to
    private final FollowRepository followRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME) 
    public void handlePostFanOut(PostFanOutEvent event) {
        // Extract content and author information from the event
        Long contentId = event.getContentId();
        Long authorId = event.getAuthorId();
/*
        // 1. THE CELEBRITY CHECK
        if (author.isCelebrity()) {
            // We stop here. We don't want to crash Redis by pushing to 1 million people.
            return; 
        }
*/
        // 2. FETCH FOLLOWER IDs
        // We only get the IDs (Long) to keep the memory usage low.
        List<Long> followerIds = followRepository.findFollowerIdsByFollowingId(authorId);

        if (followerIds == null || followerIds.isEmpty()) {
            System.out.println("No followers found for user " + authorId + ". Skipping fan-out.");
            return; 
        }

        // 3. THE MULTI-THREADED PUSH (FAN-OUT)
        // .parallelStream() tells Java to use all your CPU cores.
        // If you have 1000 followers, it updates multiple Redis lists at the same time.
        followerIds.parallelStream().forEach(followerId -> {
            String feedKey = "feed:user:" + followerId;
            
            // LPUSH: Adds the new Content ID to the "Head" of the Redis List
            redisTemplate.opsForList().leftPush(feedKey, contentId.toString());
            
            // TRIM: We only keep the latest 200 items for each user.
            // This prevents Redis from running out of RAM (very important in interviews!)
            redisTemplate.opsForList().trim(feedKey, 0, 199);
        });
    }
}