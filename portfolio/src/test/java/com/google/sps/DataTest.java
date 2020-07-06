package com.google.sps;

import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Ignore;
import org.junit.Before;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.servlets.DataServlet;
import com.google.sps.data.Comment;
import com.google.appengine.api.datastore.Entity;

/** Test class for DataServlet.java (run with maven). */
@RunWith(MockitoJUnitRunner.class)
public final class DataTest {

  private DataServlet myServlet;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private Comment myComment;
  private final int MAX_COMMENTS_DSERVLET = 50;

  /** Runs before each other methods to set up objects.*/
  @Before 
  public void setUp() {
    request = mock(HttpServletRequest.class);       
    response = mock(HttpServletResponse.class);    
    myServlet = new DataServlet();
  }
  
  /** Test doGet() to see if comments are correctly put into JSON. */ 
  @Test 
  public void testDoGet() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    
    when(myServlet.getNumComments(request)).thenReturn(-1);

    myServlet.doGet(request, response);
    
    printWriter.flush();
    assertTrue(stringWriter.toString().contains("Please enter an integer in the range 1 - " +
        MAX_COMMENTS_DSERVLET + "."));
    assertEquals("application/json", response.getContentType());
  } 

  /** Test doGet() to see if comments are correctly put into JSON. 
  @Test 
  public void testDoGet() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    
    Date date;
    myComment = new Comment("Diana", "M", "mock@google.com", date = new Date(), "Message.", 1L);
    when(new Comment(fname, surname, email, date, message, 1L)).thenReturn(myComment);

    myServlet.doGet(request, response);

    // assertTrue(stringWriter.toString().contains("{fname:"Diana",}"));
    assertEquals("application/json", response.getContentType());
  } */

  // request.addParameter(title, info);
}
