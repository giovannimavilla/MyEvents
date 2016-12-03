package com.m01000073.giovanni.myevents;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog mProgress;
    private static final int SEARCH_REQUEST = 1;

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static String currentUser;
    private static MainActivity instance;

    private String cityCheck = null;

    private GoogleApiClient client;

    public static Context getContext() {
        return instance;
    }

    public static String getCurrentUser(){return currentUser;}

    private Query mQueryDatabase;
    private RecyclerView mBlogList;

    SharedPreferences sharedpreferences;

    private String tagSelected = "tutti";
    private long currentTime;

    private String[] TAGS;
    private long size;

    private long  notification;
    private DatabaseReference message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }else{
                    DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

                    userDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            currentUser = dataSnapshot.child("name").getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        currentTime = calcTime();

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Caricamento ...");
        mProgress.show();

        sharedpreferences = getSharedPreferences("MyCity", Context.MODE_PRIVATE);

        DatabaseReference tagDB = FirebaseDatabase.getInstance().getReference().child("TAGS");
        tagDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                size = dataSnapshot.getChildrenCount();
                int dim = (int)size;
                TAGS = new String[dim];
                for (int i=0; i<dim; i++){
                    TAGS[i]= dataSnapshot.child(String.valueOf(i)).getValue().toString();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mQueryDatabase = mDatabase.orderByChild("temp");

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://key-component-148912.appspot.com");


        if(mAuth.getCurrentUser()!=null) {
            message = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("notification");
            message.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    notification = Long.parseLong(dataSnapshot.child("new message").getValue().toString());
                    if (notification != 0) {
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("notification");
                        db.child("new message").setValue(0);
                        String not;
                        if (notification == 1) {
                            not = " nuovo post è stato pubblicato";
                        } else {
                            not = " nuovi post sono stati pubblicati";
                        }
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Notifiche")
                                .setMessage(String.valueOf(notification) + not)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i= new Intent(MainActivity.this, NotificationActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(R.drawable.ic_notification)
                                .show();
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private long calcTime() {
        return (System.currentTimeMillis()-(60000*180));
    }

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();

        //startService notifiche
        startService(new Intent(getBaseContext(), ForegroundService.class));


        instance = this;
        mAuth.addAuthStateListener(mAuthListener);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("toMain", 0);
        editor.commit();

        if(sharedpreferences.contains("city")){
            cityCheck = sharedpreferences.getString("city", null);
            if (!cityCheck.equals("Seleziona provincia")) {
                mQueryDatabase = mDatabase.orderByChild("city").equalTo(cityCheck);
            } else {
                mQueryDatabase = mDatabase.orderByChild("temp");
            }
        }else{
            cityCheck = "Seleziona provincia";


        }


        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mQueryDatabase
        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, int position) {

                if(model.getTemp()>currentTime) {

                    if (tagSelected.equals("tutti") || model.getTag().equals(tagSelected)) {

                        viewHolder.setLayout();

                        if (model.getImage().equals("null")) {
                            viewHolder.setImageNull(getApplicationContext(), model.getImage());
                        } else {
                            viewHolder.setImage(getApplicationContext(), model.getImage());
                        }

                        viewHolder.setContext(instance);
                        viewHolder.setId(model.getId());
                        viewHolder.setUserID(model.getUserID());
                        viewHolder.setUser(model.getUser());
                        viewHolder.setTag(model.getTag());
                        viewHolder.setDate(model.getDate_time());
                        viewHolder.setMap(model.getMap());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setLatitude(model.getLatitude());
                        viewHolder.setLongitude(model.getLongitude());
                    }
                }

                mProgress.dismiss();
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_newPost) {
            Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
            postIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            postIntent.putExtra("array", TAGS);
            postIntent.putExtra("size", size);
            startActivity(postIntent);

        } else if (id == R.id.nav_search) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            i.putExtra("array", TAGS);
            startActivityForResult(i, SEARCH_REQUEST);

        } else if (id == R.id.nav_preferiti) {
            Intent i = new Intent(MainActivity.this, ListActivity.class);
            i.putExtra("type", "preferiti");
            startActivity(i);

        } else if (id == R.id.nav_followers) {
            Intent i = new Intent(MainActivity.this, ListActivity.class);
            i.putExtra("type", "followers");
            startActivity(i);
        } else if (id == R.id.nav_notification) {
            Intent i = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_account) {
            Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
            accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(accountIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        mAuth.signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEARCH_REQUEST && resultCode == RESULT_OK){
            tagSelected = data.getStringExtra("tag");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_logout);
        SpannableStringBuilder builder = new SpannableStringBuilder("* Logout");
        builder.setSpan(new ImageSpan(this, R.drawable.ic_logout), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(builder);

        MenuItem item1 = menu.findItem(R.id.action_exit);
        SpannableStringBuilder builder1 = new SpannableStringBuilder("* Esci");
        builder1.setSpan(new ImageSpan(this, R.drawable.ic_power_button_off), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        item1.setTitle(builder1);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();
        }

        if (id == R.id.action_exit) {
            finish();
            System.exit(0);
        }
        
        return super.onOptionsItemSelected(item);
    }

}
