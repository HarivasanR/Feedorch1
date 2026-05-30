package com.feedorch1.feedorch1.controller;

import com.feedorch1.feedorch1.model.Follow;
import com.feedorch1.feedorch1.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowRepository followRepository;

    // POST http://localhost:8080/api/follows
    @PostMapping
    public Follow followUser(@RequestBody Follow follow) {
        return followRepository.save(follow);
    }

    // GET http://localhost:8080/api/follows
    @GetMapping
    public List<Follow> getAllFollows() {
        return followRepository.findAll();
    }
}