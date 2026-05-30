package com.feedorch1.feedorch1.dto;

import java.time.LocalDateTime;

public record FeedItemResponse( //record means immutable class 
    Long id,
    Long authorId,
    String username,
    String type,  //this being string will give mismatch since it is an enum. so add check in feed service
    String category,
    String contentValue,
    String mediaUrl,
    LocalDateTime createdAt
) {}
