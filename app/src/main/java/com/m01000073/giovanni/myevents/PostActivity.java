package com.m01000073.giovanni.myevents;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private static final int MAPS_REQUEST = 2;
    private Uri imageUri = null;
    long tempDATA = 0;
    long tempTIME = 0;
    Double latitude = null;
    Double longitude = null;
    String city = null;

    private String[] TAGS;
    private long size;


    private String user_id;
    private String name_val;
    private String id;

    private ImageButton btnImage;
    private TextView textViewTag;
    private AutoCompleteTextView mPostTag;
    private Button btnDate;
    private TextView mPostDate;
    private Button btnTime;
    private TextView mPostTime;
    private Button btnMapsActivity;
    private TextView textViewDescrizione;
    private EditText mPostDesc;
    private TextView textViewMarker;


    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference newPost;
    private DatabaseReference notiPref;
    private DatabaseReference sendToPref;
    private long    sizePref;
    private ArrayList<String> arrayPref;
    private  String element;

    private long numberMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        newPost = mDatabase.child("Blog");

        arrayPref = new ArrayList<String>();

        notiPref = mDatabase.child("Users").child(user_id).child("followers");

        if(notiPref!=null) {
            notiPref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    sizePref = dataSnapshot.getChildrenCount();
                    Log.d("getChildrencount", String.valueOf(sizePref));
                    if (sizePref != 0) {
                        element = dataSnapshot.getValue().toString();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



        Intent intent = getIntent();
        TAGS = intent.getStringArrayExtra("array");
        size = intent.getLongExtra("size",0);


        DatabaseReference userName = mDatabase.child("Users").child(user_id);
        userName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                name_val = dataSnapshot.child("name").getValue().toString();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        textViewTag = (TextView)findViewById(R.id.textViewTag);
        textViewTag.setTypeface(myCustomFont);
        btnImage = (ImageButton)findViewById(R.id.btnImage);
        mPostTag = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextTag);
        btnDate = (Button) findViewById(R.id.btnDate);
        btnDate.setTypeface(myCustomFont);
        mPostDate = (TextView) findViewById(R.id.textViewDate);
        btnTime = (Button) findViewById(R.id.btnTime);
        btnTime.setTypeface(myCustomFont);
        mPostTime = (TextView)findViewById(R.id.textViewOra);
        btnMapsActivity = (Button)findViewById(R.id.btnMap);
        btnMapsActivity.setTypeface(myCustomFont);

        textViewDescrizione = (TextView)findViewById(R.id.textViewDescrizione);
        textViewDescrizione.setTypeface(myCustomFont);

        mPostDesc = (EditText)findViewById(R.id.editTextDesc);

        textViewMarker = (TextView)findViewById(R.id.textViewMarker);

        //imagePost
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });



        //date
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final java.util.Calendar c = java.util.Calendar.getInstance();
                int mYear = c.get(java.util.Calendar.YEAR); // current year
                int mMonth = c.get(java.util.Calendar.MONTH); // current month
                int mDay = c.get(java.util.Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(PostActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text

                                java.util.GregorianCalendar gregorianCalendar1  = new java.util.GregorianCalendar(year, monthOfYear, dayOfMonth);

                                Date data1 = gregorianCalendar1.getTime();
                                tempDATA=data1.getTime();

                                int dayOfWeek = gregorianCalendar1.get(Calendar.DAY_OF_WEEK);
                                String[] giorni = {"Dom","Lun","Mar","Mer","Gio","Ven","Sab"};

                                mPostDate.setText(giorni[dayOfWeek-1] + ", " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //ore
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final java.util.Calendar c = java.util.Calendar.getInstance();
                int mHour = c.get(java.util.Calendar.HOUR_OF_DAY); // current year
                int mMinute = c.get(java.util.Calendar.MINUTE); // current monthy
                TimePickerDialog timePickerDialog = new TimePickerDialog(PostActivity.this, 2,  new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // set day of month , month and year value in the edit text
                        if (minute<10){
                            mPostTime.setText(hourOfDay +":0" + minute );
                        }else{
                            mPostTime.setText(hourOfDay +":" + minute );
                        }
                        tempTIME=(hourOfDay*60*60*1000)+(minute*60*1000);

                    }
                }, mHour,mMinute,true);
                timePickerDialog.show();
            }
        });

        //maps
        btnMapsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapsIntent = new Intent(PostActivity.this , MapsActivity.class);
                startActivityForResult(mapsIntent,MAPS_REQUEST);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //autocomplete TAG
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TAGS);
        mPostTag.setAdapter(adapter);

        if(sizePref!=0){

            StringTokenizer st = new StringTokenizer(element, ",=");
            int i = 0;
            int j=0;
            while(i<sizePref){

                while(j<4) {
                    st.nextElement();
                    j++;
                }
                j=-2;
                arrayPref.add(st.nextElement().toString());
                i++;
            }

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            btnImage.setImageURI(imageUri);
            Log.d("imageUri", String.valueOf(imageUri));
        }

        if (requestCode== MAPS_REQUEST && resultCode == RESULT_OK){
            String marker = data.getStringExtra("marker");
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            city = data.getStringExtra("city");

            textViewMarker.setText(marker);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.post_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {

            int result = startPosting();
            if(result==1){
                sendNotifica();
            }


            if (result == 1){
                startActivity(new Intent(PostActivity.this, MainActivity.class));
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void sendNotifica() {
        final String tag_val = mPostTag.getText().toString().trim();
        final String date_val = mPostDate.getText().toString().trim();
        final String time_val = mPostTime.getText().toString().trim();
        int indice=0;

        sendToPref = mDatabase.child("Users");
        for(int index=0; index< arrayPref.size(); index++){
            sendToPref.child(arrayPref.get(index).toString()).child("notification").child("message").child(String.valueOf(tempDATA+tempTIME)).child("date_time").setValue(date_val + "  Ore: " + time_val);
            sendToPref.child(arrayPref.get(index).toString()).child("notification").child("message").child(String.valueOf(tempDATA+tempTIME)).child("tag").setValue(tag_val);
            sendToPref.child(arrayPref.get(index).toString()).child("notification").child("message").child(String.valueOf(tempDATA+tempTIME)).child("user").setValue(name_val);
            sendToPref.child(arrayPref.get(index).toString()).child("notification").child("message").child(String.valueOf(tempDATA+tempTIME)).child("id").setValue(id);

            sendToPref.child(arrayPref.get(index).toString()).child("notification").child("message").child(String.valueOf(tempDATA+tempTIME)).child("visualizzato").setValue(1);
            int Max = 148023839*1000*1000;
            sendToPref.child(arrayPref.get(index).toString()).child("notification").child("message").child(String.valueOf(tempDATA+tempTIME)).child("Public").setValue(System.currentTimeMillis());
            sendToPref.child(arrayPref.get(index).toString()).child("notification").child("message").child(String.valueOf(tempDATA+tempTIME)).child("PublicTemp").setValue(Max-System.currentTimeMillis());

            indice=index;
            DatabaseReference db =  mDatabase.child("Users").child(arrayPref.get(index).toString()).child("notification");
            final int finalIndice = index;
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    numberMsg = Long.parseLong((dataSnapshot.child("new message").getValue().toString()));
                    sendToPref.child(arrayPref.get(finalIndice).toString()).child("notification").child("new message").setValue(numberMsg+1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private int startPosting() {

        final String tag_val = mPostTag.getText().toString().trim();
        final String date_val = mPostDate.getText().toString().trim();
        final String time_val = mPostTime.getText().toString().trim();
        final String marker_val = btnMapsActivity.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();


        if (!TextUtils.isEmpty(tag_val) && !TextUtils.isEmpty(date_val) && !TextUtils.isEmpty(time_val) && latitude!=null && longitude!=null && !TextUtils.isEmpty(marker_val)) {
            int casuale = (int)(Math.random()*tempDATA);
            Log.d("casuale", String.valueOf(casuale));
            id = String.valueOf(tempDATA+tempTIME)+user_id+String.valueOf(casuale);

            newPost.child(id).push();

            newPost = newPost.child(id);
            if(imageUri!=null){

                StorageReference riversRef = mStorage.child("Blog_Image/"+imageUri.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(imageUri);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        newPost.child("id").setValue(id);
                        newPost.child("userID").setValue(user_id);
                        newPost.child("user").setValue(name_val);
                        newPost.child("image").setValue(downloadUrl.toString());
                        newPost.child("tag").setValue(tag_val);
                        newPost.child("date_time").setValue(date_val + "  Ore: " + time_val);
                        newPost.child("temp").setValue(tempDATA+tempTIME);
                        newPost.child("map").setValue(marker_val);
                        newPost.child("city").setValue(city);
                        newPost.child("latitude").setValue(latitude);
                        newPost.child("longitude").setValue(longitude);

                        if (TextUtils.isEmpty(desc_val)){
                            newPost.child("desc").setValue("Nessuna descrizione");
                        }else{
                            newPost.child("desc").setValue(desc_val);
                        }
                    }
                });

            }else{
                newPost.child("id").setValue(id);
                newPost.child("userID").setValue(user_id);
                newPost.child("user").setValue(name_val);
                newPost.child("image").setValue("null");
                newPost.child("tag").setValue(tag_val);
                newPost.child("date_time").setValue(date_val + "  Ore: " + time_val);
                newPost.child("temp").setValue(tempDATA+tempTIME);
                newPost.child("map").setValue(marker_val);
                newPost.child("city").setValue(city);
                newPost.child("latitude").setValue(latitude);
                newPost.child("longitude").setValue(longitude);

                if (TextUtils.isEmpty(desc_val)){
                    newPost.child("desc").setValue("Nessuna descrizione");
                }else{
                    newPost.child("desc").setValue(desc_val);
                }

            }

            if (!Arrays.asList(TAGS).contains(tag_val)){
                DatabaseReference tagRef = mDatabase.child("TAGS");
                tagRef.child(String.valueOf(size)).setValue(tag_val);
            }

            return 1;


        } else {
            Toast.makeText(getApplicationContext(), "Campi obbligatori : TAG, DATA, ORA, INDIRIZZO", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

}
