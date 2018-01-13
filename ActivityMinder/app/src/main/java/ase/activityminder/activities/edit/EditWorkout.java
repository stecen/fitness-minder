package ase.activityminder.activities.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import ase.activityminder.serializables.Exercise;
import ase.activityminder.adapters.ExerciseAdapter;
import ase.activityminder.R;
import ase.activityminder.serializables.Workout;

public class EditWorkout extends ActionBarActivity {

    Workout currentWorkout;
    EditText workoutTitle;
    int position;
    ListView lv;
    ExerciseAdapter exerciseAdpt;
    public final static int REQ_CODE_EXERCISE = 666;
    ArrayList<Integer> deletePositions = new ArrayList();


    public final static int REQ_CODE = 301;
    public final static int RES_CODE = 103;
    FloatingActionButton floatingNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editworkout);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        setTitle("Workout Editor");
        if (bar != null) {
            bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_USE_LOGO);
            bar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
//            bar.setLogo(R.drawable.ic_view_headline_white_48dp);
            //http://www.colorcombos.com/color-schemes/172/ColorCombo172.html CURRENTLY USED
            bar.setElevation(0);
        } else {
            Toast.makeText(this, "Actionbar is null", Toast.LENGTH_SHORT).show();
        }
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        currentWorkout = (Workout) extras.getSerializable("PLAY_WORKOUT");
        position = (int) extras.getInt("WORKOUT_POSITION");
        workoutTitle = (EditText) findViewById(R.id.editText);
        workoutTitle.setText(currentWorkout.getTitle());
        lv = (ListView) findViewById(R.id.listView);
        exerciseAdpt = new ExerciseAdapter(currentWorkout.getExercises(), getApplicationContext(),deletePositions,this);
        lv.setAdapter(exerciseAdpt);

        // Floating plus button
        floatingNewButton = (FloatingActionButton) findViewById(R.id.floating_new_button_editworkout);
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
                removeExercises();
                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void returnUpdatedWorkout() {
        Log.e("test", "I am already sending back");
        currentWorkout.setTitle(workoutTitle.getText().toString());

        Intent resultIntent = new Intent();
        resultIntent.putExtra("PLAY_WORKOUT", currentWorkout);
        resultIntent.putExtra("WORKOUT_POSITION", position);
        setResult(RES_CODE, resultIntent);
        finish();

//        Intent intent = new Intent(getApplicationContext(),WorkoutList.class);
//        intent.putExtra("PLAY_WORKOUT",currentWorkout);
//        intent.putExtra("WORKOUT_POSITION",position);
//        startActivity(intent);

    }

    public void createNewExercise(View view) {
        Intent intent = new Intent(this, NewExercise.class);
        startActivityForResult(intent, REQ_CODE_EXERCISE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_EXERCISE && resultCode == RESULT_OK) {
            Exercise newExercise = (Exercise) data.getExtras().getSerializable("NEW_EXERCISE");

            currentWorkout.getExercises().add(newExercise);

            exerciseAdpt.notifyDataSetChanged();
        }
        else if(requestCode == EditExercise.EXERCISE_CODE && resultCode == RESULT_OK)
        {
            Exercise editedExercise = (Exercise) data.getExtras().get("EDITED_EXERCISE");
            int position = data.getIntExtra("CLICKED_POSITION",0);
            currentWorkout.getExercises().set(position,editedExercise);
        }
    }

    public void removeExercises()
    {
        exerciseAdpt.getDeletePositions();
        for(int i = 0;i<deletePositions.size();i++) {
            int index = deletePositions.get(i);
            currentWorkout.getExercises().remove(index);
            exerciseAdpt.notifyDataSetChanged();
        }
        deletePositions.clear();
    }

    @Override
    public void onBackPressed() { // make sure that the user actually wants to quit

        new AlertDialog.Builder(this) // needs to be activity context (this)
                .setTitle("Save?")
                .setMessage("Do you want to Save and Quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        returnUpdatedWorkout();
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


}
