package ase.activityminder.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ase.activityminder.R;
import ase.activityminder.activities.DoWorkout;
import ase.activityminder.activities.MainActivity;
import ase.activityminder.activities.edit.EditWorkout;
import ase.activityminder.serializables.Workout;

public class WorkoutAdapter extends ArrayAdapter<Workout> {

    List<Workout> workouts;
    ArrayList<Integer> deletePositions;

    Context context;
    Activity mainActivity;

    public WorkoutAdapter(List<Workout> workoutList, Context ctx, ArrayList<Integer> deletePositions, Activity mActivity) {
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
            convertView = inflater.inflate(R.layout.row_workoutlist, parent, false);
        }

        // DO THINGS WITH THE VIEWS IN THE ROW
        final LinearLayout workoutLinearRow = (LinearLayout) convertView.findViewById(R.id.workout_linear_row);
        workoutLinearRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onItemClick", "LinearLayout was just clicked.");
                doWorkout(position);
            }
        });

        // Now we can fill the layout with the right values
        TextView tv = (TextView) convertView.findViewById(R.id.workout_text);
        tv.setOnClickListener(new View.OnClickListener() { // PLAY THE WORKOUT
            @Override
            public void onClick(View v) {
                Log.e("onItemClick", "Textview workout item text was just clicked.");
                doWorkout(position);
            }
        });

        CheckBox chBox = (CheckBox) convertView.findViewById(R.id.check_box);
        chBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("checkbox", Integer.toString(position) + " is checked");
                if (isChecked) {
                    Log.v("checkbox", Integer.toString(position) + " is checked");
                    deletePositions.add(position);
                } else {
                    for (int i = 0; i < deletePositions.size(); i++) {
                        if (deletePositions.get(i) == position) {
                            deletePositions.remove(i);
                        }
                    }
                }
            }
        });
        // Edit the workout
        Button editButton = (Button) convertView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "edit button was just clicked");
                Intent intent = new Intent(context, EditWorkout.class);
                intent.putExtra("PLAY_WORKOUT", workouts.get(position));
                intent.putExtra("WORKOUT_POSITION", position);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
                mainActivity.startActivityForResult(intent, EditWorkout.REQ_CODE); // start it from the main activity
            }
        });
//        editButton.setText(String.valueOf(((MainActivity) mainActivity).getWorkouts().get(position).getNumberOfPlays())); //// display the number of plays this workout has

        Workout p = workouts.get(position);

        Log.e("test", String.format("The position is %d, and the length of workouts is %d", position, workouts.size()));
        tv.setText("\n" + p.getTitle() + "\n");
//        tv.setText("ayo");
        tv.setTextColor(Color.BLACK);


        return convertView;
    }

    public ArrayList<Integer> getDeletePositions() {
        return deletePositions;
    }

    private void doWorkout(int position) {
        // DO the workout
        if (workouts.get(position).getExercises().isEmpty()) {
            Toast.makeText(context, "There are no exercises in this workout!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(mainActivity, DoWorkout.class);
            intent.putExtra("PLAY_WORKOUT", workouts.get(position));
            intent.putExtra("WORKOUT_POSITION", position);

            ((MainActivity) mainActivity).workouts.get(position).incrementNumberOfPlays(); // you did this workout one more time
            ((MainActivity) mainActivity).saveWorkoutsFile();
            ((MainActivity) mainActivity).readWorkoutsFile();

//            Toast.makeText(mainActivity, "Increment workouts count to " + String.valueOf(((MainActivity)mainActivity).workouts.get(position).getNumberOfPlays()), Toast.LENGTH_SHORT).show();

            mainActivity.startActivity(intent);
        }
    }


}
