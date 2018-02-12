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

public class Duration extends Fragment {
    ToolbarListener activityCallback;
    Button button;
    EditText hour;
    EditText minute;
    EditText second;
    String activityName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duration, container, false);
        hour = (EditText) view.findViewById(R.id.editText4);
        minute = (EditText) view.findViewById(R.id.editText5);
        second = (EditText) view.findViewById(R.id.editText6);
        Button button = (Button) view.findViewById(R.id.button3);
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

        int length = EditExercise.exerciseDuration;
        if (activityName.equals("activities.edit.EditExercise")) {
            int hours = length / 3600;
            int minutes = (length - hours * 3600) / 60;
            int seconds = (length - hours * 3600) - minutes * 60;
            hour.setText(Integer.toString(hours));
            minute.setText(Integer.toString(minutes));
            second.setText(Integer.toString(seconds));
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
        String hourText = hour.getText().toString();
        String minuteText = minute.getText().toString();
        String secondText = second.getText().toString();
        if (hourText != "" && minuteText != "" && secondText != "" && !hourText.isEmpty() && !minuteText.isEmpty() && !secondText.isEmpty()) {
            int hr = Integer.valueOf(hourText);
            int mn = Integer.valueOf(minuteText);
            int sc = Integer.valueOf(secondText);
            int num = hr * 3600 + mn * 60 + sc;
            activityCallback.setDuration(num);
        } else {
            Toast toast = Toast.makeText(getActivity(), "Please enter valid inputs", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public interface ToolbarListener {
        public void setDuration(int num);
    }


}
