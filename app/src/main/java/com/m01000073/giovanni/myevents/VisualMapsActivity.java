package com.m01000073.giovanni.myevents;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class VisualMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Double latitude;
    private Double longitude;
    private String marker;
    private String postID;
    private String currentUser;
    private int checkNotifica;

    private Button btnBack;
    Typeface myCustomFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent=getIntent();

        myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        marker = intent.getStringExtra("marker");
        latitude = intent.getDoubleExtra("latitude",0);
        longitude = intent.getDoubleExtra("longitude", 0);

        postID = intent.getStringExtra("idPost");
        currentUser = intent.getStringExtra("currentUser");
        checkNotifica = intent.getIntExtra("checkNotifica", 0);


        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setTypeface(myCustomFont);
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VisualMapsActivity.this, SinglePostActivity.class);
                i.putExtra("idPost", postID);
                i.putExtra("currentUser", currentUser);
                i.putExtra("checkNotifica", 0);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(marker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

    }
}
