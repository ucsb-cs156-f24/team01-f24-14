package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReviews;
import edu.ucsb.cs156.example.entities.MenuItemReviews;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.MenuItemReviewsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

/**
 * This is a REST controller for MenuItemReviews
 */

@Tag(name = "MenuItemReviews")
@RequestMapping("/api/menuitemreviews")
@RestController
@Slf4j
public class MenuItemReviewController extends ApiController {

    @Autowired
    MenuItemReviewsRepository menuItemReviewsRepository;

    /**
     * List all Menu Item Reviews
     * 
     * @return an iterable of MenuItemReviews
     */
    @Operation(summary= "List all menu item reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<MenuItemReviews> allMenuItemReviews() {
        Iterable<MenuItemReviews> reviews = menuItemReviewsRepository.findAll();
        return reviews;
    }

    /**
     * Get a single review by id
     * 
     * @param id the id of the review
     * @return a MenuItemReview
     */
    @Operation(summary= "Get a single review")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public MenuItemReviews getById(
            @Parameter(name="id") @RequestParam Long id) {
        MenuItemReviews menuItemReview = menuItemReviewsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReviews.class, id));

        return menuItemReview;
    }

    /**
     * Create a new review
     * 
     * @param itemId  
     * @param reviewerEmail          
     * @param stars
     * @param dateReviewed
     * @param comments
     * @return the saved menu item review
     */
    @Operation(summary= "Create a new review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public MenuItemReviews postMenuItemReviews(
            @Parameter(name="itemId") @RequestParam long itemId,
            @Parameter(name="reviewerEmail") @RequestParam String reviewerEmail,
            @Parameter(name="stars") @RequestParam int stars,
            @Parameter(name="comments") @RequestParam String comments,
            @Parameter(name="dateReviewed") @RequestParam("dateReviewed") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateReviewed)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("dateReviewed={}", dateReviewed);

        MenuItemReviews review = new MenuItemReviews();
        review.setItemId(itemId);
        review.setReviewerEmail(reviewerEmail);
        review.setStars(stars);
        review.setComments(comments);
        review.setDateReviewed(dateReviewed);


        MenuItemReviews savedReview = menuItemReviewsRepository.save(review);

        return savedReview;
    }

}