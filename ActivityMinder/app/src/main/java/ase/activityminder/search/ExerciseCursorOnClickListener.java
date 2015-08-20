package ase.activityminder.search;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import ase.activityminder.activities.ExerciseDetailsActivity;

/**
 * Created by Steven on 8/12/2015.
 */
public class ExerciseCursorOnClickListener implements AdapterView.OnItemClickListener {
    Context activityContext;

    public ExerciseCursorOnClickListener (Context ctx) {
        activityContext = ctx;
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        Log.e("intent", "intent for position " + String.valueOf(position));

        Cursor cursorData = (Cursor) adapter.getItemAtPosition(position); // get information from the database
//        cursorData.moveToFirst();

        Intent intent = new Intent(activityContext, ExerciseDetailsActivity.class);
        for (int i = 0; i < ExerciseData.HASH_KEYS.length; i++) {
            intent.putExtra(ExerciseData.HASH_KEYS[i], cursorData.getString(i + 1)); // skip _id so do column i+1
        }

//                cursorData.close();

        activityContext.startActivity(intent);

    }
}
