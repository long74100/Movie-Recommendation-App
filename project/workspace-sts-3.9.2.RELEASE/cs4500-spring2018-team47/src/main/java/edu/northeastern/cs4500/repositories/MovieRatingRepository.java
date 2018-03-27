package edu.northeastern.cs4500.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.northeastern.cs4500.model.movie.MovieRating;

/**
 * Repository for movie ratings.
 *
 */
public interface MovieRatingRepository extends JpaRepository<MovieRating, Long> {

}
