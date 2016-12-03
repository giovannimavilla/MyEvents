package com.m01000073.giovanni.myevents;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wallet.firstparty.GetBuyFlowInitializationTokenResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class SinglePostActivity extends AppCompatActivity {
    private String postID;
    private String postUserID;
    private String mUser = "ciao";
    private String mTag;
    private String mDesc;
    private String mDate;
    private String mMap;
    private Double mLatitude;
    private Double mLongitude;

    private String currentUser;
    private int checkNotifica;

    private TextView user;
    private TextView tag;
    private TextView date;
    private TextView desc;
    private TextView map;
    private Button btnMap;
    private Button btnSegui;
    private TextView textViewDescrizione;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String user_id;

    private String imgUser;
    private String imgCurrentUser;

    Typeface myCustomFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Caricamento ...");
        mProgress.show();



        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        user = (TextView)findViewById(R.id.textViewUser);
        tag = (TextView)findViewById(R.id.textViewTag);
        date = (TextView)findViewById(R.id.textViewDate);
        desc = (TextView)findViewById(R.id.textViewDesc);
        map = (TextView)findViewById(R.id.textViewMap);

        textViewDescrizione = (TextView)findViewById(R.id.textViewDescrizione);
        textViewDescrizione.setTypeface(myCustomFont);

        Intent intent = getIntent();
        postID = intent.getStringExtra("idPost");
        currentUser = intent.getStringExtra("currentUser");

        checkNotifica = intent.getIntExtra("checkNotifica", 0);


        btnSegui = (Button)findViewById(R.id.btnSegui);

    }



    @Override
    protected void onStart() {
        super.onStart();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog").child(postID);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(checkNotifica==1){

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("notification").child("message").child(dataSnapshot.child("temp").getValue().toString());
                    db.child("visualizzato").setValue(0);
                }

                postUserID = dataSnapshot.child("userID").getValue().toString();

                mMap = dataSnapshot.child("map").getValue().toString();
                mLatitude = Double.valueOf(dataSnapshot.child("latitude").getValue().toString());
                mLongitude = Double.valueOf(dataSnapshot.child("longitude").getValue().toString());



                user.setText(dataSnapshot.child("user").getValue().toString());
                mUser = user.getText().toString();
                tag.setText(dataSnapshot.child("tag").getValue().toString());
                date.setText(dataSnapshot.child("date_time").getValue().toString());
                desc.setText(dataSnapshot.child("desc").getValue().toString());
                map.setText(dataSnapshot.child("map").getValue().toString());

                if (!currentUser.equals(mUser)){

                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("preferiti");

                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(postUserID)){
                                //btnSegui = (Button)findViewById(R.id.btnSegui);
                                btnSegui.setTypeface(myCustomFont);
                                btnSegui.setVisibility(View.VISIBLE);
                                btnSegui.setText(" Aggiunto a preferiti ");
                                Log.d("dentroooo has Child", "true if onDataChange");
                            }else{
                               // btnSegui = (Button)findViewById(R.id.btnSegui);
                                btnSegui.setTypeface(myCustomFont);
                                btnSegui.setVisibility(View.VISIBLE);
                                btnSegui.setEnabled(true);
                                Log.d("dentroooo has Child", "else onDataChange");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");



                DatabaseReference userImage = mDatabase.child(postUserID);
                userImage.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        imgUser = dataSnapshot.child("image").getValue().toString();
                        Log.d("dentro ondatachange", "userimage: "+imgUser);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });




                DatabaseReference userImageCurrent = mDatabase.child(user_id);
                userImageCurrent.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        imgCurrentUser = dataSnapshot.child("image").getValue().toString();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });

                mProgress.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        btnSegui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference thisUser = mDatabase.child(user_id).child("preferiti").child(postUserID);
                thisUser.push();
                thisUser.child("id").setValue(postUserID);
                thisUser.child("name").setValue(mUser);
                thisUser.child("image").setValue(imgUser);



                DatabaseReference postUser = mDatabase.child(postUserID).child("followers").child(user_id);
                postUser.push();
                postUser.child("id").setValue(user_id);
                postUser.child("name").setValue(currentUser);
                postUser.child("image").setValue(imgCurrentUser);

                btnSegui.setEnabled(false);
                btnSegui.setText(" Aggiunto a preferiti ");
            }
        });



        btnMap = (Button)findViewById(R.id.btnMap);
        btnMap.setTypeface(myCustomFont);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SinglePostActivity.this , VisualMapsActivity.class);
                i.putExtra("marker", mMap);
                i.putExtra("latitude",mLatitude );
                i.putExtra("longitude",mLongitude );
                i.putExtra("idPost", postID);
                i.putExtra("currentUser", currentUser);
                i.putExtra("checkNotifica", checkNotifica);
                startActivity(i);
                finish();
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.back_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            if(checkNotifica==1){
                Intent i = new Intent(SinglePostActivity.this, NotificationActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }else{
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
