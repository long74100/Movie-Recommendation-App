package edu.northeastern.cs4500.model.movie;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rating")
public final class MovieRating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rating_id")
    private int id;
    
    @Column(name="movie_id")
    private String movieId;
    @Column(name="rating")
    private double rating;
    @Column(name="user_id")
    private int userID;
    @Column(name = "review_date")
    private String date;

    //getter setters
    public void setMovieId(String id) {
	this.movieId = id;
    }
    
    public void setRating(double double1) {
	this.rating = double1;
    }

    public void setUserID(int userID) {
	this.userID = userID;
    }
    
    public void setDate(String date) {
	this.date = date;
    }
    
    public int getRatingId() {
	return this.id;
    }

    public String getMovieId() {
	return this.movieId;
	
    }
    
    public double getRating() {
	return this.rating;
    }
    
    public int getUserID() {
	return this.userID;
    }
    
    public String getDate() {
	return this.date;
    }
    
}