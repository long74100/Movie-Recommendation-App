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
	
	// the sender's name
	private String senderName; 
	
	// the receiver's name
	private String receiverName;
	
	// the movie's id
	private String movieId; 
	
	// the movie's name 
	private String movieName; 
	
	// the time when the prod is sent
	private String date; 
	
	// the comment that sender has
	private String senderComment; 
	
	// the moviedbid
	private String movieDBId; 
	
	// the movie poster
	private String moviePoster; 
	
	// delete status from receiver
	private boolean isDeletedByReceiver;
	
	// delete status from sender
	private boolean isDeletedBySender;
	
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
	
	public String getMovieDBId() {
		return this.movieDBId;
	}
	
	public String getMoviePoster() {
		return this.moviePoster;
	}
	
	/**
	 * To get receiver name
	 * @return the name of receiver
	 */
	public String getReceiverName() {
		return this.receiverName;
	}
	
	/**
	 * to get sender id
	 * @return sender id
	 */
	public int getSender() {
		return this.senderId;
	}
	
	/**
	 * To get sender's name
	 * @return name of sender
	 */
	public String getSenderName() {
		return this.senderName;
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
	 * To get the delete status from sender
	 * @return true if sender has deleted it
	 */
	public boolean getDeletedStatusFromSender() {
		return this.isDeletedBySender;
	}
	
	/**
	 * To get the delete status from receiver
	 * @return true if receiver deletes it
	 */
	public boolean getDeletedStatusFromReceiver() {
		return this.isDeletedByReceiver;
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
	 * To set sender's name
	 * @param senderName name of sender will be set
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	/**
	 * To set receiver's name
	 * @param receiverName name of receiver will be set
	 */
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
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
	
	public void setMovieDBId(String id) {
		this.movieDBId = id;
	}
	
	public void setMoviePoster(String  poster) {
		this.moviePoster = poster;
	}
	
	
	/**
	 * To set the delete status from sender
	 */
	public void setDeletedStatusFromSender(boolean deleteStatus) {
		 this.isDeletedBySender = deleteStatus;
	}
	
	/**
	 * To set the delete status from receiver
	 */
	public void setDeletedStatusFromReceiver(boolean deleteStatus) {
		this.isDeletedByReceiver = deleteStatus;
	}
	
	
	
	
	
	
}
