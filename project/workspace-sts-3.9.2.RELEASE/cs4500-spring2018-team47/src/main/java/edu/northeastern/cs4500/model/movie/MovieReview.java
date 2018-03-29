package edu.northeastern.cs4500.model.movie;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class MovieReview implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
<<<<<<< HEAD
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
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
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
=======
    @Column(name = "review_id")
    private int id;
    @Column(name = "description")
    private String review;
    @Column(name = "movie_id")
    private String movie_id;
    @Column(name = "review_date")
    private String date;
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
>>>>>>> 84613000f581dc5c8c6e9d251c7b0d130d4d1bcf
}
