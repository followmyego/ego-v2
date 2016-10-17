package com.egobeta.ego;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.egobeta.ego.TableClasses.User_Badges;
import com.egobeta.ego.TableClasses.User_Profile;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 06/10/2016.
 */
public class AsynchronousProfileUpdating {



    private final static String LOG_TAG = "LFP";
    Context context;
    Activity activity;
    User_Badges userBadges;
    User_Profile userProfile;

    private String facebookId;
    DynamoDBMapper mapper = null;
    GraphResponse response;


    public AsynchronousProfileUpdating(Context context, Activity activity, String facebookId){
        this.context = context;
        this.activity = activity;
        this.facebookId = facebookId;

        userBadges = new User_Badges();
        userProfile = new User_Profile();

        /** Initialize the mapper for DynamoDB **/
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();

        /** Sync the user privacy settings **/
        syncPrivacySettings();
    }




    /** Sync user's preferences only if user is signed in **/
    private void syncPrivacySettings() {
        System.out.println("MAINACTIVITY: syncPrivacySettings");
        // sync only if user is signed in
        if (AWSMobileClient.defaultMobileClient().getIdentityManager().isUserSignedIn()) {
            final UserPermissions userPermissions = UserPermissions.getInstance(context);
            userPermissions.getDataset().synchronize(new DefaultSyncCallback() {
                @Override
                public void onSuccess(final Dataset dataset, final List<Record> updatedRecords) {
                    super.onSuccess(dataset, updatedRecords);
                    Log.d(LOG_TAG, "successfully synced user settings");

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            loadUserSettings(dialog);
//                        }
//                    });

                    loadUserSettings();
                }
            });
        }
    }

    /** userPermissions.loadFromDataset(); get called here **/
    private void loadUserSettings() {
        final UserPermissions userPermissions = UserPermissions.getInstance(context);
        final Dataset dataset = userPermissions.getDataset();
        Log.d(LOG_TAG, "Loading user settings from remote");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(final Dataset dataset, final List<Record> updatedRecords) {
                super.onSuccess(dataset, updatedRecords);
                userPermissions.loadFromDataset();
                getFacebookInfo(userPermissions);

            }

            @Override
            public void onFailure(final DataStorageException dse) {
                Log.w(LOG_TAG, "Failed to load user settings from remote, using default.", dse);

            }

            @Override
            public boolean onDatasetsMerged(final Dataset dataset,
                                            final List<String> datasetNames) {
                // Handle dataset merge. One can selectively copy records from merged datasets
                // if needed. Here, simply discard merged datasets
                for (String name : datasetNames) {
                    Log.d(LOG_TAG, "found merged datasets: " + name);
                    AWSMobileClient.defaultMobileClient().getSyncManager().openOrCreateDataset(name).delete();
                }
                return true;
            }
        });
    }

    /** Get the info from the facebook api**/
    private void getFacebookInfo(final UserPermissions userPermissions) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    final Bundle parameters = new Bundle();
                    parameters.putString("fields", "name,picture.type(large), age_range, birthday, context, " +
                            "education, email, favorite_athletes, favorite_teams, hometown, inspirational_people, is_verified, " +
                            "languages, locale, location, work, movies, music, books, friends");
                    final GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "me");
                    graphRequest.setParameters(parameters);
                    response = graphRequest.executeAndWait();
                    getBadgeVariablesFromResponse(response, userPermissions);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    /** Gets the variables set by the user's privacy preferences needed for the badge class **/
    public void getBadgeVariablesFromResponse(GraphResponse response, UserPermissions userPermissions){
        userBadges = new User_Badges();

        //Get json object from response
        JSONObject json = response.getJSONObject();

        //Print json response for debugging purposes
//        printJsonResponse(json);

        //Parse json response into the corresponding variables
        try {
            userBadges.setFacebookId(facebookId);

            if(userPermissions.getFriendsOfFriends() == 1){
                //Need to set the badge variable to 1 so that the database can do a badge comparison on this badge
                userBadges.setFriendsOfFriends("1");
            }

            if(userPermissions.getInstagramFollowers() == 1){
                //Need to set the badge variable to 1 so that the database can do a badge comparison on this badge
                userBadges.setInstagram_follower("1");
            }

            if(userPermissions.getInstagramFollowing() == 1){
                userBadges.setInstagram_following("1");
            }

            if(userPermissions.getLocation() == 1){
                JSONObject locationObject = json.getJSONObject("location");
                String location = locationObject.getString("name");
                userBadges.setLocation(location);
                System.out.println("MAINACTIVITY JSONPARSING: " + location);
            }

            if(userPermissions.getHometown() == 1){
                JSONObject hometownObject = json.getJSONObject("hometown");
                String hometown = hometownObject.getString("name");
                userBadges.setHometown(hometown);
                System.out.println("MAINACTIVITY JSONPARSING: " + hometown);
            }

            if(userPermissions.getCommonLikes() == 1){
                //Create JSON array to store the likes
                JSONArray likesArray = new JSONArray();

                //Get the items from the response JSONArray and add it to our custom json array
                JSONObject contextObject = json.getJSONObject("context");
                JSONObject mutualLikes = contextObject.getJSONObject("mutual_likes");
                JSONArray data = mutualLikes.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonobject = data.getJSONObject(i);
                    String name = jsonobject.getString("name");

                    likesArray.put(name);
                }

                JSONObject likesObject = new JSONObject();
                likesObject.put("likes", likesArray);

                String likes_json = likesObject.toString();

                userBadges.setLikes_json(likes_json);
                System.out.println("MAINACTIVITY JSONPARSING: " + likes_json);
            }

            if(userPermissions.getBirthday() == 1){
                String birthday = json.getString("birthday");
                userBadges.setBirthday(birthday);
                System.out.println("MAINACTIVITY JSONPARSING: " + birthday);
            }

            if(userPermissions.getWorkplace() == 1){
                //Create JSON array to store the work items
                JSONArray workArray = new JSONArray();

                //Get the work array from the json response
                JSONArray data = json.getJSONArray("work");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject workObject = data.getJSONObject(i);

                    //Get work position name
                    JSONObject positionObject = workObject.getJSONObject("position");
                    String position = positionObject.getString("name");

                    //Get work employer name
                    JSONObject employerObject = workObject.getJSONObject("employer");
                    String employer = employerObject.getString("name");

                    //Get work location name
                    JSONObject locationObject = workObject.getJSONObject("location");
                    String location = locationObject.getString("name");

                    JSONObject workItem = new JSONObject();
                    workItem.put("position", position);
                    workItem.put("employer", employer);
                    workItem.put("location", location);
                    workArray.put(workItem);
                }

                JSONObject workObject = new JSONObject();
                workObject.put("work", workArray);

                String workplace_json = workObject.toString();

                userBadges.setWorkplace_json(workplace_json);
                System.out.println("MAINACTIVITY JSONPARSING: " + workplace_json);
            }

            if(userPermissions.getSchool() == 1){
                //Create JSON array to store the school items
                JSONArray educationArray = new JSONArray();

                //Get the education array from the json response
                JSONArray data = json.getJSONArray("education");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject educationObject = data.getJSONObject(i);

                    //Get school name
                    JSONObject schoolObject = educationObject.getJSONObject("school");
                    String school = schoolObject.getString("name");

                    educationArray.put(school);
                }

                JSONObject schoolsObject = new JSONObject();
                schoolsObject.put("schools", educationArray);

                String school_json = schoolsObject.toString();

                userBadges.setSchool_json(school_json);
                System.out.println("MAINACTIVITY JSONPARSING: " + school_json);
            }

            if(userPermissions.getSchool() == 1){
                //Create JSON array to store the skill items
                JSONArray skillsArray = new JSONArray();

                //Get the education array from the json response
                JSONArray data = json.getJSONArray("education");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject educationObject = data.getJSONObject(i);

                    //Get skill name
                    JSONArray concentration = educationObject.getJSONArray("concentration");
                    for(int ii = 0; ii < concentration.length(); ii++){
                        JSONObject skillObject = concentration.getJSONObject(ii);
                        String skill = skillObject.getString("name");

                        skillsArray.put(skill);
                    }
                }

                JSONObject skillObject = new JSONObject();
                skillObject.put("skills", skillsArray);

                String professionalSkills_json = skillObject.toString();

                userBadges.setProfessionalSkills_json(professionalSkills_json);
                System.out.println("MAINACTIVITY JSONPARSING: " + professionalSkills_json);
            }

            if(userPermissions.getMusic() == 1){
                //Create JSON array to store the music items
                JSONArray musicArray = new JSONArray();

                //Get the items from the response JSONArray and add it to our custom json array
                JSONObject musicObject = json.getJSONObject("music");
                JSONArray data = musicObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonobject = data.getJSONObject(i);
                    String name = jsonobject.getString("name");

                    musicArray.put(name);
                }

                musicObject = new JSONObject();
                musicObject.put("music", musicArray);

                String music_json = musicObject.toString();

                userBadges.setMusic_json(music_json);
                System.out.println("MAINACTIVITY JSONPARSING: " + music_json);
            }

            if(userPermissions.getMovies() == 1){
                //Create JSON array to store the movie items
                JSONArray moviesArray = new JSONArray();

                //Get the items from the response JSONArray and add it to our custom json array
                JSONObject movieObject = json.getJSONObject("movies");
                JSONArray data = movieObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonobject = data.getJSONObject(i);
                    String name = jsonobject.getString("name");

                    moviesArray.put(name);
                }

                movieObject = new JSONObject();
                movieObject.put("movies", moviesArray);

                String movies_json = movieObject.toString();

                userBadges.setMovies_json(movies_json);
                System.out.println("MAINACTIVITY JSONPARSING: " + movies_json);
            }

            if(userPermissions.getBooks() == 1){
                //Create JSON array to store the book items
                JSONArray booksArray = new JSONArray();

                //Get the items from the response JSONArray and add it to our custom json array
                JSONObject bookObject = json.getJSONObject("books");
                JSONArray data = bookObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonobject = data.getJSONObject(i);
                    String name = jsonobject.getString("name");

                    booksArray.put(name);
                }

                bookObject = new JSONObject();
                bookObject.put("books", booksArray);

                String books_json = bookObject.toString();

                userBadges.setBooks_json(books_json);
                System.out.println("MAINACTIVITY JSONPARSING: " + books_json);
            }
//            userName = json.getString("name");
//            userImageUrl = json.getJSONObject("picture")
//                    .getJSONObject("data")
//                    .getString("url");
        } catch (final JSONException jsonException) {
            Log.e("LOGTAG",
                    "Unable to get Facebook user info. " + jsonException.getMessage() + "\n" + response,
                    jsonException);
            // Nothing much we can do here.
        }

        /** Push userBadges item to server **/
        new SaveUserBadgesToDB().execute();
    }

    public class SaveUserBadgesToDB extends AsyncTask<Void, Void, Void> {


        public SaveUserBadgesToDB() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                mapper.save(userBadges);
            } catch (AmazonClientException ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Toast.makeText(MainActivity.this, "Successfully saved user's badges to db", Toast.LENGTH_SHORT).show();
            new LoadUserProfile().execute();
        }
    }

    public class LoadUserProfile extends AsyncTask<Void, Void, Void> {


        public LoadUserProfile() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            /** Load user profile from database before saving potential new facebook items  **/
            try {
                userProfile = mapper.load(User_Profile.class, facebookId);
            } catch (final AmazonServiceException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Toast.makeText(MainActivity.this, "Successfully loaded user profile from database", Toast.LENGTH_SHORT).show();
            updateUserProfile();
        }

    }

    /** Updates the user profile info needed from facebook **/
    public void updateUserProfile(){


        //Get json object from response
        JSONObject json = response.getJSONObject();

        try {
            /** Get the variables **/
            //Get name
            String name = json.getString("name");
            String firstAndLastNAme[] = name.split(" ");
            String firstName = firstAndLastNAme[0];
            String lastName = firstAndLastNAme[1];
            //Get age
            JSONObject ageRangeObject = json.getJSONObject("age_range");
            String age = ageRangeObject.getString("min");
            //Get email
            String email = json.getString("email");

            /** Set the variables **/
            userProfile.setFacebookId(facebookId); //Set facebookId
            userProfile.setFirstName(firstName); //Set first name
            userProfile.setLastName(lastName); //Set last name
            userProfile.setAge(age); //Set Age
            userProfile.setEmail(email); //Set email
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** Push userInfo item to server **/
        new SaveUserProfile().execute();
    }

    public class SaveUserProfile extends AsyncTask<Void, Void, Void> {


        public SaveUserProfile() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                mapper.save(userProfile);
            } catch (AmazonClientException ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(activity, "Success - Asynchronously Updated User Data", Toast.LENGTH_SHORT).show();

        }

    }

}
