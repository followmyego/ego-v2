package com.egobeta.ego;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;


/**
 * Simple class for user settings.
 */
public class UserPermissions {
    private static final String LOG_TAG = UserPermissions.class.getSimpleName();

    // dataset name to store user permissions
    private static final String USER_PERMISSIONS_DATASET_NAME = "user_permissions";

    // Intent action used in local broadcast
    public static final String ACTION_PERMISSIONS_CHANGED = "user-permissions-changed";

    // key names in dataset
    private static final String KEY_NEW_USER = "new_user";
    private static final String KEY_PERMISSION_FRIENDS = "friends";
    private static final String KEY_PERMISSION_FRIENDS_OF_FRIENDS = "friends_of_friends";
    private static final String KEY_PERMISSION_INSTAGRAM_FOLLOWERS = "instagram_followers";
    private static final String KEY_PERMISSION_INSTAGRAM_FOLLOWING = "instagram_following";
    private static final String KEY_PERMISSION_LOCATION = "location";
    private static final String KEY_PERMISSION_HOMETOWN = "hometown";
    private static final String KEY_PERMISSION_LIKES = "common_likes";
    private static final String KEY_PERMISSION_BIRTHDAY = "birthday";
    private static final String KEY_PERMISSION_WORK = "workplace";
    private static final String KEY_PERMISSION_SCHOOL = "school";
    private static final String KEY_PERMISSION_MUSIC = "music";
    private static final String KEY_PERMISSION_MOVIES = "movies";
    private static final String KEY_PERMISSION_BOOKS = "books";


    private static UserPermissions instance;

    // default values
    private static int DEFAULT_PERMISSION = 1; // white

    //Class Variables
    private int newUser = 0;
    private int friends = DEFAULT_PERMISSION;
    private int friendsOfFriends = DEFAULT_PERMISSION;
    private int instagramFollowers = DEFAULT_PERMISSION;
    private int instagramFollowing = DEFAULT_PERMISSION;
    private int location = DEFAULT_PERMISSION;
    private int hometown = DEFAULT_PERMISSION;
    private int commonLikes = DEFAULT_PERMISSION;
    private int birthday = DEFAULT_PERMISSION;
    private int workplace = DEFAULT_PERMISSION;
    private int school = DEFAULT_PERMISSION;
    private int music = DEFAULT_PERMISSION;
    private int movies = DEFAULT_PERMISSION;
    private int books = DEFAULT_PERMISSION;



    String[] keys = {
            KEY_NEW_USER,
            KEY_PERMISSION_FRIENDS ,
            KEY_PERMISSION_FRIENDS_OF_FRIENDS ,
            KEY_PERMISSION_INSTAGRAM_FOLLOWERS,
            KEY_PERMISSION_INSTAGRAM_FOLLOWING,
            KEY_PERMISSION_LOCATION,
            KEY_PERMISSION_HOMETOWN,
            KEY_PERMISSION_LIKES,
            KEY_PERMISSION_BIRTHDAY,
            KEY_PERMISSION_WORK,
            KEY_PERMISSION_SCHOOL ,
            KEY_PERMISSION_MUSIC,
            KEY_PERMISSION_MOVIES ,
            KEY_PERMISSION_BOOKS
    };




    public int getNewUser() {
        return newUser;
    }

    public void setNewUser(int newUser) {
        this.newUser = newUser;
    }

    public int getFriends() {
        return friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public int getFriendsOfFriends() {
        return friendsOfFriends;
    }

    public void setFriendsOfFriends(int friendsOfFriends) {
        this.friendsOfFriends = friendsOfFriends;
    }

    public int getInstagramFollowers() {
        return instagramFollowers;
    }

    public void setInstagramFollowers(int instagramFollowers) {
        this.instagramFollowers = instagramFollowers;
    }

    public int getInstagramFollowing() {
        return instagramFollowing;
    }

    public void setInstagramFollowing(int instagramFollowing) {
        this.instagramFollowing = instagramFollowing;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getHometown() {
        return hometown;
    }

    public void setHometown(int hometown) {
        this.hometown = hometown;
    }

    public int getCommonLikes() {
        return commonLikes;
    }

    public void setCommonLikes(int commonLikes) {
        this.commonLikes = commonLikes;
    }

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    public int getWorkplace() {
        return workplace;
    }

    public void setWorkplace(int workplace) {
        this.workplace = workplace;
    }

    public int getSchool() {
        return school;
    }

    public void setSchool(int school) {
        this.school = school;
    }

    public int getMusic() {
        return music;
    }

    public void setMusic(int music) {
        this.music = music;
    }

    public int getMovies() {
        return movies;
    }

    public void setMovies(int movies) {
        this.movies = movies;
    }

    public int getBooks() {
        return books;
    }

    public void setBooks(int books) {
        this.books = books;
    }

    public static UserPermissions getInstance() {
        return instance;
    }

    public static void setInstance(UserPermissions instance) {
        UserPermissions.instance = instance;
    }

    /**
     * Loads user settings from local dataset into memory.
     */
    public void loadFromDataset() {
        Dataset dataset = getDataset();

        final String newUserD = dataset.get(KEY_NEW_USER);
        if (newUserD != null) {
            newUser = Integer.valueOf(newUserD);
        }
        final String friendsD = dataset.get(KEY_PERMISSION_FRIENDS);
        if (friendsD != null) {
            friends = Integer.valueOf(friendsD);
        }
        final String friendsOfFriendsD = dataset.get(KEY_PERMISSION_FRIENDS_OF_FRIENDS);
        if (friendsOfFriendsD != null) {
            friendsOfFriends = Integer.valueOf(friendsOfFriendsD);
        }
        final String instagramFollowersD = dataset.get(KEY_PERMISSION_INSTAGRAM_FOLLOWERS);
        if (instagramFollowersD != null) {
            instagramFollowers = Integer.valueOf(instagramFollowersD);
        }
        final String instagramFollowingD = dataset.get(KEY_PERMISSION_INSTAGRAM_FOLLOWING);
        if (instagramFollowingD != null) {
            instagramFollowing = Integer.valueOf(instagramFollowingD);
        }
        final String locationD = dataset.get(KEY_PERMISSION_LOCATION);
        if (locationD != null) {
            location = Integer.valueOf(locationD);
        }
        final String hometownD = dataset.get(KEY_PERMISSION_HOMETOWN);
        if (hometownD != null) {
            hometown = Integer.valueOf(hometownD);
        }
        final String commonLikesD = dataset.get(KEY_PERMISSION_LIKES);
        if (commonLikesD != null) {
            commonLikes = Integer.valueOf(commonLikesD);
        }
        final String birthdayD = dataset.get(KEY_PERMISSION_BIRTHDAY);
        if (birthdayD != null) {
            birthday = Integer.valueOf(birthdayD);
        }
        final String workplaceD = dataset.get(KEY_PERMISSION_WORK);
        if (workplaceD != null) {
            workplace = Integer.valueOf(workplaceD);
        }
        final String schoolD = dataset.get(KEY_PERMISSION_SCHOOL);
        if (schoolD != null) {
            school = Integer.valueOf(schoolD);
        }
        final String musicD = dataset.get(KEY_PERMISSION_MUSIC);
        if (musicD != null) {
            music = Integer.valueOf(musicD);
        }
        final String moviesD = dataset.get(KEY_PERMISSION_MOVIES);
        if (moviesD != null) {
            movies = Integer.valueOf(moviesD);
        }
        final String booksD = dataset.get(KEY_PERMISSION_BOOKS);
        if (booksD != null) {
            books = Integer.valueOf(booksD);
        }



    }





    /**
     * Saves in memory user settings to local dataset.
     */
    public void saveToDataset() {
        int[] variables = {
                newUser,
                friends,
                friendsOfFriends,
                instagramFollowers,
                instagramFollowing,
                location,
                hometown,
                commonLikes,
                birthday,
                workplace,
                school,
                music,
                movies,
                books
        };
        Dataset dataset = getDataset();

        //Save item booleans
        for(int i = 0; i < variables.length; i++){
            dataset.put(keys[i], String.valueOf(variables[i]));
            Log.d(LOG_TAG, "onSuccess - dataset updated: " + keys[i] + " " + String.valueOf(variables[i]));
        }
    }

    /**
     * Gets the Cognito dataset that stores user settings.
     *
     * @return Cognito dataset
     */
    public Dataset getDataset() {
        return AWSMobileClient.defaultMobileClient()
                .getSyncManager()
                .openOrCreateDataset(USER_PERMISSIONS_DATASET_NAME);
    }

    /**
     * Gets a singleton of user settings
     *
     * @return user settings
     */
    public static UserPermissions getInstance(final Context context) {
        if (instance != null) {
            return instance;
        }
        instance = new UserPermissions();
        final IdentityManager identityManager = AWSMobileClient.defaultMobileClient()
                .getIdentityManager();
        identityManager.addSignInStateChangeListener(
                new IdentityManager.SignInStateChangeListener() {
                    @Override
                    public void onUserSignedIn() {
                        Log.d(LOG_TAG, "load from dataset on user sign in");
                        instance.loadFromDataset();
                    }

                    @Override
                    public void onUserSignedOut() {
                        Log.d(LOG_TAG, "wipe user data after sign out");
                        AWSMobileClient.defaultMobileClient().getSyncManager().wipeData();

                        //Set the default newUser variable for this instance if user signs out
                        instance.setNewUser(0);
                        instance.setFriends(DEFAULT_PERMISSION);
                        instance.setFriendsOfFriends(DEFAULT_PERMISSION);
                        instance.setInstagramFollowers(DEFAULT_PERMISSION);
                        instance.setInstagramFollowing(DEFAULT_PERMISSION);
                        instance.setLocation(DEFAULT_PERMISSION);
                        instance.setHometown(DEFAULT_PERMISSION);
                        instance.setCommonLikes(DEFAULT_PERMISSION);
                        instance.setBirthday(DEFAULT_PERMISSION);
                        instance.setWorkplace(DEFAULT_PERMISSION);
                        instance.setSchool(DEFAULT_PERMISSION);
                        instance.setMusic(DEFAULT_PERMISSION);
                        instance.setMovies(DEFAULT_PERMISSION);
                        instance.setBooks(DEFAULT_PERMISSION);


                        instance.saveToDataset();
                        final Intent intent = new Intent(ACTION_PERMISSIONS_CHANGED);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
        return instance;
    }
}

