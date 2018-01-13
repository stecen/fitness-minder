package ase.activityminder.activities;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ase.activityminder.R;
import ase.activityminder.activities.edit.EditWorkout;
import ase.activityminder.fragments.FrequentList;
import ase.activityminder.fragments.WorkoutList;
import ase.activityminder.serializables.Workout;


//region pikachu

// quu..__
//  $$$b  `---.__
//   "$$b        `--.                          ___.---uuudP
//    `$$b           `.__.------.__     __.---'      $$$$"              .
//      "$b          -'            `-.-'            $$$"              .'|
//        ".                                       d$"             _.'  |
//          `.   /                              ..."             .'     |
//            `./                           ..::-'            _.'       |
//             /                         .:::-'            .-'         .'
//            :                          ::''\          _.'            |
//           .' .-.             .-.           `.      .'               |
//           : /'$$|           .@"$\           `.   .'              _.-'
//          .'|$u$$|          |$$,$$|           |  <            _.-'
//          | `:$$:'          :$$$$$:           `.  `.       .-'
//          :                  `"--'             |    `-.     \
//         :##.       ==             .###.       `.      `.    `\
//         |##:                      :###:        |        >     >
//         |#'     `..'`..'          `###'        x:      /     /
//          \                                   xXX|     /    ./
//           \                                xXXX'|    /   ./
//           /`-.                                  `.  /   /
//          :    `-  ...........,                   | /  .'
//          |         ``:::::::'       .            |<    `.
//          |             ```          |           x| \ `.:``.
//          |                         .'    /'   xXX|  `:`M`M':.
//          |    |                    ;    /:' xXXX'|  -'MMMMM:'
//          `.  .'                   :    /:'       |-'MMMM.-'
//           |  |                   .'   /'        .'MMM.-'
//           `'`'                   :  ,'          |MMM<
//             |                     `'            |tbap\
//              \                                  :MM.-'
//               \                 |              .''
//                \.               `.            /
//                 /     .:::::::.. :           /
//                |     .:::::::::::`.         /
//                |   .:::------------\       /
//               /   .''               >::'  /
//               `',:                 :    .'
//

//endregion pikachu

public class MainActivity extends ActionBarActivity{

    public static final int REQUEST_CODE = 5;
    //workoutList variablesw
    FragmentManager fm;
    public volatile ArrayList<Workout> workouts = new ArrayList<>();
    public static ArrayList<Integer> deletePositions = new ArrayList<>();
    public static String username;
    ArrayList<String> drawerListItems = new ArrayList<>();

    //navigation drawer things
    ListView drawerListView;
    ArrayAdapter drawerAdapter;

    // Variables dealing with storing files
    final String externalStorage = Environment.getExternalStorageDirectory().getPath();
    final String folderName = "ActivityMinder";
    private String fileName = "guest.bin";
    File folder = new File(externalStorage, folderName);
    File serializeFile; // file to save workouts in

    ConnectivityManager connManager;
    NetworkInfo mWifi;
    NetworkInfo mMobile;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    Context context;

    SharedPreferences settings; // for logging out and deleting stored username from file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        // get the username for fragments to use and file reads // TODO  INTENT GET
        Intent userIntent = getIntent();
        if (userIntent != null) {
            username = userIntent.getStringExtra("USERNAME");
        }

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        setTitle(username + "'s routines");
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


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;


        //network state
        connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        //change the file the user saves to based on the username
        fileName = username.trim() + ".bin"; // create file just for this user
        Log.e("login", "Logged in as " + username + ", using file " + fileName);
//        Toast.makeText(this, "Logged in as " + username + ", using file " + fileName, Toast.LENGTH_SHORT).show();
        serializeFile = new File(externalStorage+"/"+folderName, fileName); // stores the workout list in the specific user's file

        // set shared preferences to be able to log out
        settings = getSharedPreferences("UsernamePassword", Context.MODE_PRIVATE);

        //navigation drawer settings
        drawerListItems.add("All Routines");
        drawerListItems.add("Frequent Routines");
        drawerListItems.add("Built-in Workouts");
        drawerListItems.add("Exercise Database");
        drawerListItems.add("Log Out");

        drawerListView = (ListView) findViewById(R.id.left_drawer);

        drawerAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, drawerListItems){
            @Override
            public Object getItem(int position) {
                return super.getItem(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);

                textView.setTextColor(Color.WHITE);

                return textView;
            }

        };
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());


        //set fragment in main content view
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_frame, new WorkoutList());
        ft.commit();

        readWorkoutsFile(); // TODO: read workouts must be of the username supplied by login
        // download from network is called from the fragment

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            String clickedString = (String) parent.getItemAtPosition(position); // get the strnig that was clicked

            android.support.v4.widget.DrawerLayout mDrawerLayout;
            mDrawerLayout = (android.support.v4.widget.DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawers();

//            Toast.makeText(getApplicationContext(), "Clicked on " + clickedString, Toast.LENGTH_SHORT).show();
            Log.v("clicklistener", "clicked: " + Integer.toString(position) + " " + clickedString);

            if (clickedString.equals("All Routines")) { // display all workouts fragment
                setTitle(username + "'s routines");

                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_frame, new WorkoutList());
                ft.commit();


            } else if (clickedString.equals("Log Out")) {
            // log out by clearing shared preference and going back to log in screen
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", "null");
                editor.apply();

                Intent loginIntent = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(loginIntent);
                finish();
            } else if (clickedString.equals("Exercise Database")) {
                // view exercise database
                Intent intent = new Intent(Intent.ACTION_SEARCH, null, context, QueryDatabaseActivity.class);
                startActivity(intent);
            } else if (clickedString.equals("Frequent Routines")) {
                setTitle("Frequent routines");
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_frame, new FrequentList());
                ft.commit();
            }
        }
    }


    public boolean isConnectedToNetwork() {
        return mWifi.isConnected() || mMobile.isConnected();
    }


    // Displays the local file workouts, and then updates it with the server version if available
    public void readWorkoutsFile() {
        Log.e("test", "readWorkoutsFile");

//        WorkoutList workoutListFrag = (WorkoutList) getFragmentManager().findFragmentById(R.id.content_frame);
//        if (workoutListFrag != null) {
//            workoutListFrag.downloadWorkouts();
//        }

        if (folder.exists()) {
            if (serializeFile.exists()) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializeFile));
                    Object o = ois.readObject(); // ignore this warning rohan it's just there

                    Log.e("user", "Taking workouts from the file");

                    setWorkouts((ArrayList<Workout>) o);

                } catch (Exception ex) {
                    Log.v("Address Book", ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                Log.e("user", "no file found for " + username);
            }
        }


        // update the listview
        WorkoutList workoutListFrag = (WorkoutList) getFragmentManager().findFragmentById(R.id.content_frame);
        if (workoutListFrag != null) {
            workoutListFrag.notifyChanged();
        }

    }

    //uploads to server then saves to own file
    public boolean saveWorkoutsFile() {
        if (!folder.exists()) { // if folder doesn't exist
            folder.mkdirs();
        }

        uploadToNetwork();

        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (new FileOutputStream(serializeFile)); //Select where you wish to save the file...
            oos.writeObject(workouts); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to the file
            oos.close();// close the stream
            return true;
        } catch (Exception e) {
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void uploadToNetwork() {
        WorkoutList workoutListFrag = (WorkoutList) getFragmentManager().findFragmentById(R.id.content_frame);
        if (isConnectedToNetwork()) {
            // upload to network
            workoutListFrag.uploadWorkouts();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_workoutlist, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!WorkoutList.isActive) {
            return true; // you don't want to delete anything when any other fragment is visible
        }
        //Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_delete:
                WorkoutList articleFrag = (WorkoutList) getFragmentManager().findFragmentById(R.id.content_frame);
                if(articleFrag != null) {
                    articleFrag.removeWorkouts();
                } else {
                    Log.v("Fragment","fragment is null");
                }

                return true;
//            case R.id.action_upload: // upload workouts to server
//                Toast.makeText(getApplicationContext(), "u wanna upload?", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


/////////////Finished startActivityForResult() -- you created a workout and it is being sent back here ///////////////////////////////
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(this, String.format("%d %d", requestCode, resultCode), Toast.LENGTH_SHORT).show();
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) { // created new workout
            Workout newWorkout = (Workout) data.getExtras().getSerializable("NEW_WORKOUT");
            workouts.add(newWorkout);
            // UPDATE SERIALIZED FILE
            saveWorkoutsFile();
            WorkoutList articleFrag = (WorkoutList) getFragmentManager().findFragmentById(R.id.content_frame);
            if (articleFrag != null) {
                articleFrag.notifyChanged();
            } else {
                Log.v("Fragment","fragment is null");
            }

        }

        if (requestCode == EditWorkout.REQ_CODE && resultCode == EditWorkout.RES_CODE) {
//            Toast.makeText(this, "Got the result", Toast.LENGTH_SHORT).show();

            /// update the arraylist with the new workout
            workouts.set(data.getExtras().getInt("WORKOUT_POSITION"), (Workout) data.getExtras().getSerializable("PLAY_WORKOUT"));

            saveWorkoutsFile();

            WorkoutList articleFrag = (WorkoutList) getFragmentManager().findFragmentById(R.id.content_frame);
            if(articleFrag != null)
            {
                articleFrag.notifyChanged();
            }else
            {
                Log.v("Fragment","fragment is null");
            }
        }
    }



    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(ArrayList<Workout> workouts) {
        this.workouts = workouts;
    }


    public static ArrayList<Integer> getDeletePositions() {
        return deletePositions;
    }

    public static void setDeletePositions(ArrayList<Integer> deletePositions) {
        MainActivity.deletePositions = deletePositions;
    }
}
