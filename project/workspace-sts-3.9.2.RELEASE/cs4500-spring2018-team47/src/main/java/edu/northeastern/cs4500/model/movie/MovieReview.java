package edu.northeastern.cs4500.model.movie;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.northeastern.cs4500.model.user.User;

public class MovieReview implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "review_id")
	private int id;
	@Column(name = "description")
	private String review;
	@Column(name = "movie_id")
	private String movie_id;
	@Column(name = "review_date")
	private Date date;
	@Column(name = "reviewer_id")
	private String user_id;
	
	public int getId() {
		return id;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	public String getMovie_id() {
		return movie_id;
	}
	public void setMovie_id(String movie_id) {
		this.movie_id = movie_id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
