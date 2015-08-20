package ase.activityminder.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import ase.activityminder.R;
import ase.activityminder.activities.DoWorkout;
import ase.activityminder.activities.MainActivity;
import ase.activityminder.activities.edit.NewWorkout;
import ase.activityminder.adapters.WorkoutAdapter;
import ase.activityminder.http.WorkoutsHttpManager;
import ase.activityminder.serializables.Workout;


public class WorkoutList extends Fragment {
    ListView listView;
    Button deleteWorkoutButton;
    public static WorkoutAdapter workoutAdapter;
    private android.support.v4.widget.SwipeRefreshLayout swipeRefresh;

    public static boolean isActive = false; // USED

    TextToSpeech tts;

    Vibrator vibrator;

    //TODO: Swipe refresh layout to download

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_workoutlist, container, false);

        // set up swipe
        swipeRefresh = (android.support.v4.widget.SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        // Setup refresh listener which triggers new data loading
        swipeRefresh.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // connect to the network
                downloadWorkouts(); // download workouts from network -- updates listview when finished
            }
        });
        // Configure the refreshing colors
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // We get the ListView component from the layout
        listView = (ListView) view.findViewById(R.id.listView);
        deleteWorkoutButton = (Button) view.findViewById(R.id.delete_workout_button);
        workoutAdapter = new WorkoutAdapter(((MainActivity)getActivity()).workouts, getActivity(), MainActivity.getDeletePositions(), getActivity());

        // This is a simple adapter that accepts as parameter
        // Context, Data list, The row layout that is used during the row creation, The keys used to retrieve the data, The View id used to show the data. The key number and the view id must match


        listView.setAdapter(workoutAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                Log.e("onItemClick", "fromfragment");
                Intent intent = new Intent(getActivity(), DoWorkout.class);
                intent.putExtra("PLAY_WORKOUT", ((MainActivity) getActivity()).workouts.get((int) id));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.v("long clicked", "pos: " + pos);

                return false;
            }
        });

        // Floating plus button
        FloatingActionButton floatingNewButton = (FloatingActionButton) view.findViewById(R.id.floating_new_button);
        floatingNewButton.setType(FloatingActionButton.TYPE_NORMAL);
        //floatingNewButton.attachToListView(listView);
        floatingNewButton.setShadow(true);
        floatingNewButton.show();
        floatingNewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newWorkout(v);
            }
        });

        isActive = true;

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
//                    tts.speak("Initialized.", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });


        isActive = true;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        downloadFromNetwork();
    }


    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }

    public void uploadWorkouts() {
        (new UploadWorkoutsAsync()).execute();
    }

    public void downloadWorkouts() {
        (new DownloadWorkoutsAsync()).execute();
    }


/////////////////////////// NETWORKING /////////////////////////////////////

    public void downloadFromNetwork() { // was originally in mainactivity
        WorkoutList workoutListFrag = (WorkoutList) getFragmentManager().findFragmentById(R.id.content_frame);
        if (((MainActivity)getActivity()).isConnectedToNetwork()) {
            // upload to network
            workoutListFrag.downloadWorkouts();
        }
    }

    /*
      Downloads from server, saves it to the local file, and notifies the adapter of the change in run-time memory workouts
     */
    public class DownloadWorkoutsAsync extends AsyncTask<Void, Void, Void> {
        String user;
        ArrayList<Workout> workoutArrayList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.user = MainActivity.username;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            workoutArrayList = WorkoutsHttpManager.downloadWorkouts(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            StringBuilder sb = new StringBuilder();

            if (workoutArrayList != null) {
                Log.e("size", String.valueOf(workoutArrayList.size()));

                // set arraylist to this downloaded one
                ((MainActivity) getActivity()).workouts = new ArrayList<Workout>(workoutArrayList);

                for (int i = 0; i < ((MainActivity)getActivity()).workouts.size(); i++) {
                    Log.e("append", "appending " +((MainActivity)getActivity()).workouts.get(i).getTitle());
                    sb.append(((MainActivity)getActivity()).workouts.get(i).getTitle()).append("\n");
                }

                Toast.makeText(getActivity(), "Successfully synced routines", Toast.LENGTH_SHORT).show();
                Log.e("download", "success. sb.toString(): " + sb.toString());
                if (sb.toString().trim().isEmpty()) {
                    Log.e("WTF", "SB IS EMPTY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }

                ((MainActivity) getActivity()).saveWorkoutsFile();
                ((MainActivity) getActivity()).readWorkoutsFile();

                notifyChanged();
            }

            swipeRefresh.setRefreshing(false);
        }
    }

    /*
        Uploads workout to server, and that's it
     */
    public class UploadWorkoutsAsync extends AsyncTask<Void, Void, Void> {
        String user;
        final ArrayList<Workout> workoutArrayList = ((MainActivity) getActivity()).workouts; // upload this activity's workout
        boolean success = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.user = MainActivity.username;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            success = WorkoutsHttpManager.uploadWorkouts(MainActivity.username, workoutArrayList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("success", String.valueOf(success));
//            Toast.makeText(getActivity(), "Success: " + String.valueOf(success), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        isActive = false;
        tts.shutdown();
    }

    public void newWorkout(View v) { // you clicked the floating plus button
        vibrator.vibrate(30);
        Intent intent = new Intent(getActivity(), NewWorkout.class);
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE);
    }

    public void notifyChanged() {
        try {
            workoutAdapter.notifyDataSetChanged();
            //whatever
            listView.setAdapter(new WorkoutAdapter(((MainActivity) getActivity()).workouts, getActivity(), MainActivity.getDeletePositions(), getActivity()));
        }catch (Exception e) {
            Log.e("e", e.toString());
        }
    }

    public void removeWorkouts() {
        MainActivity.setDeletePositions(workoutAdapter.getDeletePositions());
        Collections.sort(MainActivity.getDeletePositions());
        Collections.reverse(MainActivity.getDeletePositions());
        for(int i = 0; i < MainActivity.getDeletePositions().size(); i++) {
            int index = MainActivity.getDeletePositions().get(i);
            ((MainActivity) getActivity()).workouts.remove(index);
            workoutAdapter.notifyDataSetChanged();
        }
        MainActivity.getDeletePositions().clear();
        ((MainActivity)getActivity()).saveWorkoutsFile();
        ((MainActivity)getActivity()).readWorkoutsFile();
    }










}
