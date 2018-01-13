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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ase.activityminder.R;
import ase.activityminder.activities.edit.EditExercise;
import ase.activityminder.activities.edit.EditWorkout;
import ase.activityminder.serializables.Exercise;

public class ExerciseAdapter extends ArrayAdapter<Exercise> {

    private List<Exercise> exerciseList;
    private Context context;
    public final static int REQ_CODE_EXERCISE = 666;
    ArrayList<Integer> deletePositions;
    Activity exerciseActivity;

    public ExerciseAdapter(List<Exercise> exerciseList, Context ctx,ArrayList<Integer> deletePositions, Activity eActivity) {
        super(ctx, R.layout.row_exerciselist, exerciseList);
        this.exerciseList = exerciseList;
        this.context = ctx;
        this.deletePositions = deletePositions;
        this.exerciseActivity = eActivity;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_exerciselist, parent, false);
        }
        // Now we can fill the layout with the right values
        TextView tv = (TextView) convertView.findViewById(R.id.workout_text);
        Exercise p = exerciseList.get(position);
        tv.setTextColor(Color.BLACK);
        tv.setText(p.getName());
        CheckBox chBox = (CheckBox) convertView.findViewById(R.id.check_box);
        Button editButton = (Button) convertView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("button", "button was clicked !!!!!!!");
                Intent intent = new Intent(context, EditExercise.class);
                intent.putExtra("EXERCISE", exerciseList.get(position));
                intent.putExtra("EXERCISE_POSITION", position);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);*/
                exerciseActivity.startActivityForResult(intent, EditExercise.EXERCISE_CODE);
            }
        });
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



        return convertView;
    }

    public ArrayList<Integer> getDeletePositions( ) {
        return deletePositions;
    }
}
