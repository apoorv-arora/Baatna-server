package com.application.baatna.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Uploads the images on S3.
 * */
public class ImageUploader {

	private static final String FACEBOOK_HOST =  "https://www.graph.facebook.com/";
	
	private static String LARGE = "large";
	private static String MEDIUM = "medium";
	private static String SQUARE = "square";
	private static String ALBUM = "album";
	private static String SMALL = "small";
	
	private static String[] types = new String[]{ LARGE, MEDIUM, SQUARE, ALBUM, SMALL };
	
	public static void getImageFromFacebook(String userId, String accessToken) {
		
		for(String type:types) {
			
			URL url = null;
			try {
				url = new URL( FACEBOOK_HOST + userId + "/pictures?type=" + type + "access_token=" + accessToken);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			if(url != null) {
				URLConnection connection;
				try {
					connection = url.openConnection();
					connection.setRequestProperty("User-Agent", "xxxxxx");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
	public static void storeImageInS3 () {
		
	}
	
}
