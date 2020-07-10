package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import com.google.gson.*;
import com.google.gson.annotations.*;

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
  private final long id;  // Unique ID number for each entity from Datastore.

  public Comment(String fname, String surname, String email, Date date, String message, String blobKey, long id) {
    this.fname = fname;
    this.surname = surname;
    this.email = email;
    this.date = date;
    this.message = message;
    this.blobKey = blobKey;
    this.id = id;
  }
}