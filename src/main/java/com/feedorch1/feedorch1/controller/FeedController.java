package com.feedorch1.feedorch1.controller;

import com.feedorch1.feedorch1.model.Content;
import com.feedorch1.feedorch1.service.FeedAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedAggregatorService feedAggregatorService;

    // GET localhost:8080/api/feed/3 (to see User 3's feed)
    @GetMapping("/{userId}")
    public List<Content> getFeed(@PathVariable Long userId) {
        return feedAggregatorService.getUserFeed(userId);
    }
}