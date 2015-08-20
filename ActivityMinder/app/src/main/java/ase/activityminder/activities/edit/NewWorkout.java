package ase.activityminder.activities.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import ase.activityminder.activities.FinishedWorkout;
import ase.activityminder.fragments.WorkoutList;
import ase.activityminder.serializables.Exercise;
import ase.activityminder.adapters.ExerciseAdapter;
import ase.activityminder.R;
import ase.activityminder.serializables.Workout;

public class NewWorkout extends ActionBarActivity {
    ExerciseAdapter exerciseAdpt;
    EditText title;
    Button  titleEnter;

    Workout newWorkout;

    ArrayList<Integer> deletePositions = new ArrayList<>();
    public final static int REQ_CODE_EXERCISE = 666;
    FloatingActionButton floatingNewButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newworkout);
        //createExercises();
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        setTitle("New Routine");
        if (bar != null) {
            bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_USE_LOGO);
            bar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
//            bar.setLogo(R.drawable.ic_view_headline_white_48dp);
            //http://www.colorcombos.com/color-schemes/172/ColorCombo172.html CURRENTLY USED
            bar.setElevation(0);
        } else {
            Toast.makeText(this, "Actionbar is null", Toast.LENGTH_SHORT).show();
        }
        newWorkout = new Workout();
        title = (EditText) findViewById(R.id.editText);
//        titleEnter = (Button) findViewById(R.id.button); // bye bye buttonfree

        ListView lv = (ListView) findViewById(R.id.listView);
        exerciseAdpt = new ExerciseAdapter(newWorkout.getExercises(), getApplicationContext(), deletePositions, this);
        lv.setAdapter(exerciseAdpt);

        // Floating plus button
        floatingNewButton = (FloatingActionButton) findViewById(R.id.floating_new_button_newworkout);
        floatingNewButton.setType(FloatingActionButton.TYPE_NORMAL);
        //floatingNewButton.attachToListView(lv);
        floatingNewButton.setShadow(true);
        floatingNewButton.show();
        floatingNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewExercise(v);
            }
        });
    }


    @Override
    public void onBackPressed() { // make sure that the user actually wants to quit

        new AlertDialog.Builder(this) // needs to be activity context (this)
                .setTitle("Save?")
                .setMessage("Do you want to Save and Quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendBackToMainActivity();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setNeutralButton("just quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
//                .setIcon(R.drawable.fitness)
                .show();

    }






    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_workoutlist, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteExercises();
                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    public void createNewExercise(View view)
    {

        Intent intent = new Intent(this, NewExercise.class);
        startActivityForResult(intent, REQ_CODE_EXERCISE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_EXERCISE && resultCode == RESULT_OK) {
            Exercise newExercise = (Exercise) data.getExtras().getSerializable("NEW_EXERCISE");
            Toast.makeText(getApplicationContext(), newExercise.getName(), Toast.LENGTH_SHORT).show();
            newWorkout.getExercises().add(newExercise);
            exerciseAdpt.notifyDataSetChanged();
        }
    }
    public void sendBackToMainActivity() {
        newWorkout.setTitle(title.getText().toString());
        Intent resultIntent = new Intent(/*null*/);
        resultIntent.putExtra("NEW_WORKOUT", newWorkout);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void deleteExercises()
    {
        exerciseAdpt.getDeletePositions();
        for(int i = 0;i<deletePositions.size();i++) {
            int index = deletePositions.get(i);
            newWorkout.getExercises().remove(index);
            exerciseAdpt.notifyDataSetChanged();
        }
        deletePositions.clear();
    }
}
