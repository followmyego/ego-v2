package com.egobeta.ego.OnBoarding;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egobeta.R;
import com.egobeta.ego.EgoStream;
import com.egobeta.ego.navigation.NavigationDrawer;

/**
 * Created by Lucas on 06/10/2016.
 */
public class OnBoardingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int position;
    private View view;
    Typeface typeface;
    NavigationDrawer navigationDrawer;
    Fragment egoStream;




    public static OnBoardingFragment newInstance(String param1) {
        OnBoardingFragment fragment = new OnBoardingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, param1);
        fragment.setArguments(bundle);
        return fragment;
    }

    public OnBoardingFragment() {
        // Required empty public constructor
    }

    public void prepareEgoStream(Fragment egoStream){
        this.egoStream = egoStream;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setOnBoardingVisible();



        if (getArguments() != null) {
            String arg = getArguments().getString(ARG_PARAM1);
            assert arg != null;
            position = Integer.parseInt(arg);
        }
        /**Initialize font*/
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");
    }

    private void setOnBoardingVisible() {
        RelativeLayout onBoardingSection = (RelativeLayout) getActivity().findViewById(R.id.onBoardingLayout);
        onBoardingSection.setVisibility(View.VISIBLE);
    }

    private void setOnBoardingGone() {
        RelativeLayout onBoardingSection = (RelativeLayout) getActivity().findViewById(R.id.onBoardingLayout);
        onBoardingSection.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.onboarding_slide, container, false);
        InitializeViewItems();
        return view;
    }

    private void InitializeViewItems() {
        setOnBoardingVisible();

        ImageView onBoardingImage = (ImageView) view.findViewById(R.id.onBoardingImage);
        TextView onBoardingText = (TextView) view.findViewById(R.id.onBoarding_text);
        RelativeLayout onBoardingFullScreen = (RelativeLayout) view.findViewById(R.id.onBoardingFullScreen);
        TextView getStartedText = (TextView) view.findViewById(R.id.text2);

        if(position == 0){
            onBoardingImage.setImageResource(R.drawable.onboarding_photos_1);
            onBoardingText.setText("Ego shows you profiles of the people around you.");
            onBoardingFullScreen.setVisibility(View.GONE);
        } else if(position == 1){
            onBoardingImage.setImageResource(R.drawable.onboarding_photos_2);
            onBoardingText.setText("Badges show you what you have in common.");
            onBoardingFullScreen.setVisibility(View.GONE);
        } else if(position == 2){
            onBoardingFullScreen.setVisibility(View.VISIBLE);
            onBoardingImage.setVisibility(View.GONE);
            onBoardingText.setVisibility(View.GONE);
            getStartedText.setText("Let's get started!");
            getStartedText.setTypeface(typeface);
            finishButton();
        }

        onBoardingText.setTypeface(typeface);
    }

    public void finishButton(){
        ImageButton finishButton = (ImageButton) view.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnBoardingGone();
                navigationDrawer.showHome(egoStream);
            }
        });
    }

    public void setNavigationDrawer(NavigationDrawer navigationDrawer){
        this.navigationDrawer = navigationDrawer;
    }


}
