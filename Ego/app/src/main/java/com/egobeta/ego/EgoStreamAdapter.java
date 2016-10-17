package com.egobeta.ego;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egobeta.R;
import com.egobeta.ego.InstagramClasses.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 07/10/2016.
 */
public class EgoStreamAdapter extends BaseAdapter {

    private final Context context;
    private Activity activity;
    private ArrayList<UserItem> userList;
    private static LayoutInflater inflater = null;
    ImageLoader imageLoader;
    String facebookId;
    Typeface typeface;

    ArrayList<Integer> badgeImages = new ArrayList<>();



    public EgoStreamAdapter(ArrayList<UserItem> userList, Context context, Activity activity,
                            String facebookId, Typeface typeface) {
        this.context = context;
        this.activity = activity;
        this.userList = userList;
        this.facebookId = facebookId;
        this.typeface = typeface;
        initializeBadgeImageArrayList();

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    private void initializeBadgeImageArrayList() {
//        badgeImages = new ArrayList<>();
//        badgeImages.add(R.drawable.common_workplace);
//        badgeImages.add(R.drawable.shared_birthday);
//        badgeImages.add(R.drawable.same_school); //this should be swapped out with skills image
//        badgeImages.add(R.drawable.loves_the_same_music);
//        badgeImages.add(R.drawable.loves_the_same_books);
//        badgeImages.add(R.drawable.loves_the_same_movie);
//        badgeImages.add(R.drawable.same_school);
//        badgeImages.add(R.drawable.same_hometown);
//        badgeImages.add(R.drawable.location_icon);
//        badgeImages.add(R.drawable.common_like);

        badgeImages.add(R.drawable.signing_ego_logo);
        badgeImages.add(R.drawable.signing_ego_logo);
        badgeImages.add(R.drawable.signing_ego_logo); //this should be swapped out with skills image
        badgeImages.add(R.drawable.signing_ego_logo);
        badgeImages.add(R.drawable.signing_ego_logo);
        badgeImages.add(R.drawable.signing_ego_logo);
        badgeImages.add(R.drawable.signing_ego_logo);
        badgeImages.add(R.drawable.signing_ego_logo);
        badgeImages.add(R.drawable.signing_ego_logo);
        badgeImages.add(R.drawable.signing_ego_logo);


    }

    public class UserViewHolder {
        ImageView userProfilePic;
        ImageView badge;
        TextView youTextOverlay;
        TextView nameOverlay;
        ImageView youOverlay;
        RelativeLayout bottomOverlayView;
    }


    public void setItems(ArrayList<UserItem> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void addAllItems(List<UserItem> addThisList){
        userList.addAll(addThisList);
        notifyDataSetChanged();
    }

    public void addItem(UserItem item){
        userList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public UserItem getItem(int position) {

        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        UserViewHolder holder = null;

        if(convertView == null) {
            view = inflater.inflate(R.layout.stream_gridviewitem, parent, false);
            holder = new UserViewHolder();

            holder.userProfilePic = (ImageView) view.findViewById(R.id.profilePicture);
            holder.badge = (ImageView) view.findViewById(R.id.badgeView);
            holder.youTextOverlay = (TextView) view.findViewById(R.id.youOverlay);
            holder.youOverlay = (ImageView) view.findViewById(R.id.profilePictureOverlay);
            holder.nameOverlay = (TextView) view.findViewById(R.id.nameOverlay);
            holder.bottomOverlayView = (RelativeLayout) view.findViewById(R.id.bottomOverlayView);


            view.setTag(holder);
            view.setTag(R.id.badgeView, holder.badge);
            view.setTag(R.id.profilePicture, holder.userProfilePic);
            view.setTag(R.id.profilePictureOverlay, holder.youOverlay);
            view.setTag(R.id.youOverlay, holder.youTextOverlay);
            view.setTag(R.id.nameOverlay, holder.nameOverlay);
            view.setTag(R.id.bottomOverlayView, holder.bottomOverlayView);
        } else {
            view = convertView;
            holder = (UserViewHolder) convertView.getTag();
        }

        holder.badge.setTag(position);
        holder.userProfilePic.setTag(position);
        holder.youOverlay.setTag(position);
        holder.youTextOverlay.setTag(position);
        holder.nameOverlay.setTag(position);
        holder.bottomOverlayView.setTag(position);

        UserItem userItem = userList.get(position);

        //Set Typefaces
        holder.nameOverlay.setTypeface(typeface);
        holder.youTextOverlay.setTypeface(typeface);


        //Check what badge we need to display.
        int badge = userItem.getBadge();

        if(facebookId.equals(userItem.getFacebookId())){

            holder.bottomOverlayView.setVisibility(View.INVISIBLE);
            holder.youTextOverlay.setVisibility(View.VISIBLE);
            holder.youOverlay.setVisibility(View.VISIBLE);

            holder.youTextOverlay.setTypeface(typeface);

        } else if(badge == 0){

            holder.bottomOverlayView.setVisibility(View.VISIBLE);
            holder.youTextOverlay.setVisibility(View.INVISIBLE);
            holder.youOverlay.setVisibility(View.INVISIBLE);
            holder.badge.setVisibility(View.INVISIBLE);

            holder.nameOverlay.setText(userItem.getFacebookInfo());

        } else {

            holder.bottomOverlayView.setVisibility(View.VISIBLE);
            holder.userProfilePic.setVisibility(View.VISIBLE);
            holder.badge.setImageResource(badgeImages.get(badge - 1));
            holder.badge.setVisibility(View.VISIBLE);
            holder.youTextOverlay.setVisibility(View.INVISIBLE);
            holder.youOverlay.setVisibility(View.INVISIBLE);

            holder.nameOverlay.setText(userItem.getFirstName());
        }

        if (userItem.getFacebookId() != null){
//            imageLoader.DisplayImage("https://graph.facebook.com/" + userItem.getFacebookId() + "/picture?width=500&height=500"
//                    , holder.userProfilePic);

            Picasso.with(context)
                    .load("https://graph.facebook.com/" + userItem.getFacebookId() + "/picture?width=500&height=500")
                    .fit()
                    .centerCrop()
                    .into(holder.userProfilePic);

        } else {

            holder.userProfilePic.setVisibility(View.INVISIBLE);
            holder.bottomOverlayView.setVisibility(View.INVISIBLE);
            holder.badge.setVisibility(View.INVISIBLE);
            holder.youTextOverlay.setVisibility(View.INVISIBLE);
            holder.youOverlay.setVisibility(View.INVISIBLE);
        }

        return view;
    }



}
