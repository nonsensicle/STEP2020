package com.google.sps.data;

import com.google.cloud.vision.v1.BoundingPoly;
import com.google.gson.*;
import com.google.gson.annotations.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Simple class representing a comment. Based on SubtractionGame.java by Google.
 * written by Leah Ryu 6/30/20
 *
 * Note: The private variables in this class are converted into JSON.
 */
public class Comment {

  // List of instance variables. 
  // Expose annotation ensures that variables are picked up by Gson toJson().
  @Expose
  private String fname;
  @Expose 
  private String surname;
  @Expose
  private String email;
  @Expose
  private Date date;
  @Expose
  private String message;
  @Expose 
  private String blobKey;
  @Expose 
  private ArrayList<String> imageDescriptions;       // a list of descriptions for the images
  @Expose 
  private ArrayList<Float> descScores;               // a list of confidence scores corresponding to the image descriptions
  @Expose
  private ArrayList<String> objectNames;             // a list of objects detected in the images
  @Expose 
  private ArrayList<Float> objectScores;             // a list of corresponding object confidence scores
  @Expose
  private final long id;  // Unique ID number for each entity from Datastore.

  public Comment(String fname, String surname, String email, Date date, String message, String blobKey, 
        ArrayList<String> imageDescriptions, ArrayList<Float> descScores, ArrayList<String> objectNames, 
        ArrayList<Float> objectScores, long id) {
    this.fname = fname;
    this.surname = surname;
    this.email = email;
    this.date = date;
    this.message = message;
    this.blobKey = blobKey;
    this.imageDescriptions = imageDescriptions;
    this.descScores = descScores;
    this.objectNames = objectNames;
    this.objectScores = objectScores;
    this.id = id;
  }
  
  // Getters and setters.
  public String getFname() {return this.fname;}

  public String getSurname() {return this.surname;}

  public String getEmail() {return this.email;}

  public Date getDate() {return this.date;}

  public String getMessage() {return this.message;}

  public long getID() {return this.id;}
  
  public void setFname(String fname) {this.fname = fname;}

  public void setSurname(String surname) {this.surname = surname;}

  public void setEmail(String email) {this.email = email;}

  public void setDate(Date date) {this.date = date;}

  public void setMessage(String message) {this.message = message;}
}
