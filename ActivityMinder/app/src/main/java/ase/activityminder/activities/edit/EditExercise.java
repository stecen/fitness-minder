package ase.activityminder.activities.edit;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import ase.activityminder.serializables.Exercise;
import ase.activityminder.R;
import ase.activityminder.fragments.Duration;
import ase.activityminder.fragments.Repetition;

public class EditExercise extends ActionBarActivity implements Repetition.ToolbarListener, Duration.ToolbarListener
{
    EditText typeText;
    Exercise exercise;
    Switch mySwitch;
    FragmentManager fm;
    int position;
    public static final int EXERCISE_CODE = 42;

    public static int exerciseReps, exerciseTimePerRep, exerciseDuration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editexercise);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        setTitle("Exercise Editor");
        if (bar != null) {
            bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_USE_LOGO);
            bar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
//            bar.setLogo(R.drawable.ic_view_headline_white_48dp);
            //http://www.colorcombos.com/color-schemes/172/ColorCombo172.html CURRENTLY USED
            bar.setElevation(0);
        } else {
            Toast.makeText(this, "Actionbar is null", Toast.LENGTH_SHORT).show();
        }
        typeText = (EditText) findViewById(R.id.exercise_name);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        exercise = (Exercise) extras.getSerializable("EXERCISE");
        typeText.setText(exercise.getName());

        exerciseReps = exercise.getReps();
        exerciseTimePerRep = exercise.getTimePerRep();
        exerciseDuration = exercise.getDuration();

        position = extras.getInt("EXERCISE_POSITION");
        mySwitch = (Switch) findViewById(R.id.switch1);
        fm = getFragmentManager();

        if(exercise.getDuration() > 0) {
            FragmentTransaction ft = fm.beginTransaction();
            Duration dFragment = new Duration();
            ft.add(R.id.listcontainer, dFragment, "duration");
            ft.commit();
            /*fm.executePendingTransactions();
            dFragment.changeText(exercise.getDuration());*/
        }else
        {
            FragmentTransaction ft = fm.beginTransaction();
            Repetition rFragment = new Repetition();
            ft.add(R.id.listcontainer, rFragment,"repetition");
            Log.v("where?","made it past ft.add");
            //Repetition rFragment = (Repetition) fm.findFragmentByTag("repetition");
            if(rFragment == null)
            {
                Log.v("OMGFragment","null");
            }
            ft.commit();
            /*fm.executePendingTransactions();
            rFragment.changeText(exercise.getReps(), exercise.getTimePerRep());*/
        }
        //set the switch to ON
        mySwitch.setChecked(true);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                if (isChecked) {
                    Log.v("Switch", "is checked");
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.listcontainer, new Repetition());
                    ft.commit();

                } else {
                    Log.v("Switch", "is not checked");
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.listcontainer, new Duration());
                    ft.commit();
                }

            }
        });
    }



    private void sendBackToNewWorkout() {
//        int resultCode = 666;
        Intent resultIntent = new Intent(/*null*/);
        resultIntent.putExtra("EDITED_EXERCISE", exercise);
        resultIntent.putExtra("CLICKED_POSITION",position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void setRepetition(int numRep, int numTime) {
        if(typeText.getText().toString() != "" && !typeText.getText().toString().isEmpty())
        {
            exercise.setName(typeText.getText().toString());
        }else
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Please enter a valid title", Toast.LENGTH_SHORT);
            toast.show();
        }
        Log.v("FragmentTest", exercise.getName());
        exercise.setReps(numRep);
        exercise.setTimePerRep(numTime);

        sendBackToNewWorkout();

    }


    @Override
    public void setDuration(int num) {
        if(typeText.getText().toString() != "" && !typeText.getText().toString().isEmpty()) {
            exercise.setName(typeText.getText().toString());
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"Please enter a valid title", Toast.LENGTH_SHORT);
            toast.show();
        }
        Log.v("FragmentTest", exercise.getName());
        exercise.setDuration(num);

        sendBackToNewWorkout();


    }

}
