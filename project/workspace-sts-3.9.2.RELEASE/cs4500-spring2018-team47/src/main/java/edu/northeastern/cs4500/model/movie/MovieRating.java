package edu.northeastern.cs4500.model.movie;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "movie_rating")
public final class MovieRating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rating_id")
    private int id;
    
    @Column(name="movie_name")
    private String movieName;
    @Column(name="rating")
    private double rating;
    @Column(name="user_id")
    private int userID;


    //getter setters
    public void setMovieName(String name) {
	this.movieName = name;
    }
    
    public void setRating(double double1) {
	this.rating = double1;
    }

    public void setUserID(int userID) {
	this.userID = userID;
    }

    public String getMovieName() {
	return this.movieName;
	
    }
    
    public double getRating() {
	return this.rating;
    }
    
    public int getUserID() {
	return userID;
    }
    
}
