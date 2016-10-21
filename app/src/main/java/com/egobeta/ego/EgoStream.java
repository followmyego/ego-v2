package com.egobeta.ego;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.egobeta.R;
import com.egobeta.ego.demo.DemoFragmentBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;

public class EgoStream extends DemoFragmentBase implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_POSITION = "position";
    static ArrayList<String> facebook_Ids = new ArrayList<String>();
    private static final String TAG = "DEBUGGING MESSAGE";
    public ScrollView scrollView;
    private static int mPosition;
    static Typeface typeface;
    public static String facebookId;
    private static GridView gridView;
    public static View v;
    public static EgoStreamAdapter adapter;
    private static ArrayList<String> friends_Ids;
    public static ArrayList<UserItem> userList = new ArrayList<UserItem>();
    static ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    public static LocationUpdater locationUpdater;
    DynamoDBMapper mapper;
    IdentityManager identityManager;
    private static String serverURL = "http://ebjavasampleapp-env.us-east-1.elasticbeanstalk.com/dynamodb-geo";
    public static int current_page = 0;
    static Boolean loadingMore = true;
    Boolean stopLoadingData = false;
    Boolean noMoreUsersToLoad = false;
    private static Context context;
    private static Activity activity;
    public static Bitmap image;
    RelativeLayout profile_toolbar_section;



    public void setLocationUpdater(LocationUpdater locationUpdater){
        this.locationUpdater = locationUpdater;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.ego_stream, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        activity = getActivity();

        // Display the home button on the toolbar that will open the navigation drawer.
        AppCompatActivity containingActivity = (MainActivity) getActivity();
        final ActionBar supportActionBar = containingActivity.getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setTitle("");



        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView egoLogo = ((MainActivity) getActivity()).egoLogo;
        profile_toolbar_section = ((MainActivity) getActivity()).profile_toolbar_section;


        if ( egoLogo != null) {
            egoLogo.setVisibility(View.VISIBLE);
        }

        current_page = 0; //in case someone left the application by clicking the back button
        context = getContext();
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");
        facebookId = MainActivity.facebookId;
//        final DemoListAdapter adapter = new DemoListAdapter(getActivity());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());

        userList = new ArrayList<UserItem>();
        userList.add(new UserItem(getContext(), facebookId, 0, false));
        adapter = new EgoStreamAdapter(userList, getContext(), getActivity(), facebookId, typeface);
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());
//        adapter.addAll(DemoConfiguration.getDemoFeatureList());   ad

        gridView = (GridView) view.findViewById(android.R.id.list);
        gridView.setAdapter(adapter);
        gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final UserItem userItem = adapter.getItem(position);
                final AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    Fragment fragment = UserProfileFragment.newInstance(userItem.getFacebookId(), userItem.getFirstName());



                    activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.profile_fragment_container, fragment, userItem.firstName)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

                    ((MainActivity) getActivity()).navigationDrawer.hideMenuTogglebutton();
                    profile_toolbar_section.setVisibility(View.VISIBLE);

                    ((MainActivity) getActivity()).egoLogo.setVisibility(View.INVISIBLE);

                    // Set the title for the fragment.
                    final ActionBar actionBar = activity.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(userItem.firstName);
                    }
                }
            }
        });
        // ONSCROLLLISTENER
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {

                    if (!stopLoadingData) {
                        // FETCH THE NEXT BATCH OF FEEDS
                        if(!noMoreUsersToLoad){
                            new loadMorePhotos().execute();
                        }
                    }

                }
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);

    }


    /******************************** STREAM GRID ADAPTER ***************************************/
    /*private static final class DemoListAdapter extends ArrayAdapter<DemoConfiguration.DemoFeature> {
        private LayoutInflater inflater;

        public DemoListAdapter(final Context context) {
            super(context, R.layout.stream_gridviewitem);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = inflater.inflate(R.layout.stream_gridviewitem, parent, false);
                holder = new ViewHolder();
                holder.iconImageView = (ImageView) view.findViewById(R.id.list_item_icon);
                holder.titleTextView = (TextView) view.findViewById(R.id.list_item_title);
                holder.subtitleTextView = (TextView) view.findViewById(R.id.list_item_subtitle);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            }

            DemoConfiguration.DemoFeature item = getItem(position);
            holder.iconImageView.setImageResource(item.iconResId);
            holder.titleTextView.setText(item.titleResId);
            holder.subtitleTextView.setText(item.subtitleResId);

            return view;
        }
    }

    private static final class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView subtitleTextView;
    }*/

    /******************************** STREAM GRID ADAPTER ***************************************/
//    private static final class EgoStreamAdapter extends BaseAdapter {
//
//        private Context context;
//        private Activity activity;
//        private ArrayList<String> friends_Ids;
//        private ArrayList<UserItem> userList;
//        private static LayoutInflater inflater = null;
//        ImageLoader imageLoader;
//        String facebookId;
//        Typeface typeface;
//
//        ArrayList<Integer> badgeImages = new ArrayList<>();
//
//
//
//        public EgoStreamAdapter(ArrayList<UserItem> userList, Context context, Activity activity, String facebookId, Typeface typeface) {
//            this.context = context;
//            this.friends_Ids = friends_Ids; /**This should hold be the facebook id's*/
//            this.activity = activity;
//            this.userList = userList;
//            this.facebookId = facebookId;
//            this.typeface = typeface;
//            initializeBadgeImageArrayList();
//
//            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            imageLoader = new ImageLoader(activity.getApplicationContext());
//
//        }
//
//        private void initializeBadgeImageArrayList() {
//            badgeImages = new ArrayList<>();
////            badgeImages.add(R.drawable.common_workplace);
////            badgeImages.add(R.drawable.shared_birthday);
////            badgeImages.add(R.drawable.same_school); //this should be swapped out with skills image
////            badgeImages.add(R.drawable.loves_the_same_music);
////            badgeImages.add(R.drawable.loves_the_same_books);
////            badgeImages.add(R.drawable.loves_the_same_movie);
////            badgeImages.add(R.drawable.same_school);
////            badgeImages.add(R.drawable.same_hometown);
////            badgeImages.add(R.drawable.location_icon);
////            badgeImages.add(R.drawable.common_like);
//
//            badgeImages.add(R.drawable.signing_ego_logo);
//            badgeImages.add(R.drawable.signing_ego_logo);
//            badgeImages.add(R.drawable.signing_ego_logo); //this should be swapped out with skills image
//            badgeImages.add(R.drawable.signing_ego_logo);
//            badgeImages.add(R.drawable.signing_ego_logo);
//            badgeImages.add(R.drawable.signing_ego_logo);
//            badgeImages.add(R.drawable.signing_ego_logo);
//            badgeImages.add(R.drawable.signing_ego_logo);
//            badgeImages.add(R.drawable.signing_ego_logo);
//            badgeImages.add(R.drawable.signing_ego_logo);
//
//        }
//
//        @Override
//        public int getCount() {
//            return 0;
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, final View convertView, final ViewGroup parent) {
//            View view;
//            ViewHolder holder;
//            if (convertView == null) {
//                view = inflater.inflate(R.layout.stream_gridviewitem, parent, false);
//                holder = new ViewHolder();
//                holder.profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
//                holder.profilePictureOverlay = (ImageView) view.findViewById(R.id.profilePictureOverlay);
//                holder.youOverlay = (TextView) view.findViewById(R.id.youOverlay);
//                holder.nameOverlay = (TextView) view.findViewById(R.id.nameOverlay);
//                holder.bottomOverlayView = (RelativeLayout) view.findViewById(R.id.bottomOverlayView);
//                view.setTag(holder);
//            } else {
//                view = convertView;
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            holder.youOverlay.setTypeface(typeface);
//            holder.nameOverlay.setTypeface(typeface);
//
//            if(position == 0){
//                holder.profilePictureOverlay.setVisibility(View.VISIBLE);
//                holder.youOverlay.setVisibility(View.VISIBLE);
//                holder.bottomOverlayView.setVisibility(View.INVISIBLE);
//            } else {
//                holder.profilePictureOverlay.setVisibility(View.INVISIBLE);
//                holder.youOverlay.setVisibility(View.INVISIBLE);
//                holder.bottomOverlayView.setVisibility(View.VISIBLE);
//            }
//
//
////            DemoConfiguration.DemoFeature item = getItem(position);
////            imageLoader.DisplayImage("https://graph.facebook.com/" + "10153200103831432" + "/picture?width=500&height=500"
////                    , holder.profilePicture);
//            Picasso.with(context)
//                    .load("https://graph.facebook.com/544642662390533/picture?width=500&height=500")
//                    .fit()
//                    .centerCrop()
//                    .into(holder.profilePicture);
//
//            return view;
//        }
//    }

//    private static final class ViewHolder {
//        ImageView profilePicture;
//        ImageView profilePictureOverlay;
//        TextView youOverlay;
//        TextView nameOverlay;
//        RelativeLayout bottomOverlayView;
//    }


    //AsyncTask to get profile pic url string from server
    public static class PushUserLocationToDataBase extends AsyncTask<String, Void, String> {

        public PushUserLocationToDataBase(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // SHOW THE BOTTOM PROGRESS BAR (SPINNER) WHILE LOADING MORE PHOTOS
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            // CHANGE THE LOADING MORE STATUS TO PREVENT DUPLICATE CALLS FOR
            // MORE DATA WHILE LOADING A BATCH
            loadingMore = true;


            try {
                java.net.URL url = new URL(serverURL);
                HttpURLConnection LucasHttpURLConnection = (HttpURLConnection)url.openConnection();
                LucasHttpURLConnection.setRequestMethod("POST");
                LucasHttpURLConnection.setDoOutput(true);
                LucasHttpURLConnection.setDoInput(true);
                LucasHttpURLConnection.setConnectTimeout(1000 * 6);
                LucasHttpURLConnection.setReadTimeout(1000 * 6);
                LucasHttpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                LucasHttpURLConnection.setRequestProperty("Accept", "application/json");
                //OutputStream to get response
                OutputStream outputStream = LucasHttpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                JSONObject requestParams   = new JSONObject();
                JSONObject parent = new JSONObject();

//                requestParams.put("lat", locationUpdater.getLatitude());
//                requestParams.put("lng", locationUpdater.getLongitude());
                requestParams.put("lat", 49.8593521);
                requestParams.put("lng", -119.5861359);
                System.out.println("Fragment LONGITUDE: " + locationUpdater.getLongitude());
                System.out.println("Fragment LATITUDE: " + locationUpdater.getLatitude());
//                requestParams.put("lat", 49.888721);
//                requestParams.put("lng", -119.491572);
                requestParams.put("count", current_page);
                requestParams.put("accessToken", "");
                requestParams.put("radiusInMeter", "500000");
                requestParams.put("debug", "androidApp");
                requestParams.put("rangeKey", facebookId);

                parent.put("action", "put-point");
                parent.put("request", requestParams);

                System.out.println(parent.toString());
                bufferedWriter.write(parent.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //InputStream to get response
                InputStream IS = LucasHttpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                StringBuilder response = new StringBuilder();
                String json;
                while( (json = bufferedReader.readLine()) != null){
                    response.append(json + "\n");
                    break;
                }
                bufferedReader.close();
                IS.close();
                LucasHttpURLConnection.disconnect();

                String json_response = response.toString().trim();
                String[] facebook_Ids1 = returnArrayOfFacebookIds(json_response);
                int[] badges = returnArrayOfBadges(json_response);
                int[] close_bye = returnArrayOfCloseBye(json_response);

                /** Set the arrList of UserItems **/
                if(returnArrayOfFacebookIds(json_response) != null && returnArrayOfBadges(json_response) != null){
                    for (int i = 0; i < returnArrayOfFacebookIds(json_response).length; i++) {

                        //Create temporary list of ids
                        ArrayList<String> listOfIds = new ArrayList<String>();
                        for(int c = 0; c < userList.size(); c++){
                            listOfIds.add(userList.get(c).getFacebookId());
                        }

                        //Compare items, check to see what index is duplicate
                        if(!facebook_Ids1[i].equals(facebookId)){
                            /** Make sure its not one of the pinned users **/

                            if(listOfIds.contains(facebook_Ids1[i]) && userList.get(listOfIds.indexOf(facebook_Ids1[i])).isPinned() ){
                                /** user is pinned so don't add to list **/
                            } else if(listOfIds.contains(facebook_Ids1[i])){
                                userList.remove(listOfIds.indexOf(facebook_Ids1[i]));

                                UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i], false);
                                if(close_bye[i] == 1){
                                    userItem.setNearby(true);
                                }

                                userList.add(userItem);
                            } else {
                                UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i], false);
                                if(close_bye[i] == 1){
                                    userItem.setNearby(true);
                                }

                                userList.add(userItem);
                            }

                        }
                    }
                }

                return json_response;
            } catch (MalformedInputException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // HIDE THE BOTTOM PROGRESS BAR (SPINNER) AFTER LOADING MORE ALBUMS
            progressBar.setVisibility(View.GONE);

            //Print server AsyncTask response
            System.out.println("Resulted Value: " + result);

            // SET THE ADAPTER TO THE GRIDVIEW
//            adapter = new EgoStreamAdapter(userList, context, activity, facebookId, typeface);
            adapter.notifyDataSetChanged();
//            gridView.setAdapter(adapter);


            // CHANGE THE LOADING MORE STATUS
            loadingMore = false;


//            If null Response
            if (result != null && !result.equals("")) {
                if(!locationUpdater.mRequestLocationUpdates){
                    locationUpdater.startLocationUpdates();
                    locationUpdater.mRequestLocationUpdates = true;
                } else {

                }
            } else {
                if(!locationUpdater.mRequestLocationUpdates){
                    locationUpdater.mRequestLocationUpdates = true;
                }
            }
        }


    }

    private class loadMorePhotos extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // SHOW THE BOTTOM PROGRESS BAR (SPINNER) WHILE LOADING MORE PHOTOS
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... arg0) {

            // SET LOADING MORE "TRUE"
            loadingMore = true;

            // INCREMENT CURRENT PAGE
            current_page += 1;

            try {
                java.net.URL url = new URL(serverURL);
                HttpURLConnection LucasHttpURLConnection = (HttpURLConnection)url.openConnection();
                LucasHttpURLConnection.setRequestMethod("POST");
                LucasHttpURLConnection.setDoOutput(true);
                LucasHttpURLConnection.setDoInput(true);
                LucasHttpURLConnection.setConnectTimeout(1000 * 6);
                LucasHttpURLConnection.setReadTimeout(1000 * 6);
                LucasHttpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                LucasHttpURLConnection.setRequestProperty("Accept", "application/json");
                //OutputStream to get response
                OutputStream outputStream = LucasHttpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                JSONObject requestParams   = new JSONObject();
                JSONObject parent = new JSONObject();

//                requestParams.put("lat", locationUpdater.getLatitude());
//                requestParams.put("lng", locationUpdater.getLongitude());
                requestParams.put("lat", 49.8593521);
                requestParams.put("lng", -119.5861359);
                System.out.println("Fragment LONGITUDE: " + locationUpdater.getLongitude());
                System.out.println("Fragment LATITUDE: " + locationUpdater.getLatitude());
//                requestParams.put("lat", 49.888721);
//                requestParams.put("lng", -119.491572);
                requestParams.put("count", current_page);
                requestParams.put("accessToken", "");
                requestParams.put("radiusInMeter", "500000");
                requestParams.put("debug", "androidApp");
                requestParams.put("rangeKey", facebookId);

                parent.put("action", "put-point");
                parent.put("request", requestParams);

                System.out.println(parent.toString());
                bufferedWriter.write(parent.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //InputStream to get response
                InputStream IS = LucasHttpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                StringBuilder response = new StringBuilder();
                String json;
                while( (json = bufferedReader.readLine()) != null){
                    response.append(json + "\n");
                    break;
                }
                bufferedReader.close();
                IS.close();
                LucasHttpURLConnection.disconnect();

                String json_response = response.toString().trim();
                String[] facebook_Ids1 = returnArrayOfFacebookIds(json_response);
                int[] badges = returnArrayOfBadges(json_response);
                int[] close_bye = returnArrayOfCloseBye(json_response);


                /** Set the arrList of UserItems **/
                if(returnArrayOfFacebookIds(json_response).length < 1){
                    noMoreUsersToLoad = true;
                } else {
                    if(returnArrayOfFacebookIds(json_response) != null && returnArrayOfBadges(json_response) != null){
                        for (int i = 0; i < returnArrayOfFacebookIds(json_response).length; i++) {

                            //Create temporary list of ids
                            ArrayList<String> listOfIds = new ArrayList<String>();
                            for(int c = 0; c < userList.size(); c++){
                                listOfIds.add(userList.get(c).getFacebookId());
                            }

                            //Compare items, check to see what index is duplicate
                            if(!facebook_Ids1[i].equals(facebookId)){
                                /** Make sure its not one of the pinned users **/

                                if(listOfIds.contains(facebook_Ids1[i]) && userList.get(listOfIds.indexOf(facebook_Ids1[i])).isPinned() ){
                                    /** user is pinned so don't add to list **/
                                } else if(listOfIds.contains(facebook_Ids1[i])){
                                    userList.remove(listOfIds.indexOf(facebook_Ids1[i]));

                                    UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i], false);
                                    if(close_bye[i] == 1){
                                        userItem.setNearby(true);
                                    }

                                    userList.add(userItem);
                                } else {
                                    UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i], false);
                                    if(close_bye[i] == 1){
                                        userItem.setNearby(true);
                                    }

                                    userList.add(userItem);
                                }
                            }

                        }

                    }
                    return response.toString().trim();
                }


            } catch (MalformedInputException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            // HIDE THE BOTTOM PROGRESS BAR (SPINNER) AFTER LOADING MORE ALBUMS
            progressBar.setVisibility(View.GONE);

            //Print server AsyncTask response
            System.out.println("Resulted Value: " + result);

            if(result == null){
                Toast.makeText(getActivity(), "End of the stream", Toast.LENGTH_LONG).show();
            } else {

//            // get gridView current position - used to maintain scroll position
                int currentPosition = gridView.getFirstVisiblePosition();
//
//            // APPEND NEW DATA TO THE ArrayList AND SET THE ADAPTER TO THE gridView
//                adapter = new EgoStreamAdapter(userList, getContext(), friends_Ids, getActivity());
                adapter.notifyDataSetChanged();
//                gridView.setAdapter(adapter);
//
//            // Setting new scroll position
                gridView.setSelection(currentPosition + 1);
//
            }
//             SET loadingMore "FALSE" AFTER ADDING NEW FEEDS TO THE EXISTING LIST
            loadingMore = false;
        }

    }







    //Method to parse json result and get the value of the key "rangeKey"
    private static String[] returnArrayOfFacebookIds(String result){
        JSONObject resultObject = null;
        JSONArray arrayOfUsers = null;
        String[] facebookIds = null;
        try {

            resultObject = new JSONObject(result);
            arrayOfUsers = resultObject.getJSONArray("result");

            facebookIds = new String[arrayOfUsers.length()];
            for(int i = 0; i < arrayOfUsers.length(); ++i){
                JSONObject user = arrayOfUsers.getJSONObject(i);
                String id = user.getString("rangeKey");
                facebookIds[i] = id;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return facebookIds;
    }

    //Method to parse json result and get the value of the key "badge"
    private static int[] returnArrayOfBadges(String result){
        JSONObject resultObject = null;
        JSONArray arrayOfUsers = null;
        int[] badges = null;
        try {

            resultObject = new JSONObject(result);
            arrayOfUsers = resultObject.getJSONArray("result");

            badges = new int[arrayOfUsers.length()];
            for(int i = 0; i < arrayOfUsers.length(); ++i){
                JSONObject user = arrayOfUsers.getJSONObject(i);
                int badge = user.getInt("badge");
                badges[i] = badge;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return badges;
    }

    //Method to parse json result and get the value of the key "close_bye"
    private static int[] returnArrayOfCloseBye(String result){
        JSONObject resultObject = null;
        JSONArray arrayOfUsers = null;
        int[] closeByes = null;
        try {

            resultObject = new JSONObject(result);
            arrayOfUsers = resultObject.getJSONArray("result");

            closeByes = new int[arrayOfUsers.length()];
            for(int i = 0; i < arrayOfUsers.length(); ++i){
                JSONObject user = arrayOfUsers.getJSONObject(i);
                int closeBye = user.getInt("close_bye");
                closeByes[i] = closeBye;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return closeByes;
    }

}
