package ase.activityminder.fragments;

/**
 * Created by Steven on 8/17/2015.
 */
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
import java.util.Comparator;
import java.util.Locale;

import ase.activityminder.R;
import ase.activityminder.activities.DoWorkout;
import ase.activityminder.activities.MainActivity;
import ase.activityminder.activities.edit.NewWorkout;
import ase.activityminder.adapters.StupidWorkoutAdapterSorted;
import ase.activityminder.adapters.WorkoutAdapter;
import ase.activityminder.http.WorkoutsHttpManager;
import ase.activityminder.serializables.Workout;


public class FrequentList extends Fragment {
    ListView listView;
    Button deleteWorkoutButton;
    public static StupidWorkoutAdapterSorted workoutAdapter;
    private android.support.v4.widget.SwipeRefreshLayout swipeRefresh;


    static boolean isActive = false; // not used

    TextToSpeech tts;

    Vibrator vibrator;

    ArrayList<Workout> sorted;

    //TODO: Swipe refresh layout to download

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sorted, container, false);

        // We get the ListView component from the layout
        listView = (ListView) view.findViewById(R.id.listView);

        sorted = new ArrayList<>();
        sorted = ((MainActivity)getActivity()).workouts;

        Collections.sort(sorted, new CustomComparator());

        workoutAdapter = new StupidWorkoutAdapterSorted(sorted, getActivity(), MainActivity.getDeletePositions(), getActivity());

        // This is a simple adapter that accepts as parameter
        // Context, Data list, The row layout that is used during the row creation, The keys used to retrieve the data, The View id used to show the data. The key number and the view id must match


        listView.setAdapter(workoutAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                Log.e("onItemClick", "fromfragment");
                Intent intent = new Intent(getActivity(), DoWorkout.class);
                intent.putExtra("PLAY_WORKOUT", (sorted.get((int) id)));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.v("long clicked", "pos: " + pos);

                return false;
            }
        });


        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        return view;
    }


    public class CustomComparator implements Comparator<Workout> {
        @Override
        public int compare(Workout o1, Workout o2) {
            return o1.compareTo(o2);
        }
    }








}
