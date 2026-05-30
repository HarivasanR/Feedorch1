package com.feedorch1.feedorch1.repository;

import com.feedorch1.feedorch1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring automatically provides: save(), findById(), findAll(), delete()
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users (id, username, is_celebrity) VALUES (:id, :username, :isCelebrity)", nativeQuery = true)
    void insertUserManually(@Param("id") Long id, @Param("username") String username, @Param("isCelebrity") boolean isCelebrity);
}