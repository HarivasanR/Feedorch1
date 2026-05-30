package com.feedorch1.feedorch1.model;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity 
@Table(name = "users")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@JsonIgnoreProperties(value = { "celebrity" })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    // Forces MySQL to reject duplicate usernames and ensures it cannot be null
    @Column(unique = true, nullable = false)
    private String username;

    // Forces Jackson to map the exact payload field "isCelebrity" reliably
    @JsonProperty("isCelebrity")
    private boolean isCelebrity;
}