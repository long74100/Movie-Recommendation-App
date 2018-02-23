package edu.northeastern.cs4500.model.movie;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "movie")

//Represents a movie
public class Movie {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "movie_id")
	private int id;
	
	String title;
	String plot;

	public String getTitle() {
		return title;
	}
	
	public String getPlot() {
		return plot;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setPlot(String plot) {
		this.plot = plot;
	}
	
}
