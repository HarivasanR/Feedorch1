package com.feedorch1.feedorch1.dto;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostFanOutEvent implements Serializable {
    private static final long serialVersionUID = 1L; // for serialization

    private Long contentId;
    private Long authorId;
}
