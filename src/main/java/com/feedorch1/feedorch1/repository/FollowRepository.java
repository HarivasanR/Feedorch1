package com.feedorch1.feedorch1.repository;

import com.feedorch1.feedorch1.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // FIX 1: Find people who follow me (For Pushing/Fan-out)
    @Query("SELECT f.followerId FROM Follow f WHERE f.followingId = :followingId")
    List<Long> findFollowerIdsByFollowingId(@Param("followingId") Long followingId);

    // FIX 2: Find people I follow (For the Hybrid Feed Pull)
    @Query("SELECT f.followingId FROM Follow f WHERE f.followerId = :followerId")
    List<Long> findFollowingIdsByFollowerId(@Param("followerId") Long followerId);
}