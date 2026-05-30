package com.feedorch1.feedorch1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follows", indexes = {
    @Index(name = "idx_following", columnList = "followingId")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long followerId;
    private Long followingId;
}
//index is the high level detail. it is used to speed up query for following id, and it not unique since one user can follow many. this fastens up the query by creating a separate data structure, which is usually a hashmap, to find the followers quickly.