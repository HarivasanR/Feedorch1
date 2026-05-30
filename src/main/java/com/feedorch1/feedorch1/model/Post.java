package com.feedorch1.feedorch1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity 
@Table(name = "posts") 
@Data()
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long authorId; //used for tier 1 affinity sorting redis

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt; //used for tier 2 tie breaker sorting
}
