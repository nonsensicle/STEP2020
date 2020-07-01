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

  public Comment(String first, String last, String mail, String msg) {
    fname = first;
    surname = last;
    email = mail;
    date = new Date();
    message = msg;
  }
}