package ase.activityminder.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ase.activityminder.R;
import ase.activityminder.UsernameFilter;
import ase.activityminder.activities.edit.EditExercise;
import ase.activityminder.http.WorkoutsHttpManager;

/**
 * Created by Steven on 8/13/2015.
 */
public class CreateUserActivity extends ActionBarActivity {
    EditText usernameText, passwordText;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewuser);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        setTitle("New User");
        if (bar != null) {
            bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_USE_LOGO);
            bar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
//            bar.setLogo(R.drawable.ic_view_headline_white_48dp);
            //http://www.colorcombos.com/color-schemes/172/ColorCombo172.html CURRENTLY USED
            bar.setElevation(0);
        } else {
            Toast.makeText(this, "Actionbar is null", Toast.LENGTH_SHORT).show();
        }

        usernameText = (EditText) findViewById(R.id.new_username_text);
        passwordText = (EditText) findViewById(R.id.new_password_text);

        usernameText.setFilters(new InputFilter[]{new UsernameFilter()}); // set edittext restriction

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void createUserClicked(View v) {
        vibrator.vibrate(30);
        (new CreateUserAsync()).execute();
    }

    public class CreateUserAsync extends AsyncTask<Void, Void, Void> {
        String username, password;
        Boolean success = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = usernameText.getText().toString();
            password = passwordText.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            success = WorkoutsHttpManager.createUser(username, password);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("success", String.valueOf(success));
//            Toast.makeText(getApplicationContext(), "Success: " + String.valueOf(success), Toast.LENGTH_SHORT).show();

            if (success) {
                Toast.makeText(getApplicationContext(), "Account " + username + " successfully created!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Username already taken...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
