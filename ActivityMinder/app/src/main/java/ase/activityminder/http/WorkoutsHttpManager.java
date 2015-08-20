package ase.activityminder.http;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ase.activityminder.serializables.Exercise;
import ase.activityminder.serializables.Workout;

/**
 * Created by Steven on 8/13/2015.
 */

public class WorkoutsHttpManager {
    // TODO: make a static variable for IP
    public static final String SOCKET_ADDRESS = "http://bmw.cs.pdx.edu:9999";
//    public static final String SOCKET_ADDRESS = "http://10.200.85.187:9999";
//    public static final String SOCKET_ADDRESS = "http://192.168.1.182:9999";
    private static final Gson gson = new Gson();

    public static ArrayList<Workout> downloadWorkouts(String username) {
        BufferedReader reader = null;
        String uri = SOCKET_ADDRESS + "/downloadWorkouts";// append to url with the correct command to server
        ArrayList<Workout> retVal = null;

        Log.e("workouts", "Downloading workouts");

        try {
            HashMap<String, String> postParameters = new HashMap<>();
            postParameters.put("username", username);

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // .connect() is implicitly called
            con.setConnectTimeout(15000);
            con.setDoOutput(true);
            con.setDoInput(true);

            Log.e("userdownload", username);

            // POST
            int responseCode = postToServer(con, postParameters);

            Log.e("response", String.valueOf(responseCode));

            // READ FROM SERVER
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    Log.e("line", line);
                    sb.append(line).append("\n");
                }

                String json = sb.toString();

                retVal = new ArrayList<Workout>(convertJsonToWorkoutList(json)); // return array list

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.toString());
        } finally {
            if (reader != null) {
                try {
                    Log.e("Closing reader", "Success");
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IOException", e.toString());
                }
            }

        }

        return retVal;
    }

    public static boolean uploadWorkouts(String username, ArrayList<Workout> workouts) {
        BufferedReader reader = null;
        String uri = SOCKET_ADDRESS + "/uploadWorkouts";// append to url with the correct command to server
        Boolean yesOrNo = false;

        Log.e("workouts", "Uploading workouts");

        // convert the workouts list to a json array string
        String workoutsJson = gson.toJson(workouts);
        Log.e("JSONupload", workoutsJson);

        try {
            HashMap<String, String> postParameters = new HashMap<>();
            postParameters.put("username", username);
            postParameters.put("workouts", workoutsJson);

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // .connect() is implicitly called
            con.setConnectTimeout(15000);
            con.setDoOutput(true);
            con.setDoInput(true);

            Log.e("userupload", username);

            // POST
            int responseCode = postToServer(con, postParameters);

            Log.e("response", String.valueOf(responseCode));

            // READ FROM SERVER
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    Log.e("line", line);
                    sb.append(line).append("\n");
                }

                String response = sb.toString();

                // return true if the server successfully registered your upload
                Log.e("response", response);
                yesOrNo = response.trim().equals("Y".trim());
                Log.e("yesOrNo", String.valueOf(yesOrNo));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.toString());
            return false;
        } finally {
            if (reader != null) {
                try {
                    Log.e("Closing reader", "Success");
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IOException", e.toString());
                }
            }

        }

        return yesOrNo;
    }

    public static boolean logIn(String username, String password) {
        BufferedReader reader = null;
        String uri = SOCKET_ADDRESS + "/signIn";// append to url with the correct command to server
        Boolean yesOrNo = false;

        try {
            HashMap<String, String> postParameters = new HashMap<String, String>();
            postParameters.put("username", username);
            postParameters.put("password", password);

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // .connect() is implicitly called
            con.setConnectTimeout(15000);
            con.setDoOutput(true);
            con.setDoInput(true);

            Log.e("userpass", username + ", " + password);

            // POST
            int responseCode = postToServer(con, postParameters);

            Log.e("response", String.valueOf(responseCode));

            // READ FROM SERVER
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    Log.e("line", line);
                    sb.append(line).append("\n");
                }

                String response = sb.toString();

                // return true if the account was successfully made
                // return false if there is already a user with that account
                Log.e("response", response);
                yesOrNo = response.trim().equals("Y".trim());
                Log.e("yesOrNo", String.valueOf(yesOrNo));
//                return yesOrNo;
            }

//            return yesOrNo;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.toString());
            return false;
        } finally {
            if (reader != null) {
                try {
                    Log.e("Closing reader", "Success");
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IOException", e.toString());
                }
            }

        }

        return yesOrNo;
    }

    public static boolean createUser(String username, String password) {
        BufferedReader reader = null;
        String uri = SOCKET_ADDRESS + "/createUser";// append to url with the correct command to server
        Boolean yesOrNo = false;

        try {
            HashMap<String, String> postParameters = new HashMap<String, String>();
            postParameters.put("username", username);
            postParameters.put("password", password);

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // .connect() is implicitly called
            con.setConnectTimeout(15000);
            con.setDoOutput(true);
            con.setDoInput(true);

            Log.e("userpass", username +", "+password);

            // POST
            int responseCode = postToServer(con, postParameters);

            Log.e("response", String.valueOf(responseCode));

            // READ FROM SERVER
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    Log.e("line", line);
                    sb.append(line).append("\n");
                }

                String response = sb.toString();

                // return true if the account was successfully made
                // return false if there is already a user with that account
                Log.e("response", response);
                yesOrNo = response.trim().equals("Y".trim());
                Log.e("yesOrNo", String.valueOf(yesOrNo));
//                return yesOrNo;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.toString());
//            return false;
        } finally {
            if (reader != null) {
                try {
                    Log.e("Closing reader", "Success");
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IOException", e.toString());
                }
            }
//            return yesOrNo;
        }

        return yesOrNo;
    }

    // UTILITY FUNCTIONS
    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) { // why the .entrySet();?
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static int postToServer(HttpURLConnection con, HashMap<String, String> postParameters) {
        try {
            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postParameters));
            writer.flush();
            writer.close();
            os.close();
            return con.getResponseCode();
        } catch (Exception e) {
            Log.e("Exception postToServer", e.toString());
        }

        return 666;
    }

    public static ArrayList<Workout> convertJsonToWorkoutList(String json) {
        ArrayList<Workout> workoutsList = new ArrayList<>();

        try {
            JSONArray workoutsListJson = new JSONArray(json); // json array of all workouts
            // DONT CONFUSE workoutsJson with workoutJson

            for (int i = 0; i < workoutsListJson.length(); i++) { // goes through all workouts
                JSONObject workoutJson = workoutsListJson.getJSONObject(i); // a single workout
                JSONArray exercisesJson = workoutJson.getJSONArray("exercises"); // json array of exercises of this workout

                // java objects of the same thing
                Workout workout = new Workout();
                ArrayList<Exercise> exercises = new ArrayList<>();

                for (int j = 0; j < exercisesJson.length(); j++) { // goes through all exercises of that workout
                    JSONObject curJsonExercise = exercisesJson.getJSONObject(j);
                    Exercise ex = new Exercise();

                    // we could have just made a constructor for exercise... facepalm
                    ex.setName(curJsonExercise.getString("type"));
                    ex.setDuration(curJsonExercise.getInt("duration"));
                    ex.setReps(curJsonExercise.getInt("reps")); // no ID for now even though its in the json
                    ex.setTimePerRep(curJsonExercise.getInt("time"));
                    ex.setCountType(curJsonExercise.getInt("countType"));

                    // add packaged exercise
                    exercises.add(ex);
                }

                // fill out info about the workout itself
                workout.setTitle(workoutJson.getString("title"));
                workout.setNumberOfPlays(workoutJson.getInt("numberOfPlays"));
                workout.setExercises(exercises); // set workout exercise list

                // add workout to list of workouts
                workoutsList.add(workout);
            }

        } catch (JSONException e) {
            Log.e("jsonlol", e.toString());
        }

        return workoutsList;
    }
}