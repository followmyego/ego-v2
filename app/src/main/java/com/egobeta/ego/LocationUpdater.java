package com.egobeta.ego;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.List;


/**
 * Created by Lucas on 06/10/2016.
 */
public class LocationUpdater implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    //Location/GPS services variables
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String TAG = LocationUpdater.class.getSimpleName();
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    public boolean mRequestLocationUpdates = false;
    private LocationRequest mLocationRequest;
    int firstTime = 0;
    private Context context;

    //Other variables
    private List<String> usersImages;
    private List<String> usernames;
    private static String serverURL = "http://ebjavasampleapp-env.us-east-1.elasticbeanstalk.com/dynamodb-geo";
    private String googleAPI = "AIzaSyAyMXHOJdJg6Jjj64SZnmyxIaY2lWvKDC0";
    private Activity activity;
    private String username = "username";
    double longitude;
    double latitude;
    //    UserLocation userLocation;
    IdentityManager identityManager;
    DynamoDBMapper mapper;


    public LocationUpdater(Activity activity, Context context, IdentityManager identityManager, DynamoDBMapper mapper) {
        this.activity = activity;
        this.context = context;
        this.identityManager = identityManager;
        this.mapper = mapper;
    }

    public void saveToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AmazonClientException lastException = null;


//                    userLocation = new UserLocation();
//                    userLocation.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
//                    userLocation.setFacebookId(identityManager.getUserFacebookId());
////                    userLocation.setLongitude(getLongitude() + "");
////                    userLocation.setLatitude(getLatitude() + "");
//                    userLocation.setLongitude("49.888747");
//                    userLocation.setLatitude("-119.491591");

                    try {
//                        mapper.save(userLocation);
                    } catch (final AmazonClientException ex) {
                        Log.e("AMAZON EXCEPTION", "Failed saving item : " + ex.getMessage(), ex);
                        lastException = ex;
                    }

                    if (lastException != null) {
                        // Re-throw the last exception encountered to alert the user.
                        throw lastException;
                    }

                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.


                    return;
                }
//                ThreadUtils.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
//                        dialogBuilder.setTitle(R.string.nosql_dialog_title_added_sample_data_text);
//                        dialogBuilder.setMessage(R.string.nosql_dialog_message_added_sample_data_text);
//                        dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
//                        dialogBuilder.show();
//                    }
//                });
            }
        }).start();
    }


    public void theOnCreateMethod() {
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
            displayLocation();
        }
    }

    public void theOnStartMethod() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void theOnResumeMethod() {
        checkPlayServices();
        if (mGoogleApiClient.isConnected() && mRequestLocationUpdates && mGoogleApiClient != null) {
            startLocationUpdates();
        }
    }



    protected void theOnPauseMethod() {
        stopLocationUpdates();
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    private void displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            longitude = mLastLocation.getLongitude();
            latitude = mLastLocation.getLatitude();
            //lblLocation.setText(latitude + " " + longitude);


            if (getLongitude() != 0 && getLatitude() != 0) {
                EgoStream.PushUserLocationToDataBase pushLocation = new EgoStream.PushUserLocationToDataBase();
                pushLocation.execute();
            } else {
                startLocationUpdates();
                mRequestLocationUpdates = true;
            }


//            saveToDB();
            Toast.makeText(activity, "Long: " + longitude + " Lat: " + latitude, Toast.LENGTH_SHORT).show();
            System.out.println("Long: " + longitude + " Lat: " + latitude + " facebookId: " + identityManager.getUserFacebookId());
        } else {
            //lblLocation.setText("Couldn't get the location. Make sure location is enabled on the device");
        }
    }

    public void PushLocation(int count) {
//        new PushUserLocationToDataBase(count).execute(serverURL);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 20000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FASTEST_INTERVAL = 5000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //int DISPLACEMENT = 1000;
        //mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(activity, "This device is not supported", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }


    public void startLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    protected void stopLocationUpdates(){
        if(mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();

        if(mRequestLocationUpdates){
            startLocationUpdates();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //Toast.makeText(getApplicationContext(), "Location changed", Toast.LENGTH_SHORT).show();

        displayLocation();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }

}
