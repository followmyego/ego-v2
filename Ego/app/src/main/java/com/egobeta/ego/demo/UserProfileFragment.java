package com.egobeta.ego.demo;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.egobeta.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


public class UserProfileFragment extends DemoFragmentBase {

    private static final String ARGUMENT_FACEBOOK_ID = "facebook_id";
    private static final String ARGUMENT_FIRST_NAME = "first_name";
    String facebookId;
    String firstName;
    Typeface typeface;

    //View item variables
    ImageView profilePicture;
    TextView tvFirstName;
    TextView tvStatus;
    EditText etStatus;

    TextView tvAboutHeader;
    TextView tvAgeHeader;
    TextView tvLivesInHeader;
    TextView tvAbout;
    TextView tvAge;
    TextView tvLivesIn;
    TextView tvWorksAtHeader;
    TextView tvWorksAt;




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
        Button button = (Button) view.findViewById(R.id.buttonTest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(UserProfileFragment.this).commit();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Bundle args = getArguments();
        facebookId = args.getString(ARGUMENT_FACEBOOK_ID);
        System.out.println("PROFILE FRAGMENT: " + facebookId);
        firstName = args.getString(ARGUMENT_FIRST_NAME);
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");

        // Set the title for the instruction fragment.
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }

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


//        final TextView tvOverview = (TextView) view.findViewById(R.id.text_demo_feature_overview);
//        tvOverview.setText(demoFeature.overviewResId);
//        final TextView tvDescription = (TextView) view.findViewById(
//
//                R.id.text_demo_feature_description);
//        if (demoFeature.descriptionResId > 0) {
//            tvDescription.setText(demoFeature.descriptionResId);
//        } else {
//            final TextView tvDescHeading = (TextView) view.findViewById(R.id.text_demo_feature_description_heading);
//            tvDescHeading.setVisibility(View.GONE);
//            tvDescription.setVisibility(View.GONE);
//        }
//        final TextView tvPoweredBy = (TextView) view.findViewById(
//                R.id.text_demo_feature_powered_by);
//        tvPoweredBy.setText(demoFeature.poweredByResId);

//        final ArrayAdapter<DemoConfiguration.DemoItem> adapter = new ArrayAdapter<DemoConfiguration.DemoItem>(
//                getActivity(), R.layout.stream_gridviewitem) {
//            @Override
//            public View getView(final int position, final View convertView,
//                                final ViewGroup parent) {
//                View view = convertView;
//                if (view == null) {
//                    view = getActivity().getLayoutInflater()
//                            .inflate(R.layout.list_item_demo_button_icon_text, parent, false);
//                }
//                final DemoConfiguration.DemoItem item = getItem(position);
//                final ImageView imageView = (ImageView) view.findViewById(R.id.list_item_icon);
//                imageView.setImageResource(item.iconResId);
//                final TextView title = (TextView) view.findViewById(R.id.list_item_title);
//                title.setText(item.buttonTextResId);
//                return view;
//            }
//        };
//        adapter.addAll(demoFeature.demos);
//        final ListView listView = (ListView) view.findViewById(android.R.id.list);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(final AdapterView<?> parent, final View view,
//                                    final int position, final long id) {
//                final DemoConfiguration.DemoItem item = adapter.getItem(position);
//                final AppCompatActivity activity = (AppCompatActivity) getActivity();
//                if (activity != null) {
//                    final Fragment fragment = Fragment.instantiate(getActivity(), item.fragmentClassName);
//                    activity.getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.main_fragment_container, fragment, item.fragmentClassName)
//                        .addToBackStack(null)
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .commit();
//                    activity.getSupportActionBar().setTitle(item.titleResId);
//                }
//            }
//        });
//
//        listView.setBackgroundColor(Color.WHITE);

        final UserSettings userSettings = UserSettings.getInstance(getContext());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                userSettings.loadFromDataset();
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
//                listView.setBackgroundColor(userSettings.getBackgroudColor());
            }
        }.execute();
    }

}
