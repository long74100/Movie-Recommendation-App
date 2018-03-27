package edu.northeastern.cs4500.model.services;

import edu.northeastern.cs4500.model.movie.MovieRating;

public interface MovieRatingService {
	/**
	 * Saves a movie rating to the repository.
	 * @param rating movie rating
	 */
	public void saveMovieRating(MovieRating rating);
}
