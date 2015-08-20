package ase.activityminder.activities;

/**
 * Created by Steven on 8/13/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ase.activityminder.R;
import ase.activityminder.UsernameFilter;
import ase.activityminder.serializables.Exercise;
import ase.activityminder.serializables.Workout;
import ase.activityminder.http.WorkoutsHttpManager;

public class LogInActivity extends Activity {
    Exercise a,b;
    ArrayList<Workout> workouts;

    ImageView bgImage;

    EditText usernameText, passwordText;

    SharedPreferences settings;

    Vibrator vib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        settings = getSharedPreferences("UsernamePassword", Context.MODE_PRIVATE);
        String storedUsername = settings.getString("username", "null");
        if (storedUsername.trim().equals("null")) { // there is no stored username/password
            Log.e("ASDF", "No one is signed in");
        } else { // somebody has stored a username
            // start the activity using the stored username
            Log.e("ASDF", "Already logged in as " + storedUsername);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("USERNAME", storedUsername);
            startActivity(intent);
            finish(); // delete the login screen
        }

        bgImage = (ImageView) findViewById(R.id.background_image); // fill screen ignoring aspect ratio
//        bgImage.setScaleType(ImageView.ScaleType.FIT_XY);

        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        //region workoutsjson
        workouts = new ArrayList<>();

        a = new Exercise();
        a.setName("ExerciseOne");
        a.setDuration(2);
        a.setReps(-1);
        a.setTimePerRep(-1);
        a.setCountType(Exercise.DURATION_COUNT);

        b = new Exercise();
        b.setName("ExerciseTwo");
        b.setDuration(-1);
        b.setReps(5);
        b.setTimePerRep(3);
        b.setCountType(Exercise.REPS_COUNT);

        ArrayList<Exercise> exerciseList1 = new ArrayList<>(); // tobe added to workout list
        exerciseList1.add(a);
        exerciseList1.add(b);

        ArrayList<Exercise> exerciseList2 = new ArrayList<>();
        exerciseList2.add(b); // reverse order
        exerciseList2.add(a);

        Workout w1 = new Workout();
        w1.setExercises(exerciseList1);
        w1.setNumberOfPlays(0);
        w1.setTitle("Workout1");

        Workout w2  = new Workout();
        w2.setExercises(exerciseList2);
        w2.setNumberOfPlays(1);
        w2.setTitle("asdfasdfasdfworkout");

        workouts.add(w1);
        workouts.add(w2);
        //endregion workoutsjson

        usernameText = (EditText) findViewById(R.id.username_text);
        passwordText = (EditText) findViewById(R.id.password_text);

        passwordText.setFilters(new InputFilter[]{new UsernameFilter()});

        usernameText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // darken when on focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    usernameText.setAlpha(.7f);
                    passwordText.setAlpha(.46f);
                } else {
                    usernameText.setAlpha(.46f); //#77
                    passwordText.setAlpha(.7f);
                }
            }
        });

        hideSoftKeyboard(this, usernameText);

    }

    public void logInClicked(View v) {
        // check if password is correct and then fire an intent to MainActivity
        vib.vibrate(30); // tactile feedback
        if (usernameText.getText().toString().trim().equals("guest".trim())) { // log in as guest
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("username", "guest");
            editor.apply();

            // fire an intent to main activity with username
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("USERNAME", "guest");
            startActivity(intent);
            finish(); // delete the login screen
        }
        (new LogInAsync(this)).execute();
    }

    public void createUserClicked(View v) {
        // bring to create new user activity
        Log.e("clicked", "createuser");
        vib.vibrate(30);
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }


    public class LogInAsync extends AsyncTask<Void, Void, Void> {
        String username, password;
        Boolean success = false;
        Context context;

        public LogInAsync(Context c) {
            context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = usernameText.getText().toString();
            password = passwordText.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            success = WorkoutsHttpManager.logIn(username, password); //todo
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("success", String.valueOf(success));
//            Toast.makeText(getApplicationContext(), "Success: " + String.valueOf(success), Toast.LENGTH_SHORT).show();

            if (success) { // if password was correct todo: make this not succeed all the time
                // store the username in the sharedpreferences so that the next time the person opens the app, there will be no need to sign in
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", username);
                editor.apply();

                // fire an intent to main activity with username
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish(); // delete the login screen
            } else { // incorrect
                Toast.makeText(getApplicationContext(), "Username / password combination not recognized...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
