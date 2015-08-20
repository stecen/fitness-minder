package ase.activityminder.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ase.activityminder.R;
import ase.activityminder.fragments.WorkoutList;
import ase.activityminder.serializables.Exercise;
import ase.activityminder.serializables.Workout;

/**
 * Created by Steven on 8/7/2015.
 */
public class FinishedWorkout extends ActionBarActivity {
    Button backButton;
    TextView messageText;

    Workout workout;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_finishedworkout);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        setTitle("Nice work!");
        if (bar != null) {
            bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_USE_LOGO);
            bar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
            bar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
//            bar.setLogo(R.drawable.ic_view_headline_white_48dp);
            //http://www.colorcombos.com/color-schemes/172/ColorCombo172.html CURRENTLY USED
            bar.setElevation(0);
        } else {
            Toast.makeText(this, "Actionbar is null", Toast.LENGTH_SHORT).show();
        }

        backButton = (Button) findViewById(R.id.back_button);
        messageText = (TextView) findViewById(R.id.message_text);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

//        Toast.makeText(getApplicationContext(), extras.getString("STATE_OF_QUIT"), Toast.LENGTH_SHORT).show();
        if (extras.getString("STATE_OF_QUIT").equals("slacker")) {
            messageText.setText("STOP SAYING I WISH.\nSTART SAYING I WILL");
        } else { // someone finished an exercise
            try {
                Button shareButton = (Button) findViewById(R.id.share_button);
                shareButton.setVisibility(View.VISIBLE);
                workout = (Workout) extras.getSerializable("WORKOUT");

                messageText.setText("Congratulations on finishing " + workout.getTitle() + ". I am being cynical... but make sure you don't cheat. I'm kind of dumb -- I have no idea if you actually finished your routine or not. But it's on you. Push yourself -- no one is going to do it for you.");
            } catch (Exception e) {
                Log.e("string", e.toString());

            }
        }

        final Context context = this;



        backButton.setOnClickListener(new View.OnClickListener() { // go back to workout screen
            @Override
            public void onClick(View view) {
//                if (WorkoutList.isActive) {
//                    finish();
//                } else { // create a new intent to the workoutlist
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    finish(); //
//                }

            }
        });
    }

    private String getStringOfCurrentExercise(Exercise curExercise) { // used for texttospeech in speaking what the next exercise is
        String name = curExercise.getName();

        if (curExercise.getCountType() == Exercise.REPS_COUNT) {
            String pluralSuffix = "";
            if (name.toLowerCase().charAt(name.length()-1) != 's') { // if exercise name is current singular
                pluralSuffix = "s"; // make it plural lol
            }
            return String.valueOf(curExercise.getReps()) + " " + name + pluralSuffix;

        } else if (curExercise.getCountType() == Exercise.DURATION_COUNT) {

            // TODO: if greater than a minute, make a string in the format of min/seconds instead of just seconds
            return name + " for " + String.valueOf(curExercise.getDuration()) + " seconds";
        }

        return "I have no idea sorry lol";
    }

    public void share(View v) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        StringBuilder sb = new StringBuilder();
        ArrayList<Exercise> exercises =workout.getExercises();
        for (int i = 0; i < exercises.size(); i++) {
            sb.append(getStringOfCurrentExercise(exercises.get(i))).append("\n");
        }

        String shareTitle = "I just did my exercise routine, " + workout.getTitle().trim() + "!";

        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}
