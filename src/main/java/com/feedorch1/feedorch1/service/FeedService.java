package com.feedorch1.feedorch1.service;

import com.feedorch1.feedorch1.dto.FeedItemResponse;
import com.feedorch1.feedorch1.model.Content;
import com.feedorch1.feedorch1.model.User;
// !! Removed the unused Post import since we unified everything to use your Content entity
import com.feedorch1.feedorch1.repository.ContentRepository;
import com.feedorch1.feedorch1.repository.FollowRepository;
import com.feedorch1.feedorch1.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;
// !! Removed the raw RedisTemplate import to fix the type mismatch/serialization bugs
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.stream.Collectors; //collectors for converting stream of bits back to list

@Slf4j
@Service //spring managed component that can be injected into controllers or other services
@RequiredArgsConstructor
public class FeedService {
     //pull regular posts from redis cache, pull celeb posts from mysql, and then merge and sort both lists, based on timestamp
    
    // !! Changed from raw RedisTemplate to StringRedisTemplate to keep serialization consistent across reading list caches and tracking hashes
    private final StringRedisTemplate redisTemplate;
    private final FollowRepository followRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    // !! Removed the redundant redisTemplate2 field since a single StringRedisTemplate handles both operations cleanly now

    // !! Changed the method parameters from Post to Content to perfectly match your active hybrid database entities
    public List<Content> getPersonalizedFeed(Long userId, List<Content> rawDbPosts) {
        log.info("generating personalised feed layour for User {}", userId);

        String redisKey = "user:affinity:" + userId;

        // !! Changed from redisTemplate2 to redisTemplate since we consolidated into a single unified bean field
        Map<Object, Object> rawAffinityMap = redisTemplate.opsForHash().entries(redisKey);//fetch entire map in one network call
        HashMap<Long, Integer> affinityMap = new HashMap<>();
        if (rawAffinityMap != null) {
            rawAffinityMap.forEach((key, value) -> {
                // !! Fixed your original line by changing the commas (,) at the end of these statements into proper Java semicolons (;)
                Long authorId =  Long.parseLong(key.toString());
                Integer score = Integer.parseInt(value.toString());
                affinityMap.put(authorId, score);
            });//this is the converting the objects of raw map into needed long and int for authorid and delta score
        }
        // !! Changed the stream from Post to Content type to match your main feed database entities
        return rawDbPosts.stream().sorted(new Comparator<Content>() { //high perf in-mem sorting stream pass
            @Override
            // !! Changed the comparator method signature from Post to Content to handle the list unification
            public int compare(Content c1, Content c2) {
                int score1 = affinityMap.getOrDefault(c1.getAuthorId(), 0);
                int score2 = affinityMap.getOrDefault(c2.getAuthorId(), 0);

                if (score1 != score2) {
                    return Integer.compare(score2, score1); //sort by interest affinity score
                }
                // !! Changed the fallback sorting strategy from p2.getCreatedAt() to c2.getId() to align with your original hybrid timeline logic
                return c2.getId().compareTo(c1.getId());
            }
        }).collect(Collectors.toList());
    }

    public List<FeedItemResponse> getUserFeed(Long userId) {
        System.out.println("feed service: generating hybrid timeline for User ID: " + userId);

        //1. FETCH REGU:LAR push posts from redis

        String feedKey = "feed:user:"+userId;
        List<String> cachedPostIdsStr = redisTemplate.opsForList().range(feedKey, 0, 49); //get the latest 50 post IDs from Redis

        List<Content> regularPosts = new ArrayList<>();
        if (cachedPostIdsStr != null && !cachedPostIdsStr.isEmpty()) {
            List<Long> cachedPostIds = cachedPostIdsStr.stream().map(Long::valueOf).collect(Collectors.toList()); //Redis stores as strings, like 10, 12, this is converted to array of Longs, 10L, 12L.
            regularPosts = contentRepository.findAllByIdInOrderByCreatedAtDesc(cachedPostIds); //instead of fetching 50 times, we fetch all 50 in one shot with a single batch query
        }

        //2. FETCH CELEB posts from MySQL
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(userId);
        List<User> followedCelebrities = userRepository.findAllById(followingIds).stream().filter(User::isCelebrity).collect(Collectors.toList());
        //stores list of following, and from those, filter the celebs with filter(), from the stream(), only keeps the celebs and rebuilds the list. Fetches as batch.
        List<Content> celebrityPosts = new ArrayList<>();
        if (!followedCelebrities.isEmpty()) {
            List<Long> celebIds = followedCelebrities.stream().map(User::getId).collect(Collectors.toList());
            celebrityPosts = contentRepository.findTop50ByAuthorIdInOrderByIdDesc(celebIds);
             //if celeb list not empty, from the stream get the IDs, sort by time and get top 50, this uses the heavy optimised lookup using a B-Tree to search by order, based on author_id.
        }

        //3. Merge and chronological sort
        List<Content> allCombinedContent = new ArrayList<>();
        allCombinedContent.addAll(regularPosts);
        allCombinedContent.addAll(celebrityPosts);
        
        // !! Intercepted the pipeline right here to pass the combined content list through your new affinity matrix lookup before transforming it to DTOs
        List<Content> personalizedContent = getPersonalizedFeed(userId, allCombinedContent);

        // !! Changed the stream source from allCombinedContent to personalizedContent so the DTO conversion preserves your algorithmic sort order
        return personalizedContent.stream()
        //this is a smart sorting algorithm to sort in descending order of time, this is the synta
        .map(content -> {
            User author = userRepository.findById(content.getAuthorId()).orElse(null);
            String username = (author != null) ? author.getUsername() : "Unknown";
            //this part is required since content only stores authorId, it gets author name. 
            return new FeedItemResponse(
                content.getId(),
                content.getAuthorId(),
                username,
                content.getType() != null ? content.getType().name() : null,
                content.getCategory(),
                content.getContentValue(),
                content.getMediaUrl(),
                content.getCreatedAt()
            );
        }).collect(Collectors.toList()); //how is two returns possible? the outer public getUserFeed() owns the first return. the second return belongs to lamba function ->
        //the map converts from object of type content to type FeedItemRespose, and defined how to build an object of FeedItemResponse.

        //need collector for converting bit stream to list back again. 
    }
}