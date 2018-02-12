package ase.activityminder.search;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Steven on 8/9/2015.
 */
public class ExerciseDatabaseAssetHelper extends SQLiteAssetHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String EXERCISE_DATABASE = "theholydatabase.db";
    // Contacts table name
    private static final String EXERCISES_TABLE = "exercises";
    private static final String VIRTUAL_TABLE = "virtualExercises";
    public static boolean IS_VIRTUAL_CREATED = false;
    Context context;

    // TODO: GET SEARCH RESULTS WITH THIS FTS3 TABLE

    public ExerciseDatabaseAssetHelper(Context context) {
        super(context, EXERCISE_DATABASE, null, DATABASE_VERSION);
        this.context = context;

        createFts3Table(); // create the virtual table for faster search results (full-text-search3)
    }


    //Returns a cursor to all records with a name matching the search query parameter
    // REMEMBER TO SORT, STEVEN
    public Cursor querySearchLimited(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String statement = "SELECT * FROM " + EXERCISES_TABLE + " WHERE " + "name" + " LIKE '%" + query + "%' OR mechanics LIKE '%" +
                query + "%' OR otherMuscles LIKE '%" + query + "%' OR type LIKE '%" + query + "%' OR equipment LIKE '%" +
                query + "%' OR muscle LIKE '%" + query + "%'" + " ORDER BY name ";

        Log.e("notsovirtual", statement);

        // search the entire fts3 virtual table for the query

        Log.e("virtual", "statement = " + statement);
        Cursor qCursor = db.rawQuery(statement, null);
        qCursor.moveToFirst();

        return qCursor;
    }

    // Returns a cursor to all records with any column matching the search query parameter
    public Cursor querySearchAllColumns(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String statement = "SELECT * FROM " + VIRTUAL_TABLE + " WHERE " + VIRTUAL_TABLE + " MATCH '" + query + "*' ORDER BY name";

        // search the entire fts3 virtual table for the query

        Log.e("virtual", "statement = " + statement);
        Cursor qCursor = db.rawQuery(statement, null);
        qCursor.moveToFirst();

        return qCursor;
    }

    // Returns a cursor to ALL records, SORTED by name
    public Cursor getAllExerciseDataAsCursor() {
        // Select All Query
        String selectSortQuery = "SELECT * FROM " + EXERCISES_TABLE + " ORDER BY name";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectSortQuery, null);

        // return contact list
        return cursor;
    }

    public void createFts3Table() {
        SQLiteDatabase db = getWritableDatabase();
        final String CREATE_VIRTUAL_TABLE = "CREATE VIRTUAL TABLE " + VIRTUAL_TABLE +
                " USING fts3(_id, name, rating, type, muscle, otherMuscles, equipment, mechanics, level, guide, url)";

        // Create virtual table //////////////////////////////
        Log.e("virtual", CREATE_VIRTUAL_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + VIRTUAL_TABLE);


//        //debug: drop every time
//        db.execSQL("DROP TABLE " + VIRTUAL_TABLE);

        // create the virtual table if it doesn't exist
        Cursor existCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + VIRTUAL_TABLE + "'", null);
        if (existCursor.getCount() == 0) { // no tables named VIRTUAL_TABLE
            Log.e("virtual", "No tables named this hehe");

            db.execSQL(CREATE_VIRTUAL_TABLE);
            Log.e("virtual", "finished creating virtual table");

            // populate the table (we created it but we haven't necessarily populated it)
            (new PopulateVirtualAsync(db)).execute();
        } else { // virtual table already exists
            Log.e("virtual", "Virtual table already exists");
        }

        existCursor.close();

    }

    private class PopulateVirtualAsync extends AsyncTask<Void, Void, Void> {
        SQLiteDatabase db;
        long startTime; // for debugging how long it takes to populate the virtual table

        public PopulateVirtualAsync(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... callmemaybe) {
            startTime = System.currentTimeMillis();

            // populate FTS3 table with unoptimized exercise database
            Log.e("virtual", "INSERT INTO " + VIRTUAL_TABLE + " SELECT * FROM " + EXERCISES_TABLE);
            db.execSQL("INSERT INTO " + VIRTUAL_TABLE + " SELECT * FROM " + EXERCISES_TABLE);

            return null;
        }

        @Override
        protected void onPostExecute(Void ayylmao) {
            super.onPostExecute(ayylmao);

            IS_VIRTUAL_CREATED = true; // tell other activities trying to search that YAHOO it's created folks

            // get the count from the virtual table for debug

            Toast.makeText(context, "Finished populating virtual table in " + String.valueOf(System.currentTimeMillis() - startTime)
                    + " milliseconds", Toast.LENGTH_LONG).show();

            //debug to see make sure all fields were populated
            Cursor cursor = db.rawQuery("SELECT count(*) from " + EXERCISES_TABLE, null);
            cursor.moveToFirst();
            Log.e("virtual", "Count: " + String.valueOf(cursor.getInt(0)));

            cursor.close(); // free resources
        }

    }
}