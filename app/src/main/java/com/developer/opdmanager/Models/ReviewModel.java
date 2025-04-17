package com.developer.opdmanager.Models;

public class ReviewModel {
    private String reviewerName;
    private float rating;
    private String reviewText;

    public ReviewModel(String reviewerName, float rating, String reviewText) {
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public float getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }
}
