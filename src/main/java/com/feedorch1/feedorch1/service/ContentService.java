package com.feedorch1.feedorch1.service;

import com.feedorch1.feedorch1.dto.ContentRequest;
import com.feedorch1.feedorch1.model.Content;
import com.feedorch1.feedorch1.model.User;
import com.feedorch1.feedorch1.repository.ContentRepository;
import com.feedorch1.feedorch1.repository.UserRepository;
import com.feedorch1.feedorch1.dto.PostFanOutEvent;
import com.feedorch1.feedorch1.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentService {
    
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
 //   private final FanOutService fanOutService; // For pushing content to followers' feeds
    private final RabbitTemplate rabbitTemplate; // spring gets this bean from AMQP dependency

    @Transactional
    public Content createContent(ContentRequest request) {
        try {
            System.out.println("DEBUG: Starting createContent for authorId: " + request.authorId());

            // 1. Find User
            User author = userRepository.findById(request.authorId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.authorId()));

            // 2. Map and Save Content
            Content content = new Content();
            content.setAuthorId(author.getId());
            content.setType(request.type());
            content.setCategory(request.category());
            content.setMediaUrl(request.mediaUrl());

            content.setContentValue(request.contentValue());
            
            System.out.println("DEBUG: Saving to MySQL...");
            Content savedContent = contentRepository.saveAndFlush(content);
            System.out.println("DEBUG: Successfully saved Content ID: " + savedContent.getId());
    /*#pragma region
            // 3. Trigger Fan-out
            System.out.println("DEBUG: Starting Fan-out...");
            fanOutService.handlePostFanOut(savedContent, author);
            System.out.println("DEBUG: Fan-out Finished!");
  #pragma endregion  */

            //3. Trigger Fan-out via RabbitMQ
            System.out.println("DEBUG: Publishing to RabbitMQ...");
            PostFanOutEvent event = new PostFanOutEvent(savedContent.getId(), author.getId());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, event); //build the lightweight envelope and send it to the exchange with the routing key
            System.out.println("DEBUG: Published to RabbitMQ!");

            return savedContent;

        } catch (Exception e) {
            System.err.println("!!! CRASH LOG START !!!");
            System.err.println("Error Type: " + e.getClass().getName());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace(); // This prints the huge block of red text we need
            System.err.println("!!! CRASH LOG END !!!");
            throw e; // Still throw it so the transaction rolls back
        }
    }
}
