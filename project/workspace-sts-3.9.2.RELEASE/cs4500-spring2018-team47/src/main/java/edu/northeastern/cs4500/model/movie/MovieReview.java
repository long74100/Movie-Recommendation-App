package edu.northeastern.cs4500.model.movie;

import java.io.Serializable;

public class MovieReview implements Serializable {

	private String review;
	private Movie movie;

	public String getReview() {
		return review;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public void setReview(String review) {
		this.review = review;
	}

}
