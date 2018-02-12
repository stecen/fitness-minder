package ase.activityminder.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ase.activityminder.R;
import ase.activityminder.activities.edit.EditExercise;

public class Repetition extends Fragment {
    public EditText numRep;
    public EditText numTime;
    ToolbarListener activityCallback;
    String activityName;
    View view = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_repetition, container, false);
        numRep = (EditText) view.findViewById(R.id.editText3);
        numTime = (EditText) view.findViewById(R.id.editText7);

        Button button = (Button) view.findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonClicked(v);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activityName.equals("activities.edit.EditExercise")) {
            numRep.setText(String.valueOf(EditExercise.exerciseReps));
            numTime.setText(String.valueOf(EditExercise.exerciseTimePerRep));
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityName = activity.getLocalClassName();
        try {
            activityCallback = (ToolbarListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ToolbarListener");
        }
    }

    public void buttonClicked(View v) {
        String repetitionNum = numRep.getText().toString();
        String timeNum = numTime.getText().toString();
        boolean validFields = false;
        if (repetitionNum != "" && timeNum != "" && !repetitionNum.isEmpty() && !timeNum.isEmpty()) {
            validFields = true;
        }
        if (validFields) {
            activityCallback.setRepetition(Integer.valueOf(repetitionNum), Integer.valueOf(timeNum));
        } else {
            Toast toast = Toast.makeText(getActivity(), "Please enter valid inputs", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void changeText(int reps, int durationReps) {
        numRep.setText(Integer.toString(reps));
        numTime.setText(Integer.toString(durationReps));
    }

    public interface ToolbarListener {
        public void setRepetition(int numRep, int numTime);
    }
}
