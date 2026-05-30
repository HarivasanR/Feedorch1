package com.feedorch1.feedorch1.controller;

import com.feedorch1.feedorch1.dto.ContentRequest;
import com.feedorch1.feedorch1.model.Content;
import com.feedorch1.feedorch1.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    //post to localhost:8080/api/content with body as json like this:
    @PostMapping
    public Content postContent(@Valid @RequestBody ContentRequest request) {
        return contentService.createContent(request);
        //@requestbody converts incoming json to content request object.
    }
}
