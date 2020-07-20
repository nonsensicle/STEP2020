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
import org.junit.After;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.servlets.DataServlet;
import com.google.sps.data.Comment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/** Test class for DataServlet.java (run with maven). */
@RunWith(MockitoJUnitRunner.class)
public final class DataTest {

  private DataServlet myServlet;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private Comment myComment;
  private Query query;
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private final int MAX_COMMENTS_DSERVLET = 50;

  /** Runs before each other methods to set up objects.*/
  @Before 
  public void setUp() {
    request = mock(HttpServletRequest.class);       
    response = mock(HttpServletResponse.class);    
    myServlet = new DataServlet();
    query = new Query();
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
