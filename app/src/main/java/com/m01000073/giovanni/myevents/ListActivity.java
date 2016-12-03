package com.m01000073.giovanni.myevents;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private GoogleApiClient client;

    private DatabaseReference mDatabase;
    private DatabaseReference rDatabase;
    private FirebaseAuth mAuth;
    private Query mQueryDatabase;

    private String user_id;
    String type;

    Typeface myCustomFont;

    long count= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        mUserList = (RecyclerView) findViewById(R.id.user_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        Intent intent=getIntent();
        type = intent.getStringExtra("type");

        if(type.equals("preferiti")){
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("preferiti");
            rDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            mQueryDatabase = mDatabase.orderByChild("name");
            setTitle("Preferiti");

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount()==0){
                        Toast.makeText(getApplicationContext(), "Lista vuota", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("followers");
            mQueryDatabase = mDatabase.orderByChild("name");
            setTitle("Followers");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount()==0){
                        Toast.makeText(getApplicationContext(), "Lista vuota", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();

        FirebaseRecyclerAdapter<Users, ListActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, ListActivity.UsersViewHolder>(
                Users.class,
                R.layout.user_row,
                ListActivity.UsersViewHolder.class,
                mQueryDatabase
        ) {
            @Override
            protected void populateViewHolder(final ListActivity.UsersViewHolder viewHolder, final Users model, int position) {
                    count++;
                    if (type.equals("preferiti")) {
                        viewHolder.setBtn();
                    }

                    if (!model.getImage().equals("default")) {
                        viewHolder.setImage(getApplicationContext(), model.getImage());
                    }

                    viewHolder.setName(model.getName());
                    viewHolder.setId(model.getId());

                    Button btn = (Button) viewHolder.btnDelete;
                    btn.setTypeface(myCustomFont);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewHolder.deleteUser(mDatabase, rDatabase, viewHolder.getId(), user_id);
                        }
                    });

            }
        };
        mUserList.setAdapter(firebaseRecyclerAdapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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

    public static class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;

        Button btnDelete;
        String id;
        String image;
        String name;

        public ListActivity.UsersViewHolder.MyViewHolderClickListener mListener;

        private String TAG = getClass().getSimpleName();


        public static interface MyViewHolderClickListener {
            public void onViewClick(View view, int position);
        }


        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mView.setOnClickListener(this);
            btnDelete = (Button)mView.findViewById(R.id.btnDelete);
        }


        public void setCustomOnClickListener(ListActivity.UsersViewHolder.MyViewHolderClickListener listener) {
            this.mListener = listener;
        }

        @Override
        public void onClick(View view) {
            //
        }

        public void deleteUser(DatabaseReference db, DatabaseReference db1, String id, String userID){
            db.child(id).removeValue();

            db1.child(id).child("followers").child(userID).removeValue();
        }

        public String getId(){
            return this.id;
        }
        public void setId(String id){
            this.id = id;
        }


        public void setBtn(){
            //btnDelete = (Button)mView.findViewById(R.id.btnDelete);
            btnDelete.setVisibility(View.VISIBLE);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.imageUser);
            Picasso.with(ctx).load(image).into(post_image);
        }



        public String getImage() {
            return this.image;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            TextView username = (TextView) mView.findViewById(R.id.username);
            username.setText(name);
            this.name = name;
        }
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
