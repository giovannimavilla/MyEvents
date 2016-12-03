package com.m01000073.giovanni.myevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BlogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;

    String id;
    String userID;
    String user;
    String tag;
    String image;
    String date;
    String map;
    String desc;
    Double latitude;
    Double longitude;


    public static Context publicContext;

    private static Context context = MainActivity.getContext();
    private static Context contextN = NotificationActivity.getContextN();



    public MyViewHolderClickListener mListener;

    private String TAG = getClass().getSimpleName();

    public void setLayout() {
        LinearLayout lay = (LinearLayout)mView.findViewById(R.id.layoutLinear);
        lay.setVisibility(View.VISIBLE);
    }


    public static interface MyViewHolderClickListener {
        public void onViewClick(View view, int position);
    }


    public BlogViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mView.setOnClickListener(this);
    }

    public void setCustomOnClickListener(MyViewHolderClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View view) {
        SharedPreferences sharedpreferences =   context.getSharedPreferences("MyCity", Context.MODE_PRIVATE);
        int check = sharedpreferences.getInt("toMain", 0);



        if(check!=0){
            Intent i = new Intent(publicContext, SinglePostActivity.class);
            i.putExtra("idPost", getId());
            i.putExtra("currentUser", NotificationActivity.getCurrentUser());
            i.putExtra("checkNotifica", 1);
            publicContext.startActivity(i);
        }else{

            Intent i = new Intent(publicContext, SinglePostActivity.class);
            i.putExtra("idPost", getId());
            i.putExtra("currentUser", MainActivity.getCurrentUser());
            i.putExtra("checkNotifica", 0);
            publicContext.startActivity(i);
        }


    }

    public void setView(){
        ImageView img = (ImageView)mView.findViewById(R.id.imageView);
        img.setVisibility(View.VISIBLE);
    }

    public void setContext(Context contex){
        publicContext = contex;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setUserID(String userID) {
        this.userID = userID;
        Log.d("userID", userID);
    }

    public String getUserID() {
        return this.userID;
    }


    public void setUser(String user) {
        TextView post_user = (TextView) mView.findViewById(R.id.viewUser);
        post_user.setText(user);
        //post_user.setTypeface(myCustomFont);
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }

    public void setTag(String tag) {
        TextView post_tag = (TextView) mView.findViewById(R.id.viewTag);
        post_tag.setText(tag);
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public void setImage(Context ctx, String image) {
        ImageView post_image = (ImageView) mView.findViewById(R.id.viewImage);
        post_image.setVisibility(View.VISIBLE);
        Picasso.with(ctx).load(image).into(post_image);
    }

    public void setImageNull(Context ctx, String image) {
        ImageView post_image = (ImageView) mView.findViewById(R.id.viewImage);
        post_image.setImageResource(0);
    }

    public String getImage() {
        return this.image;
    }

    public void setDate(String date) {
        TextView post_date = (TextView) mView.findViewById(R.id.viewDate);
        post_date.setText(date);
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public void setMap(String map) {
        TextView post_map = (TextView) mView.findViewById(R.id.viewMap);
        post_map.setText(map);
        this.map = map;
    }

    public String getMap() {
        return this.map;
    }


    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setNew(){
        ImageView img = (ImageView)mView.findViewById(R.id.imageView);
        img.setVisibility(View.VISIBLE);
    }


    public void setPublic(long milliSeconds) {
        TextView text = (TextView)mView.findViewById(R.id.viewPublic);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        text.setText("Pubblicato il: "+formatter.format(calendar.getTime()).toString());

    }



}
