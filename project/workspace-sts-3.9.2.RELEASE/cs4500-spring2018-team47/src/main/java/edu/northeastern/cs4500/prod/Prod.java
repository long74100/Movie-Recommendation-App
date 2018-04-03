package edu.northeastern.cs4500.prod;

import java.util.Date;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This is the Prod object, when a user prods to the other, it will pass one Prod object to other user.
 * @author lgj81
 *
 */

public class Prod {
	// the sender's id
	private int senderId;
	
	// the receiver's id
	private int receiverId;
	
	// the movie's id
	private String movieId;
	
	// the movie's name 
	private String movieName;
	
	// the time when the prod is sent
	private String date;
	
	// the comment that sender has
	private String senderComment;
	
	/**
	 * Default Constructor
	 */
	public Prod() {
		
	}
	
	
	/**
	 *  Getter methods
	 */
	/**
	 * to get receiver id
	 * @return receiver id
	 */
	public int getReceiver() {
		return this.receiverId;
	}
	
	/**
	 * to get sender id
	 * @return sender id
	 */
	public int getSenderId() {
		return this.senderId;
	}
	
	/**
	 *  to get movie id
	 * @return movie id
	 */
	public String getMovieId() {
		return this.movieId;
	}
	
	/**
	 * To get movie name
	 * @return movie name
	 */
	public String getMovieName() {
		return this.movieName;
	}
	
	/**
	 * To get comment
	 * @return the comment that sender has
	 */
	public String getComment() {
		return this.senderComment;
	}
	
	/**
	 * To get the time of this prod
	 * @return the time that the prod was sent out
	 */
	public String getTime() {
		return this.date;
	}
	
	/**
	 * The setter 
	 */
	
	/**
	 * Set receiver
	 * @param receiverid the id for receiver
	 */
	public void setReceiver(Integer receiverid) {
		this.receiverId = receiverid;
	}
	
	/**
	 * To set the senderId
	 * @param senderId id for sender
	 */
	public void setSender(Integer senderId) {
		this.senderId = senderId;
	}
	
	/**
	 * To set the movie id for this prod
	 * @param mid movie id
	 */
	public void setMovieId(String mid) {
		this.movieId = mid;
	}
	
	/**
	 * To set the movie name
	 * @param movieName
	 */
	public void setMovieName(String mName) {
		this.movieName = mName;
	}
	
	/**
	 * add the time for the prod to be sent out
	 */
	public void setTime(String date) {
		this.date = date;
	}
	
	
	/**
	 * The sender to set comment on this prod
	 * @param message the message that sender wrote
	 */
	public void setComment(String message) {
		this.senderComment = message;
	}
	
	
	
	
	
}
