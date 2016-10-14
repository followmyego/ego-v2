package com.egobeta.ego.TableClasses;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "user_profile")
public class User_Profile {

    private String facebookId;
    private String status;
    private int views;
    private String firstName;
    private String lastName;
    private String age;
    private String email;

    private String snapchat_username;
    private String instagram_id;
    private String instagram_username;
    private String twitter_id;
    private String twitter_username;
    private String google_plus_id;
    private String linkedIn_id;

    private String instagram_photos_connected;

    private String facebookConnected;
    private String google_plusConnected;
    private String twitterConnected;
    private String instagramConnected;
    private String vineConnected;
    private String pinterestConnected;
    private String snapchatConnected;
    private String linkedInConnected;



    @DynamoDBHashKey(attributeName = "facebook_id")
    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    @DynamoDBAttribute(attributeName = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = "views")
    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @DynamoDBAttribute(attributeName = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @DynamoDBAttribute(attributeName = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @DynamoDBAttribute(attributeName = "age")
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @DynamoDBAttribute(attributeName = "snapchat_username")
    public String getSnapchat_username() {
        return snapchat_username;
    }

    public void setSnapchat_username(String snapchat_username) {
        this.snapchat_username = snapchat_username;
    }

    @DynamoDBAttribute(attributeName = "instagram_id")
    public String getInstagram_id() {
        return instagram_id;
    }

    public void setInstagram_id(String instagram_id) {
        this.instagram_id = instagram_id;
    }

    @DynamoDBAttribute(attributeName = "instagram_username")
    public String getInstagram_username() {
        return instagram_username;
    }

    public void setInstagram_username(String instagram_username) {
        this.instagram_username = instagram_username;
    }

    @DynamoDBAttribute(attributeName = "twitter_id")
    public String getTwitter_id() {
        return twitter_id;
    }

    public void setTwitter_id(String twitter_id) {
        this.twitter_id = twitter_id;
    }

    @DynamoDBAttribute(attributeName = "twitter_username")
    public String getTwitter_username() {
        return twitter_id;
    }

    public void setTwitter_username(String twitter_username) {
        this.twitter_username = twitter_username;
    }

    @DynamoDBAttribute(attributeName = "googlePlus_id")
    public String getGoogle_plus_id() {
        return google_plus_id;
    }

    public void setGoogle_plus_id(String google_plus_id) {
        this.google_plus_id = google_plus_id;
    }

    @DynamoDBAttribute(attributeName = "linkedIn_id")
    public String getLinkedIn_id() {
        return linkedIn_id;
    }

    public void setLinkedIn_id(String linkedIn_id) {
        this.linkedIn_id = linkedIn_id;
    }

    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "instagram_photos_connected")
    public String getInstagram_photos_connected() {
        return instagram_photos_connected;
    }

    public void setInstagram_photos_connected(String instagram_photos_connected) {
        this.instagram_photos_connected = instagram_photos_connected;
    }




    @DynamoDBAttribute(attributeName = "facebook_connected")
    public String getFacebookConnected() {
        return facebookConnected;
    }

    public void setFacebookConnected(String facebookConnected) {
        this.facebookConnected = facebookConnected;
    }

    @DynamoDBAttribute(attributeName = "google_plus_connected")
    public String getGoogle_plusConnected() {
        return google_plusConnected;
    }

    public void setGoogle_plusConnected(String google_plusConnected) {
        this.google_plusConnected = google_plusConnected;
    }

    @DynamoDBAttribute(attributeName = "twitter_connected")
    public String getTwitterConnected() {
        return twitterConnected;
    }

    public void setTwitterConnected(String twitterConnected) {
        this.twitterConnected = twitterConnected;
    }

    @DynamoDBAttribute(attributeName = "instagram_connected")
    public String getInstagramConnected() {
        return instagramConnected;
    }

    public void setInstagramConnected(String instagramConnected) {
        this.instagramConnected = instagramConnected;
    }

    @DynamoDBAttribute(attributeName = "vine_connected")
    public String getVineConnected() {
        return vineConnected;
    }

    public void setVineConnected(String vineConnected) {
        this.vineConnected = vineConnected;
    }

    @DynamoDBAttribute(attributeName = "pinterest_connected")
    public String getPinterestConnected() {
        return pinterestConnected;
    }

    public void setPinterestConnected(String pinterestConnected) {
        this.pinterestConnected = pinterestConnected;
    }

    @DynamoDBAttribute(attributeName = "snapchat_connected")
    public String getSnapchatConnected() {
        return snapchatConnected;
    }

    public void setSnapchatConnected(String snapchatConnected) {
        this.snapchatConnected = snapchatConnected;
    }

    @DynamoDBAttribute(attributeName = "linkedIn_connected")
    public String getLinkedInConnected() {
        return linkedInConnected;
    }

    public void setLinkedInConnected(String linkedInConnected) {
        this.linkedInConnected = linkedInConnected;
    }
}

