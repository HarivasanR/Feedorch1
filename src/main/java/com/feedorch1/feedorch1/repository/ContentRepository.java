package com.feedorch1.feedorch1.repository;

import com.feedorch1.feedorch1.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    
    // This fixed the red line. It tells Spring: 
    // "Find all content where authorId is in this list, and sort by date descending"
    List<Content> findAllByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds);

    // Also add this to fetch the posts stored in the Redis IDs
    List<Content> findAllByIdInOrderByCreatedAtDesc(List<Long> ids);

    List<Content> findTop50ByAuthorIdInOrderByIdDesc(List<Long> authorIds);
}