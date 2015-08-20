package ase.activityminder.search;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ase.activityminder.R;
import ase.activityminder.activities.QueryDatabaseActivity;

/**
 * Created by Steven on 8/8/2015.
 */
public class ExerciseDataCursorAdapter extends CursorAdapter {
    ImageView returnButton;
    Context context;
    QueryDatabaseActivity qActivity;

    public ExerciseDataCursorAdapter(QueryDatabaseActivity activity, Cursor cursor, int flags) {
        super(activity, cursor, 0);
        this.qActivity = activity;
    }

    // Don't call getView(); that's ideal for ArrayAdapters

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_exercisedata, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        final TextView exerciseTitleText = (TextView) view.findViewById(R.id.aexercise_title_text);
        TextView ratingText = (TextView) view.findViewById(R.id.arating_text);
        TextView levelText = (TextView) view.findViewById(R.id.alevel_text);
        TextView muscleText = (TextView) view.findViewById(R.id.muscles_text);
        TextView typeText = (TextView) view.findViewById(R.id.atype_text);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String rating = cursor.getString(cursor.getColumnIndexOrThrow("rating"));
        String level = cursor.getString(cursor.getColumnIndexOrThrow("level"));
        String muscle = cursor.getString(cursor.getColumnIndexOrThrow("mechanics")); //oops
        String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));

        // Populate fields with extracted properties of the exercises
        exerciseTitleText.setText(name);
        ratingText.setText(rating);
        levelText.setText(level);
        muscleText.setText(muscle);
        typeText.setText(type);

        returnButton = (ImageView) view.findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onClick", "clicked from imageview");
                qActivity.returnExerciseName(exerciseTitleText.getText().toString());
            }
        });

    }
}
