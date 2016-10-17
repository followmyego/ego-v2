package com.egobeta.ego.InstagramClasses;

import android.content.SharedPreferences;
import android.content.Context;

/**
 * Created by Lucas on 13/03/2016.
 */
public class InstagramSession {

    //Declare variables
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String INSTAGRAM_SP = "Instagram_Preferences";
    private static final String API_USERNAME = "username";
    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_ACCESS_TOKEN = "access_token";


    //Constructor for this class
    public InstagramSession(Context context) {
        sharedPref = context.getSharedPreferences(INSTAGRAM_SP, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }


    /**
     *
     * @param accessToken
     * @param expireToken
     * @param expiresIn
     * @param username
     */
    public void storeAccessToken(String accessToken, String id, String username, String name) {
        editor.putString(API_ID, id);
        editor.putString(API_NAME, name);
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.putString(API_USERNAME, username);
        editor.commit();
    }


    public void storeAccessToken(String accessToken) {
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.commit();
    }


    /**
     * Reset access token and user name
     */
    public void resetAccessToken() {
        /*editor.putString(API_ID, null);
        editor.putString(API_NAME, null);
        editor.putString(API_ACCESS_TOKEN, null);
        editor.putString(API_USERNAME, null);
        editor.commit();*/

        editor.clear();
        editor.commit();
    }


    /**
     * Get user name
     *
     * @return User name
     */
    public String getUsername() {
        return sharedPref.getString(API_USERNAME, null);
    }


    /**
     *
     * @return
     */
    public String getId() {
        return sharedPref.getString(API_ID, null);
    }


    /**
     *
     * @return
     */
    public String getName() {
        return sharedPref.getString(API_NAME, null);
    }


    /**
     * Get access token
     *
     * @return Access token
     */
    public String getAccessToken() {
        return sharedPref.getString(API_ACCESS_TOKEN, null);
    }

}
