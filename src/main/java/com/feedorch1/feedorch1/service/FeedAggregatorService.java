package com.feedorch1.feedorch1.service;

import com.feedorch1.feedorch1.model.Content;
import com.feedorch1.feedorch1.repository.ContentRepository;
import com.feedorch1.feedorch1.repository.FollowRepository;
import com.feedorch1.feedorch1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedAggregatorService {

    private final RedisTemplate<String, String> redisTemplate;
    private final FollowRepository followRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    public List<Content> getUserFeed(Long userId) {
        // 1. PULL FROM REDIS (Regular user IDs)
        String feedKey = "feed:user:" + userId;
        List<String> redisContentIds = redisTemplate.opsForList().range(feedKey, 0, 49); 
        
        List<Long> regularContentIds = (redisContentIds == null) ? new ArrayList<>() : 
                redisContentIds.stream().map(Long::valueOf).collect(Collectors.toList());

        // 2. PULL FROM MYSQL (Celebrity IDs - The Pull Model)
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(userId);
        
        // Safety fix: Using .map().orElse(false) instead of .get() to remove yellow lines
        List<Long> celebIds = followingIds.stream()
                .filter(id -> userRepository.findById(id)
                        .map(user -> user.isCelebrity())
                        .orElse(false))
                .collect(Collectors.toList());

        // 3. THE MERGE ENGINE
        List<Content> finalFeed = new ArrayList<>();

        // Add regular posts (from the IDs we got from Redis)
        if (!regularContentIds.isEmpty()) {
            finalFeed.addAll(contentRepository.findAllByIdInOrderByCreatedAtDesc(regularContentIds));
        }

        // Add Celebrity posts (Directly from MySQL)
        if (!celebIds.isEmpty()) {
            // This fix uses the variable, removing the yellow warning
            List<Content> celebPosts = contentRepository.findAllByAuthorIdInOrderByCreatedAtDesc(celebIds);
            finalFeed.addAll(celebPosts);
        }

        // 4. FINAL SORT
        // Since we combined two lists, we must sort them by time so the feed makes sense
        finalFeed.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return finalFeed;
    }
}