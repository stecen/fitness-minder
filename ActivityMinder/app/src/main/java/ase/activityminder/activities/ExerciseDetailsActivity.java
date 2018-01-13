package ase.activityminder.activities;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ase.activityminder.R;

public class ExerciseDetailsActivity extends ActionBarActivity{
    //    TextView detailsText[] = new TextView[10];
    TextView /*nameText,*/
            ratingText,
            typeText,
            muscleText,
            otherMusclesText,
            equipmentText,
            mechanicsText,
            levelText,
            guideText;

    String videoURL = "";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_exercisedetails);

//        nameText = (TextView) findViewById(R.id.name_text);
        ratingText = (TextView) findViewById(R.id.rating_text);
        typeText = (TextView) findViewById(R.id.type_text);
        muscleText = (TextView) findViewById(R.id.muscle_text);
        otherMusclesText = (TextView) findViewById(R.id.otherMuscles_text);
        equipmentText = (TextView) findViewById(R.id.equipment_text);
        mechanicsText = (TextView) findViewById(R.id.mechanics_text);
        levelText = (TextView) findViewById(R.id.level_text);
        guideText = (TextView) findViewById(R.id.guide_text);

        Intent intent = getIntent(); // intent for the exercise to display details of
        if (intent != null) {
            Bundle exerciseBundle = intent.getExtras();

//            nameText.setText(exerciseBundle.getString("name"));
            ratingText.setText(exerciseBundle.getString("rating"));
            typeText.setText(exerciseBundle.getString("type"));
            muscleText.setText(exerciseBundle.getString("muscle"));
            otherMusclesText.setText(exerciseBundle.getString("otherMuscles"));
            equipmentText.setText(exerciseBundle.getString("equipment"));
            mechanicsText.setText(exerciseBundle.getString("mechanics"));
            levelText.setText(exerciseBundle.getString("level"));
            guideText.setText(exerciseBundle.getString("guide"));
            videoURL = exerciseBundle.getString("url");

            // action bar set up

            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(exerciseBundle.getString("name"));
            actionBar.setDisplayHomeAsUpEnabled(true); // be able to go back
            if (actionBar != null) {
                actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_USE_LOGO);
                actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
//            bar.setLogo(R.drawable.ic_view_headline_white_48dp);
                //http://www.colorcombos.com/color-schemes/172/ColorCombo172.html CURRENTLY USED
                actionBar.setElevation(0);
            } else {
                Toast.makeText(this, "Actionbar is null", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void watchVideo(View v) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("VIDEO_URL", videoURL);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
