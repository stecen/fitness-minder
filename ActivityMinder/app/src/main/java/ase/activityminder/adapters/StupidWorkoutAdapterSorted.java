package ase.activityminder.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ase.activityminder.R;
import ase.activityminder.serializables.Workout;

public class StupidWorkoutAdapterSorted extends ArrayAdapter<Workout> {

    List<Workout> workouts;
    ArrayList<Integer> deletePositions;

    Context context;
    Activity mainActivity;

    public StupidWorkoutAdapterSorted(List<Workout> workoutList, Context ctx, ArrayList<Integer> deletePositions, Activity mActivity) {
        super(ctx, R.layout.item_workout, workoutList);
        this.workouts = workoutList;
        this.context = ctx;
        this.deletePositions = deletePositions;
        this.mainActivity = mActivity; // passed in to do mainActivity.startActivityForResult()
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_sorted, parent, false);
        }

        // Now we can fill the layout with the right values
        TextView tv = (TextView) convertView.findViewById(R.id.workout_text);

        Workout p = workouts.get(position);

        Log.e("test", String.format("The position is %d, and the length of workouts is %d", position, workouts.size()));
        tv.setText("\n" + p.getTitle() + "\n");
//        tv.setText("ayo");
        tv.setTextColor(Color.BLACK);

        TextView playsText = (TextView) convertView.findViewById(R.id.plays_text);
        playsText.setText(String.valueOf(p.getNumberOfPlays()) + " plays");

        return convertView;
    }


}
