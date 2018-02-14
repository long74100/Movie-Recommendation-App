package edu.northeastern.cs4500.controllers.hello;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Simple example of CRU services on an object
 * @author Alec
 *
 */
@Entity(name="hello")
public class HelloObject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String message;
	
	public HelloObject() {
		
	}
	
	public HelloObject(String message) {
		this.message = message;
	}
	
	/*
	 * getter and setter
	 */
	public String getMessage() {
		return message;
	}
	
	public int getId() {
	return id;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setId(int id) {
	this.id = id;
	}
}
