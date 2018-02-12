package ase.activityminder.activities.edit;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.commons.lang3.text.WordUtils;

import ase.activityminder.R;
import ase.activityminder.activities.QueryDatabaseActivity;
import ase.activityminder.fragments.Duration;
import ase.activityminder.fragments.Repetition;
import ase.activityminder.serializables.Exercise;

public class NewExercise extends ActionBarActivity implements Repetition.ToolbarListener, Duration.ToolbarListener {
    //    EditText typeText;
    Exercise exercise;
    Switch mySwitch;
    FragmentManager fm;
    SearchView exerciseDataSearch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newexercise);


        //style the action bar
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        setTitle("New Exercise");
        if (bar != null) {
            bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_USE_LOGO);
            bar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
//            bar.setLogo(R.drawable.ic_view_headline_white_48dp);
            //http://www.colorcombos.com/color-schemes/172/ColorCombo172.html CURRENTLY USED
            bar.setElevation(0);
        } else {
            Toast.makeText(this, "Actionbar is null", Toast.LENGTH_SHORT).show();
        }

        // search bar initialization
        exerciseDataSearch = (SearchView) findViewById(R.id.exercise_data_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        exerciseDataSearch.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        exerciseDataSearch.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//        exerciseDataSearch.setSubmitButtonEnabled(true); // submit button

//        typeText = (EditText) findViewById(R.id.exercise_name);
        exercise = new Exercise();
        mySwitch = (Switch) findViewById(R.id.switch1);
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.listcontainer, new Repetition());
        ft.commit();

        //set the switch to ON
        mySwitch.setChecked(true);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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

    /*
        onResume could either be called when the user presses back button, first uses activity, or after user selected an exercise in QueryDatabaseActivity.
        If it is from after QueryDatabaseActivity, then get the name of the exercise selected and put it in the searchview for display.
        startActivityForResult() can't be used because I haven't found a way to manipulate the intent sent by SearchViews so I used this sketchy workaround
     */
    @Override
    public void onResume() {
        super.onResume();

        if (QueryDatabaseActivity.selectedName.equals("nothing")) {
            // the user pressed the back button or something
            Log.e("user", "pressed back button LLOOLOLOLOLOO");
        } else {
            exerciseDataSearch.setQuery(QueryDatabaseActivity.selectedName, false);
            QueryDatabaseActivity.selectedName = "nothing"; // reassign
        }
    }


//    //chose an exercise thing from search query from QueryDatabaseActivity
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent nameIntent) {
//        if (requestCode == QueryDatabaseActivity.REQUEST_CODE && resultCode == QueryDatabaseActivity.RESULT_CODE) {
//            String name = nameIntent.getStringExtra("EXERCISE_NAME");
//            exerciseDataSearch.setQuery(name, false);
//        }
//    }

    private void sendBackToNewWorkout() {
//        int resultCode = 666;
        Intent resultIntent = new Intent(/*null*/);
        resultIntent.putExtra("NEW_EXERCISE", exercise);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    //TODO: port everything here to editexercise

    @Override
    public void setRepetition(int numRep, int numTime) {
        String nameText = exerciseDataSearch.getQuery().toString(); // get the name from the search view
        nameText = WordUtils.capitalize(nameText);

        if (nameText.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid title", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            exercise.setName(nameText);
        }

        Log.v("FragmentTest", exercise.getName());
        exercise.setReps(numRep);
        exercise.setTimePerRep(numTime);
        exercise.setCountType(Exercise.REPS_COUNT);

        sendBackToNewWorkout();

    }


    @Override
    public void setDuration(int num) {
        String nameText = exerciseDataSearch.getQuery().toString(); // get the name from the search view
        nameText = WordUtils.capitalize(nameText); // capitalize first letter of every word

        if (nameText.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid title", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            exercise.setName(nameText);
        }

        Log.v("FragmentTest", exercise.getName());
        exercise.setDuration(num);
        exercise.setCountType(Exercise.DURATION_COUNT);

        sendBackToNewWorkout();
    }

}
