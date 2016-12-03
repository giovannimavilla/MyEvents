package com.m01000073.giovanni.myevents;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;


public class NotificationActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static NotificationActivity instance;
    private GoogleApiClient client;

    public static Context getContextN() {
        return instance;
    }
    public static String getCurrentUser(){return MainActivity.getCurrentUser();}

    private Query mQueryDatabase;
    private RecyclerView mBlogList;

    private static Typeface myCustomFont;
    private String user_id;


    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        sharedpreferences = getSharedPreferences("MyCity", Context.MODE_PRIVATE);;

        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("notification").child("message");
        mQueryDatabase = mDatabase.orderByChild("PublicTemp");

        /*DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("notification");
        db.child("new message").setValue(0);*/

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
        instance = this;

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("toMain", 1);
        editor.commit();

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.notification_row,
                BlogViewHolder.class,
                mQueryDatabase
        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, int position) {

                viewHolder.setContext(instance);

                viewHolder.setId(model.getId());
                viewHolder.setUser(model.getUser());
                viewHolder.setTag(model.getTag());
                viewHolder.setDate(model.getDate_time());
                viewHolder.setPublic(model.getPublic());

                if(model.getVisualizzato()==1){
                    viewHolder.setNew();
                }



            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }


    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.back_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

}



