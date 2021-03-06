package edu.northeastern.cs4500.model.movie;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class MovieReview implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "review_id", nullable = false, unique = true)
	private int id;
	@Column(name = "description")
	private String review;
	@Column(name = "movie_id")
	private String movie_id;
	@Column(name = "review_date")
	private String date;
	@Column(name = "reviewer_id")
	private String user_id;
	@Column(name = "reviewer_name")
	private String username;
	@Column(name= "movie_name")
	private String movie_name;
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getReview() {
		return review;
	}
	
	public String getMovieName() {
		return this.movie_name;
	}
	
	public void setReview(String review) {
		this.review = review;
	}
	public String getMovie_id() {
		return movie_id;
	}
	
	public void setId(int id) {
	    this.id = id;
	}
	
	public void setMovie_id(String movie_id) {
		this.movie_id = movie_id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	public void setUsername(String name) {
		this.username = name;
	}
	
	public void setMovieName(String name) {
		this.movie_name = name;
	}
}
