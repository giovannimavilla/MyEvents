package com.m01000073.giovanni.myevents;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AccountActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    private TextView username;
    private EditText eta;
    private EditText telefono;
    private TextView followers;
    private Button btnSave;
    private Button btnImage;
    private ImageView Image;


    private static final int GALLERY_REQUEST = 1;

    private String user_id;
    private Uri uriImage;

    Typeface myCustomFont;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        username = (TextView) findViewById(R.id.textViewUsername);
        eta = (EditText)findViewById(R.id.editTextEta);
        telefono = (EditText)findViewById(R.id.editTextTele);
        btnSave = (Button) findViewById(R.id.btnSaveAccount);
        btnSave.setTypeface(myCustomFont);
        followers = (TextView)findViewById(R.id.textViewNumFollowers);
        Image = (ImageView)findViewById(R.id.imageProfile);
        btnImage = (Button)findViewById(R.id.btnImage);
        btnImage.setTypeface(myCustomFont);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://key-component-148912.appspot.com");


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("name").getValue().toString());
                eta.setText(dataSnapshot.child("età").getValue().toString());
                telefono.setText(dataSnapshot.child("telefono").getValue().toString());
                followers.setText(String.valueOf(dataSnapshot.child("followers").getChildrenCount()));


                if(dataSnapshot.child("image").getValue().toString().compareTo("default") == 0){
                    Log.d("caricata", "immagine default");
                }else{
                    Log.d("caricata", "immagine da Storage");

                    String url = dataSnapshot.child("image").getValue().toString();

                    Picasso.with(getApplicationContext())
                            .load(url)
                            .into(Image);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("click", "imageButton");
                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child("età").setValue(eta.getText().toString());
                mDatabase.child("telefono").setValue(telefono.getText().toString());


                StorageReference filepath = mStorageRef.child("Profile_image").child(user_id);
                if(uriImage!=null){
                    filepath.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl =  taskSnapshot.getDownloadUrl();
                            mDatabase.child("image").setValue(downloadUrl.toString());
                        }
                    });
                }

                Intent mainIntent = new Intent(AccountActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){

                uriImage = result.getUri();

                Image.setImageURI(uriImage);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
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

