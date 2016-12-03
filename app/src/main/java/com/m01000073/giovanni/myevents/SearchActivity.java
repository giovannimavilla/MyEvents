package com.m01000073.giovanni.myevents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import static android.app.PendingIntent.getActivities;
import static android.app.PendingIntent.getActivity;

public class SearchActivity extends AppCompatActivity {

    Intent returnIntent;
    Button ok;

    Spinner spinnerTag;
    String tagCheck= null;

    Spinner spinner;
    TextView city;

    String cityCheck;

    SharedPreferences sharedpreferences;

    private String [] TAGS;
    Typeface myCustomFont;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        spinner = (Spinner)findViewById(R.id.spinner);

        spinnerTag = (Spinner) findViewById(R.id.spinnerTag);

        Intent intent = getIntent();
        TAGS = intent.getStringArrayExtra("array");

        sharedpreferences = getSharedPreferences("MyCity", Context.MODE_PRIVATE);
        cityCheck = sharedpreferences.getString("city", null);
        city = (TextView)findViewById(R.id.textViewCity);

        if(cityCheck==null){
            city.setText("Provincia Selezionata :" + "Seleziona provincia");
        }else {
            city.setText("Provincia impostata: " + cityCheck);
        }


        Resources res = getResources();
        String[] array = res.getStringArray(R.array.cityarray);
        spinner.setSelection(sharedpreferences.getInt("spinnerSelection",0));
        String selectedString = array[spinner.getSelectedItemPosition()];





    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
        checkBox.setTypeface(myCustomFont);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TAGS);
        spinnerTag.setAdapter(adapter);

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if ( ((CheckBox)v).isChecked() ) {
                    spinnerTag.setVisibility(View.VISIBLE);
                    tagCheck = "";
                }else{
                    spinnerTag.setVisibility(View.INVISIBLE);
                    tagCheck = null;
                }
            }
        });






        ok = (Button)findViewById(R.id.button2);
        ok.setTypeface(myCustomFont);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cityCheck = spinner.getSelectedItem().toString();

                int selectedPosition = spinner.getSelectedItemPosition();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("spinnerSelection", selectedPosition);
                editor.putString("city", cityCheck);
                editor.commit();


                //Log.d("returnInternt", cityCheck);
                //returnIntent.putExtra("cityCheck", cityCheck);

                returnIntent = new Intent();

                if(tagCheck!=null){
                    tagCheck = spinnerTag.getSelectedItem().toString();
                    returnIntent.putExtra("tag", tagCheck);
                }else{
                    returnIntent.putExtra("tag", "tutti");
                }

                setResult(Activity.RESULT_OK, returnIntent);
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
            finish();

        }

        return super.onOptionsItemSelected(item);
    }
}
