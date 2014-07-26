package com.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
/*
 * https://developers.google.com/admin-sdk/directory/v1/quickstart/quickstart-java
 * https://developers.google.com/admin-sdk/directory/v1/guides/authorizing
 * http://stackoverflow.com/questions/18711849/google-admin-sdk-user-access-api-using-java  (via service account oauth)
 */
public class UserFeed {

	  private static String CLIENT_ID = "569157645219-sbgk8uog7g0rsf3pcqljd1v6bs40vevb.apps.googleusercontent.com";
	  private static String CLIENT_SECRET = "xsQ9JF9aftahAcdxXxAYlfQ0";

	  private static String REDIRECT_URI = "http://localhost:8080/oauth2callback";
	  
	  private static HttpTransport httpTransport;
	  private static JsonFactory jsonFactory;
	  private static GoogleAuthorizationCodeFlow flow;
	  
	  public UserFeed() {
		  httpTransport = new NetHttpTransport();
		  jsonFactory = new JacksonFactory();

		  flow = new GoogleAuthorizationCodeFlow.Builder(
				 httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY))
	        	.setAccessType("online")
	        	.setApprovalPrompt("auto").build();
	  }
	  
	  public String getRedirectUrl() {
		  String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
		  return url;
	  }

	  public List<User> getUserList(String code) throws IOException {
	    GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
	    GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

	    // Create a new authorized API client
	    Directory service = new Directory.Builder(httpTransport, jsonFactory, credential)
	        .setApplicationName("DirectoryCommandLine")
	        .build();

	    List<User> allUsers = new ArrayList<User>();
	    Directory.Users.List request = service.users().list().setCustomer("my_customer");

	    // Get all users
	    do {
	      try {
	        Users currentPage = request.execute();
	        allUsers.addAll(currentPage.getUsers());
	        request.setPageToken(currentPage.getNextPageToken());
	      } catch (IOException e) {
	        System.out.println("An error occurred: " + e);
	        request.setPageToken(null);
	      }
	    } while (request.getPageToken() != null &&
	             request.getPageToken().length() > 0 );

	    // Print all users
	    return allUsers;
	  }
	  
	  public static void main(String[] args) throws IOException {
		  UserFeed feed = new UserFeed();
		  
		  String url = feed.getRedirectUrl();
		  System.out.println("Please open the following URL in your browser then type the authorization code:");
		  System.out.println("  " + url);
		  System.out.println("Enter authorization code:");
		  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		  String code = br.readLine();
		    
		  List<User> allUsers = feed.getUserList(code);
		  
		  //if the user is not a admin, it will return the below error json
		  /*
		   * An error occurred: com.google.api.client.googleapis.json.GoogleJsonResponseException: 403 Forbidden
			{
			  "code" : 403,
			  "errors" : [ {
			    "domain" : "global",
			    "message" : "Not Authorized to access this resource/api",
			    "reason" : "forbidden"
			  } ],
			  "message" : "Not Authorized to access this resource/api"
			}

		   */
		  for (User user : allUsers) {
		      System.out.println(user.getPrimaryEmail()+"   "+user.getName().getFullName());
		    }
	  }
}