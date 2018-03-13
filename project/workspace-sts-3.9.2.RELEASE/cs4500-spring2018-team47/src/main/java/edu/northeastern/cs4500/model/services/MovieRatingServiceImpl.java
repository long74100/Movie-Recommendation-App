package edu.northeastern.cs4500.model.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.northeastern.cs4500.model.movieRating.MovieRating;
import edu.northeastern.cs4500.repositories.MovieRatingRepository;

@Service("movieRatingService")
public class MovieRatingServiceImpl implements MovieRatingService {

    @Autowired
    private MovieRatingRepository ratingRepository;

    @Override
    public void saveMovieRating(MovieRating rating) {
	ratingRepository.save(rating);
    }
    

}
