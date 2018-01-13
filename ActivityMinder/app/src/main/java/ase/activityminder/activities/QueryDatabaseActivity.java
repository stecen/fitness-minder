package ase.activityminder.activities;

/**
 * Created by Steven on 8/14/2015.
 */

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import ase.activityminder.R;
import ase.activityminder.search.ExerciseCursorOnClickListener;
import ase.activityminder.search.ExerciseDataCursorAdapter;
import ase.activityminder.search.ExerciseDatabaseAssetHelper;


/*
 Activity that displays search results
 */
public class QueryDatabaseActivity extends ActionBarActivity {
    TextView displayText;
    EditText indexEdit;
    ListView exerciseDataListView;
    ExerciseDataCursorAdapter exerciseDataAdapter;

    SearchView exerciseDataSearch;

    ExerciseDatabaseAssetHelper dbAssetHelper;

    final Context activityContext = this;

    Intent comingIntent; // intent coming from prev activity

    // use these to send result to editexercise/newexercise for when the user selects an execise in this activity,
    // edit exercise / new exercise gets the name of the selected exercise UR WELCOME
    public static final int REQUEST_CODE = 79;
    public static final int RESULT_CODE = 89369; // lol idk random number :^)

    SharedPreferences settings; // determine if the user has already used the app once

    public static String selectedName = "nothing"; // string for previous activity to get selected exercise

    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_database_layout);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        setTitle("Search results");
        if (bar != null) {
            bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_USE_LOGO);
            bar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
//            bar.setLogo(R.drawable.ic_view_headline_white_48dp);
            //http://www.colorcombos.com/color-schemes/172/ColorCombo172.html CURRENTLY USED
            bar.setElevation(0);
        } else {
            Toast.makeText(this, "Actionbar is null", Toast.LENGTH_SHORT).show();
        }

        // display instruction if first time opening app
        settings = getSharedPreferences("FirstTime", Context.MODE_PRIVATE);
        if (settings.getBoolean("FIRST_TIME", true)) { // it is first time
            Toast.makeText(getApplicationContext(), "Press the triangle to select the exercise, or click on the exercise to view more details", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FIRST_TIME", false); // no longer first time
            editor.apply();
        }

        // initialize search related things
        exerciseDataSearch = (SearchView) findViewById(R.id.exercise_data_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        exerciseDataSearch.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        exerciseDataSearch.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//        exerciseDataSearch.setSubmitButtonEnabled(true); // submit button

        dbAssetHelper = new ExerciseDatabaseAssetHelper(this);

        dbAssetHelper = new ExerciseDatabaseAssetHelper(this);

        exerciseDataListView = (ListView) findViewById(R.id.entire_exercise_data_list);
        Cursor queryCursor = dbAssetHelper.getAllExerciseDataAsCursor();
        queryCursor.moveToFirst();
        exerciseDataAdapter = new ExerciseDataCursorAdapter(this, queryCursor, 0); // initialize adapter withcursor
        exerciseDataListView.setAdapter(exerciseDataAdapter); // display the cursor info with adapter

        // when the user clicks on an exercise, display its contents
        exerciseDataListView.setOnItemClickListener(new ExerciseCursorOnClickListener(this));

        // get intent from New Exercise / Edit Exercise to query
        comingIntent = getIntent();
        if (comingIntent != null) {
            if (comingIntent.getAction().equals(Intent.ACTION_SEARCH)) {
                String query =  comingIntent.getStringExtra(SearchManager.QUERY); // does virtual table stuff

                if (query == null) {
                    setTitle("Browse Database");
                }

                search(query);

                // set the search bar to the searched text and hide keyboard
                exerciseDataSearch.setQuery(query, false);
                exerciseDataSearch.clearFocus();
                hideSoftKeyboard(this, exerciseDataListView);
            }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    // Received a search query (this is the same activity)
    public void onNewIntent(Intent intent) {
        // Get the intent, verify the action and get the query
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            dbAssetHelper = new ExerciseDatabaseAssetHelper(this);

            search(query);
        }

    }

    private void search(String query) {
        Toast.makeText(getApplicationContext(), "Received search query for " + query, Toast.LENGTH_SHORT).show();

        Cursor queryCursor = dbAssetHelper.querySearchLimited(query);
        Log.e("virtual", "queryCursor count = " + String.valueOf(queryCursor.getCount()));

        if (queryCursor.getCount() <= 0) { // no search results
            Toast.makeText(getApplicationContext(), "No search results found for " + query, Toast.LENGTH_LONG).show();
        }

        exerciseDataAdapter = new ExerciseDataCursorAdapter(this, queryCursor, 0); // initialize adapter with cursor
        try {
            exerciseDataListView.setAdapter(exerciseDataAdapter); // display the cursor info with adapter
        } catch(NullPointerException e) {
            Log.e("virtual", e.toString());
        }

        // when the user clicks on an exercise, display its contents
        exerciseDataListView.setOnItemClickListener(new ExerciseCursorOnClickListener(activityContext));
    }


    // send result to editexercise/newexercise for when the user selects an execise in this activity,
    // edit exercise / new exercise gets the name of the selected exercise UR WELCOME
    public void returnExerciseName(String name) {
//        Toast.makeText(getApplicationContext(), "Selected " + name + " from database", Toast.LENGTH_SHORT).show();
//
//        Intent nameIntent = new Intent();
//        nameIntent.putExtra("EXERCISE_NAME", name);
//        setResult(RESULT_CODE, nameIntent);
//        finish();

        vibrator.vibrate(30);
        QueryDatabaseActivity.selectedName = name; // let the previous activity access the selected exercise
        finish(); // go back to NewExercise/EditExercise

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }


    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

}
