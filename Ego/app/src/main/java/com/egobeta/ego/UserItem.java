package com.egobeta.ego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.egobeta.ego.TableClasses.User_Badges;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Lucas on 06/10/2016.
 */

public class UserItem {
    Context context;
    Drawable profilePicture = null;
    String facebookId;
    int badge = 0;
    boolean isFriend;
    ImageView imageView;
    boolean isLoading = false;
    boolean isPinned = false;
    boolean isNearby = false;
    String firstName;
    GraphResponse response;



    public UserItem(Context context, String facebookId, int badge, boolean isPinned) {
        this.context = context;
        this.facebookId = facebookId;
        this.badge = badge;
        this.isPinned = isPinned;
        getFacebookInfo();
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setIsPinned(boolean isPinned) {
        isPinned = isPinned;
    }

    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }

    private void setUserProfilePicture(){
        if(profilePicture == null){
            new LoadUserImageAsyncTask().execute(facebookId);
        }

    }

    public Drawable getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Drawable profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public void setViewItem(ImageView imageView, boolean isFriend) {
        this.imageView = imageView;
        new LoadUserImageAsyncTask().execute(facebookId);
        this.isFriend = isFriend;
    }




    public class LoadUserImageAsyncTask extends AsyncTask<String, Void, String> {

        String userImageUrl;
        Bitmap userImage;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            isLoading = true;
            //Code to Load Bitmap
            userImageUrl = "https://graph.facebook.com/" + params[0] + "/picture?width=190&height=190";

            try {
                final InputStream is = new URL(userImageUrl).openStream();
                userImage = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                // clear user image
                userImage = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(userImage != null){
                System.out.println("UserItem: ONE");
                profilePicture = new BitmapDrawable(context.getResources(), userImage);
            } else {
                System.out.println("UserItem: TWO");
                System.out.println("User Image eqauls null");
            }

            if(imageView != null){
                System.out.println("UserItem: THREE");
                if(profilePicture != null){
                    System.out.println("UserItem: FOUR");
                    imageView.setImageDrawable(profilePicture);
                }
            }

            isLoading = false;
        }


    }





    /** Get the info from the facebook api**/
    public String getFacebookInfo() {
        final String[] name = new String[1];
        Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    sleep(1000);
//                    final Bundle parameters = new Bundle();
//                    parameters.putString("fields", "name");
//                    final GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "me");
//                    graphRequest.setParameters(parameters);
//                    response = graphRequest.executeAndWait();
//                    getBadgeVariablesFromResponse(response);




                    /* make the API call */
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(), "/" + facebookId, null, HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    /* handle the result */
                                    firstName = getBadgeVariablesFromResponse(response);
                                }
                            }
                    ).executeAsync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        return firstName;
    }

    /** Gets the variables set by the user's privacy preferences needed for the badge class **/
    public String getBadgeVariablesFromResponse(GraphResponse response){

        //Get json object from response
        JSONObject json = response.getJSONObject();
//        System.out.println("FaceBook name: " + json.toString());
        String name = null;
        try {
            if(json != null){
                name = json.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(name != null){
            String[] separated = name.split(" ");
            System.out.println("FaceBook name: " + separated[0]);
            firstName = separated[0];
            setFirstName(separated[0]);
        }

        return firstName;
    }
}
