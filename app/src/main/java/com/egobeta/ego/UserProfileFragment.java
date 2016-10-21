package com.egobeta.ego;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.egobeta.R;
import com.egobeta.ego.TableClasses.User_Badges;
import com.egobeta.ego.TableClasses.User_Profile;
import com.egobeta.ego.demo.DemoFragmentBase;
import com.egobeta.ego.navigation.NavigationDrawer;
import com.squareup.picasso.Picasso;


public class UserProfileFragment extends DemoFragmentBase {

    private static final String ARGUMENT_FACEBOOK_ID = "facebook_id";
    private static final String ARGUMENT_FIRST_NAME = "first_name";
    String facebookId;
    String firstName;
    Typeface typeface;
    User_Profile userProfile;
    User_Badges userBadges;
    DynamoDBMapper mapper;
    boolean badgesFetched = false;
    ActionBar supportActionBar;

    //View item variables
    ImageView profilePicture;
    TextView tvFirstName;
    TextView tvStatus;
    EditText etStatus;

    TextView tvLivesInHeader;
    TextView tvWorksAtHeader;
    TextView tvAboutHeader;
    TextView tvAgeHeader;
    TextView tvLivesIn;
    TextView tvWorksAt;
    TextView tvAbout;
    TextView tvAge;

    RelativeLayout profile_toolbar_section;
    ImageView xBackButton;
    TextView profileName;

    //User profile info variables
    private String status;
    private String lastName;
    String email;
    String snapchat_username;
    String instagram_id;
    String twitter_id;
    String google_plus_id;
    String linkedIn_id;
    String instagram_photos_connected;
    String age;

    //User badge variables
    String location;
    String hometown;
    String birthday;
    String likes_json;
    String workplace_json;
    String school_json;
    String music_json;
    String movies_json;
    String books_json;
    String professionalSkills_json;


    public static UserProfileFragment newInstance(String facebookId, final String firstName) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(UserProfileFragment.ARGUMENT_FACEBOOK_ID, facebookId);
        args.putString(UserProfileFragment.ARGUMENT_FIRST_NAME, firstName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");
        mapper = ((MainActivity) getContext()).mapper;
        profile_toolbar_section = ((MainActivity) getActivity()).profile_toolbar_section;
        xBackButton = ((MainActivity) getActivity()).xBackButton;
        profileName = ((MainActivity) getActivity()).profileName;
        profileName.setTypeface(typeface);

        initializeBackButton(view);


        return view;
    }


    public void initializeBackButton(View view){
        Button button = (Button) view.findViewById(R.id.buttonTest);
        xBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) getActivity()).navigationDrawer.showMenuTogglebutton();

                profile_toolbar_section.setVisibility(View.INVISIBLE);


                //Make the top ego logo visible again
                ((MainActivity) getActivity()).egoLogo.setVisibility(View.VISIBLE);

                //Go back to EgoStream by removing this fragment
                getActivity().getSupportFragmentManager().beginTransaction().remove(UserProfileFragment.this).commit();
            }
        });
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getFragmentArguments();

        initializeViewItems(view);

        new LoadUserProfile().execute();

        setUpActionBar();
    }

    private void getFragmentArguments() {
        final Bundle args = getArguments();
        facebookId = args.getString(ARGUMENT_FACEBOOK_ID);
        System.out.println("PROFILE FRAGMENT: " + facebookId);
        firstName = args.getString(ARGUMENT_FIRST_NAME);

    }

    private void initializeViewItems(View view) {
        profilePicture = (ImageView) view.findViewById(R.id.profilePicture_Background);
        etStatus = (EditText) view.findViewById(R.id.user_status_et);

        tvFirstName = (TextView) view.findViewById(R.id.first_name);
        tvStatus = (TextView) view.findViewById(R.id.user_status_tv);
        tvAboutHeader = (TextView) view.findViewById(R.id.about_header);
        tvAgeHeader = (TextView) view.findViewById(R.id.age_header);
        tvLivesInHeader = (TextView) view.findViewById(R.id.lives_in_header);
        tvAbout = (TextView) view.findViewById(R.id.about_header);
        tvAge = (TextView) view.findViewById(R.id.age);
        tvLivesIn = (TextView) view.findViewById(R.id.lives_in);
        tvWorksAtHeader = (TextView) view.findViewById(R.id.works_at_header);
        tvWorksAt = (TextView) view.findViewById(R.id.works_at);

        etStatus.setTypeface(typeface);
        tvFirstName.setTypeface(typeface);
        tvStatus.setTypeface(typeface);
        tvAboutHeader.setTypeface(typeface);
        tvAgeHeader.setTypeface(typeface);
        tvLivesInHeader.setTypeface(typeface);
        tvAbout.setTypeface(typeface);
        tvAge.setTypeface(typeface);
        tvLivesIn.setTypeface(typeface);
        tvWorksAtHeader.setTypeface(typeface);
        tvWorksAt.setTypeface(typeface);

        Picasso.with(getContext())
                .load("https://graph.facebook.com/" + facebookId + "/picture?width=500&height=500")
                .into(profilePicture);

        tvFirstName.setText(firstName);
    }

    private void setUpActionBar() {
        profileName.setText(firstName);
        ((MainActivity) getActivity()).navigationDrawer.hideMenuTogglebutton();
    }


    public class LoadUserProfile extends AsyncTask<Void, Void, Void> {

        public LoadUserProfile() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            /** Load the user profile that was clicked form the stream **/
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


            if(userProfile != null){
                /** Fetch item variables from loaded User_Profile class **/
                fetchUserProfileVariables();

                new LoadUserBadges().execute();

                /** Update the ui and displayed info from the user's profile info **/
                updateViewItems();
            } else {
                /** Fetch basic user variables if theres no data **/
                fetchDefaultProfileVariables();

                /** Update the ui and displayed info from the user's profile info **/
                updateViewItems();
            }
        }

    }

    public class LoadUserBadges extends AsyncTask<Void, Void, Void> {

        public LoadUserBadges() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            /** Load the user profile that was clicked form the stream **/
            try {
                userBadges = mapper.load(User_Badges.class, facebookId);
            } catch (final AmazonServiceException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            badgesFetched = true;


            if(userBadges != null){
                /** Fetch item variables from loaded User_Profile class **/
                fetchUserBadgeVariables();

                /** Update the ui and displayed info from the user's profile info **/
                updateViewItems();
            } else {
                /** Fetch basic user badge variables if theres no data **/
                fetchDefaultBadgeVariables();

                /** Update the ui and displayed info from the user's profile info **/
                updateViewItems();
            }
        }

    }

    /** Anything on the ui thread that needs changing after sync gets updated here **/
    private void updateViewItems() {

        tvStatus.setText(status);
        tvAge.setText(age);

        if(badgesFetched){
//            tvLivesIn();
//            tvWorksAt.setText(work);
        }


        System.out.println("PROFILE UPDATED: YES");
    }

    /** Fetch item variables from the loaded User_Profile class **/
    private void fetchUserProfileVariables() {
        facebookId = userProfile.getFacebookId();
        status = userProfile.getStatus();
        firstName = userProfile.getFirstName();
        lastName = userProfile.getLastName();
        age = userProfile.getAge();
        email = userProfile.getEmail();
        snapchat_username = userProfile.getSnapchat_username();
        instagram_id = userProfile.getInstagram_id();
        twitter_id = userProfile.getTwitter_id();
        google_plus_id = userProfile.getGoogle_plus_id();
        linkedIn_id = userProfile.getLinkedIn_id();
        instagram_photos_connected = userProfile.getInstagram_photos_connected();
    }

    /** Fetch item variables from the loaded User_Badges class **/
    private void fetchUserBadgeVariables() {
        location = userBadges.getLocation();
        hometown = userBadges.getHometown();
        birthday = userBadges.getBirthday();
        likes_json = userBadges.getLikes_json();
        workplace_json = userBadges.getWorkplace_json();
        school_json = userBadges.getSchool_json();
        music_json = userBadges.getMusic_json();
        movies_json = userBadges.getMovies_json();
        books_json = userBadges.getBooks_json();
        professionalSkills_json = userBadges.getProfessionalSkills_json();

    }

    /** Fetch item variables from the loaded User_Profile class **/
    private void fetchDefaultProfileVariables() {
        status = "Hey everyone! I am new to ego!";
        firstName = "User";
        lastName = "User";
        age = "20";
        email = "SomeEmail";
        snapchat_username = "";
        instagram_id = "";
        twitter_id = "";
        google_plus_id = "";
        linkedIn_id = "";
        instagram_photos_connected = "no";

        userProfile = new User_Profile();
        userProfile.setStatus(status);
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setAge(age);
        userProfile.setEmail(email);
        userProfile.setSnapchat_username(snapchat_username);
        userProfile.setInstagram_photos_connected(instagram_id);
        userProfile.setTwitter_id(twitter_id);
        userProfile.setGoogle_plus_id(google_plus_id);
        userProfile.setLinkedIn_id(linkedIn_id);
        userProfile.setInstagram_photos_connected(instagram_photos_connected);
    }

    /** Fetch item variables from the loaded User_Badges class **/
    private void fetchDefaultBadgeVariables() {
        location = "Default";
        hometown = "Default";
        birthday = "Default";
        likes_json = "Default";
        workplace_json = "Default";
        school_json = "Default";
        music_json = "Default";
        movies_json = "Default";
        books_json = "Default";
        professionalSkills_json = "Default";

        userBadges = new User_Badges();
        userBadges.setLocation(location);
        userBadges.setHometown(hometown);
        userBadges.setBirthday(birthday);
        userBadges.setLikes_json(likes_json);
        userBadges.setWorkplace_json(workplace_json);
        userBadges.setSchool_json(school_json);
        userBadges.setMusic_json(music_json);
        userBadges.setMovies_json(movies_json);
        userBadges.setBooks_json(books_json);
        userBadges.setProfessionalSkills_json(professionalSkills_json);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Toast.makeText(getActivity(), "Fragment Attatched", Toast.LENGTH_SHORT).show();
    }
}
