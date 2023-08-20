package com.spring.shopping.controller;

import com.spring.shopping.DTO.ReviewDTO;
import com.spring.shopping.entity.Product;
import com.spring.shopping.service.OrderService;
import com.spring.shopping.service.ProductService;
import com.spring.user.entity.User;
import com.spring.shopping.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public ReviewController(ReviewService reviewService, ProductService productService, OrderService orderService) {
        this.reviewService = reviewService;
        this.productService = productService;
        this.orderService = orderService;
    }

    //특정 상품 리뷰 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProduct(@PathVariable Long productId) {
        Product product = productService.getProductInfo(productId);
        product.setProductId(productId);

        List<ReviewDTO> reviews = reviewService.getReviewsByProduct(product);
        return ResponseEntity.ok(reviews);
    }

    // 특정 유저가 작성한 리뷰 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        User user = orderService.getUserInfo(userId);
        user.setUserId(userId);

        List<ReviewDTO> reviews = reviewService.getReviewsByUser(user);
        return ResponseEntity.ok(reviews);
    }

    // 특정 평점 이상의 리뷰 조회 -> 굳이 필요할까?
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<ReviewDTO>> getReviewsWithRatingGreaterThan(@PathVariable Double rating) {
        List<ReviewDTO> reviews = reviewService.getReviewsWithRatingGreaterThan(rating);
        return ResponseEntity.ok(reviews);
    }

    // 특정 리뷰 상세 정보 조회 -> 얘도 굳이?
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long reviewId) {
        return reviewService.getReviewById(reviewId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 새로운 리뷰 생성
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestParam Long userId,
                                                  @RequestParam Long productId,
                                                  @RequestParam Double rating,
                                                  @RequestParam String comment) {
        User user = orderService.getUserInfo(userId);
        user.setUserId(userId);

        Product product = productService.getProductInfo(productId);
        product.setProductId(productId);

        ReviewDTO createdReview = reviewService.createReview(user, product, rating, comment);
        return ResponseEntity.ok(createdReview);
    }

    // 특정 리뷰를 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}