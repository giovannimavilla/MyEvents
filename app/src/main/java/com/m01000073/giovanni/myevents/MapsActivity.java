package com.m01000073.giovanni.myevents;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.cast.JoinOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    private Button geoLocationBt;
    private Button btnSave;
    private Spinner spinnercity;
    Double myLatitude = null;
    Double myLongitude = null;
    String myMarker = null;
    String myCity = null;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    protected static final String TAG = "MapsActivity";

    Intent returnIntent;

    String search = null;

    String placesName;

    Typeface myCustomFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        geoLocationBt = (Button) findViewById(R.id.btnSearch);
        geoLocationBt.setTypeface(myCustomFont);
        geoLocationBt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                EditText searchText = (EditText) findViewById(R.id.etLocationEntry);
                spinnercity = (Spinner) findViewById(R.id.spinner);
                myCity = spinnercity.getSelectedItem().toString();
                myMarker = searchText.getText().toString();


                if (myMarker != null && !myMarker.equals("") && !myCity.equals("Seleziona provincia")) {
                    List<android.location.Address> geocodeMatches = null;

                    String indirizzo = searchText.getText().toString();
                    StringTokenizer st = new StringTokenizer(indirizzo, " -,=");
                    search = spinnercity.getSelectedItem().toString();
                    while(st.hasMoreElements()){
                        search = search + "+" + st.nextElement().toString();
                    }


                    LatLng latLng =getLatLng(getLocationFormGoogle(search));


                    myLatitude = latLng.latitude;
                    myLongitude = latLng.longitude;
                    //myMarker = search;

                    returnIntent = new Intent();
                    returnIntent.putExtra("city", myCity);
                    returnIntent.putExtra("marker", spinnercity.getSelectedItem().toString()+", "+indirizzo);
                    returnIntent.putExtra("latitude", myLatitude);
                    returnIntent.putExtra("longitude", myLongitude);

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(myMarker));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                }else if(myMarker.equals("") && myCity.equals("Seleziona provincia")){
                    Toast.makeText(getApplicationContext(), "Selezionare provincia e inserire indirizzo", Toast.LENGTH_LONG).show();
                }
                else if(myCity.equals("Seleziona provincia")){
                    Toast.makeText(getApplicationContext(), "Selezionare provincia", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Inserire Indirizzo", Toast.LENGTH_LONG).show();
                }

            }
        });



        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setTypeface(myCustomFont);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myMarker != null && !myMarker.equals("") && !myCity.equals("Seleziona provincia") ) {

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                }else if(myMarker.equals("") && myCity.equals("Seleziona provincia")){
                    Toast.makeText(getApplicationContext(), "Selezionare provincia e inserire indirizzo", Toast.LENGTH_LONG).show();
                }
                else if(myCity.equals("Seleziona provincia")){
                    Toast.makeText(getApplicationContext(), "Selezionare provincia", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Inserire Indirizzo", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public  JSONObject getLocationFormGoogle(String placesName) {

        JSONObject jsonObject = new JSONObject();
        try {
            HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + placesName + "&ka&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();


            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }


            jsonObject = new JSONObject(stringBuilder.toString());

        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }catch (IllegalArgumentException e){
            Toast.makeText(getApplicationContext(), "Indirizzo errato", Toast.LENGTH_SHORT).show();
        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject;
    }

    public  LatLng getLatLng(JSONObject jsonObject) {

        Double lon = new Double(0);
        Double lat = new Double(0);

        try {

            lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new LatLng(lat,lon);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "this app requires location permssion to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i (TAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i (TAG, "connection fallita");
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()){
            requestLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }


}
