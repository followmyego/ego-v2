//package com.egobeta.ego.InstagramClasses;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//
//
//import com.egobeta.R;
//import com.squareup.picasso.Picasso;
//
//
//
//import java.util.ArrayList;
//
///**
// * Created by Lucas on 13/03/2016.
// */
//
//public class ImageViewAdapterInstagram extends BaseAdapter{
//
//    private final Activity activity;
//    private ArrayList<String> arrList;
//    ImageLoader imageLoader;
//
//    public ImageViewAdapterInstagram(Activity context, ArrayList<String> arrList) {
//        this.activity = context;
//        this.arrList = arrList;
//    }
//
//    @Override
//    public int getCount() {
//        return arrList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return arrList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ImageViewHolder holder = null;
//
//        if(convertView == null){
//            LayoutInflater inflater = activity.getLayoutInflater();
//            convertView = inflater.inflate(R.layout.instagram_gridviewitem, null);
//            holder = new ImageViewHolder();
//
//            holder.imageView = (ImageView) convertView.findViewById(R.id.instagramImage);
//
//            convertView.setTag(holder);
//            convertView.setTag(R.id.img, holder.imageView);
//        } else {
//            holder = (ImageViewHolder) convertView.getTag();
//        }
//
//        holder.imageView.setTag(position);
//
//        if(arrList.get(position) != null && holder.imageView != null){
////            imageLoader.DisplayImage(arrList.get(position), holder.imageView);
//            Picasso.with(activity.getApplicationContext()).load(arrList.get(position)).into(holder.imageView);
//        }
//
//        return convertView;
//    }
//
//    public class ImageViewHolder{
//        ImageView imageView;
//    }
//
//
//}