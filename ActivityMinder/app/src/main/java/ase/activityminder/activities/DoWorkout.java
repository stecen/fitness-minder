package ase.activityminder.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import ase.activityminder.AutoResizeTextView;
import ase.activityminder.R;
import ase.activityminder.serializables.Exercise;
import ase.activityminder.serializables.Workout;


public class DoWorkout extends Activity {

    // Workout to be played
    Workout currentWorkout;
    ArrayList<Long> exerciseDurations = new ArrayList<>(); // durations are in milliseconds in this array aight?
    ArrayList<Long> afterThisDuration = new ArrayList<>(); // duration of all exercises after this (in milliseconds)
    ArrayList<Boolean> hasSpokenFive = new ArrayList<>();

    //Exercise to be played
    int exerciseIndex = 0; // play the first exercise first

    //duration of entire workout
    long workoutDuration = 0;

    // timer-related objects
    AutoResizeTextView stopwatchText;
    TextView totalTimeText, indexText, exerciseTitleText;
    TextView tenthText;
    CountDownTimer workoutCountDownTimer;
    Button startPauseButton/*, resetButton*/;

    long COUNTDOWN_TIME = 15000;
    final int TIME_EPS = 10; // minimum time threshold for voice alerts
    final int SPEAKING_DELAY = 500;

    final static int RUNNING = 1, PAUSED = 0; // for the start/resume button - since they are the same multipurpose button. These are constants for keeping track of the state
    int runOrPause = PAUSED;
    long timeWhenResumes = COUNTDOWN_TIME; // this is the time that the countdowntimer should set to -- used to set the time for an exercise and also for pausing/resuming, hence the name
                                            // there is no direct way to pause a CountDownTimer -- the best way to do this is to cancel the countdowntimer, record the time, and then when
                                            // the user wants to resume, create a new CountDownTimer with the previously recorded time and start it from there (giving the sufficient illusion of pause/resume)
    long nextTimeLandmark; // time landmark to be less than to update the notification (decrease seconds in the notification)

    // Text to speech stuff
    TextToSpeech tts;
    boolean isTTSInitialized = false; // TTS initializes on a separate thread, so this handler makes sure that it's intialized before the app tries to use it to speak

    boolean isBegin = true; // is starting from the beginning of the workout (variable also to be used when resetting the clock)
    boolean isFinished = false; // finished with workout

    private final int NOTIFICATION_CODE = 0;

    final Context activityContext = this;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doworkout);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        currentWorkout = (Workout) extras.getSerializable("PLAY_WORKOUT");
        position = extras.getInt("WORKOUT_POSITION");
//        Toast.makeText(getApplicationContext(), "Doing " + currentWorkout.getTitle() + ". Good luck!", Toast.LENGTH_SHORT).show();

        // Handlers
        exerciseTitleText = (TextView) findViewById(R.id.exercise_title_text);
        stopwatchText = (AutoResizeTextView) findViewById(R.id.stopwatch_text);
        startPauseButton = (Button) findViewById(R.id.start_button);
        tenthText = (TextView) findViewById(R.id.tenth_text);
//        resetButton = (Button) findViewById(R.id.reset_button);
        //debug
        totalTimeText = (TextView) findViewById(R.id.totaltime_text);
        indexText = (TextView) findViewById(R.id.index_text);

        // use custom font CODE_BOLD
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/saxmono.ttf");
//        stopwatchText.setTypeface(typeface); // set custom font
        stopwatchText.resizeText(500, 500); // AUTO RESIZE TEXT

        initArrayLists();

//        //debug
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < exerciseDurations.size(); i++) sb.append(String.format("%d ", afterThisDuration.get(i)));
//        Toast.makeText(this, String.valueOf(workoutDuration) + ": " + sb.toString(), Toast.LENGTH_SHORT).show();

        // initialize countdown timer / set text for the first exercise
        exerciseIndex = 0;
        stopwatchText.setText(stringifyTime(workoutDuration - afterThisDuration.get(exerciseIndex), false));
        displayTenth(workoutDuration - afterThisDuration.get(exerciseIndex));

        timeWhenResumes = workoutDuration;
        nextTimeLandmark = workoutDuration - 1000; // update the notification one second after the start of the workout
        runOrPause = PAUSED;
        exerciseTitleText.setText(currentWorkout.getExercises().get(exerciseIndex).getName());
        isBegin = true;
        isFinished = false;

        // put notification
        updateTimerNotification(workoutDuration - afterThisDuration.get(exerciseIndex));


        // start/stop the countdowntimer with the button
        // region startButton
        startPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (stopwatchText.getText().toString() == "00:00.0") { // if there is no time left
                    return;
                }
                if (runOrPause == RUNNING) { // if it is currently running
                    // pause the countdown homie
                    startPauseButton.setText("Resume");
                    workoutCountDownTimer.cancel(); // this doesn't tell the timer to save the paused time but it will cancel it
                    // the time to start with upon resume depends on "timeWhenResumes = millisUntilFinished"
                } else if (runOrPause == PAUSED) {
                    // resume the countdown
                    startPauseButton.setText("Pause");

                    workoutCountDownTimer = new CountDownTimer(timeWhenResumes, 50) { // "tick" every 50 milliseconds

                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (isBegin) {
                                isBegin = false;
                                // speak the first exercise / set the text to first exercise
                                tts.speak(getStringOfCurrentExercise(), TextToSpeech.QUEUE_FLUSH, null);
                                // exerciseIndex should be 0 in the line below
                                // (since isBegin should only be true when you are trying to reset / you just started the workout)
                                exerciseTitleText.setText(currentWorkout.getExercises().get(exerciseIndex).getName());
                            }

                            totalTimeText.setText(stringifyTime(millisUntilFinished, false));
                            displayTenth(millisUntilFinished);

                            // time remaining for this exercise
                            long timeRemForThis = millisUntilFinished - afterThisDuration.get(exerciseIndex);

//                            indexText.setText(String.format("Index = %d\n %d - %d = %d", exerciseIndex, millisUntilFinished, afterThisDuration.get(exerciseIndex), timeRemForThis));

                            timeWhenResumes = millisUntilFinished; // for the other thread for pausing

                            if (!hasSpokenFive.get(exerciseIndex) && Math.abs(5000+SPEAKING_DELAY - timeRemForThis) < TIME_EPS) { // 5 seconds left for this exercise -- notify the user (first condition makes sure that you do not say 5 seconds left a million times for this one exercise))
                                hasSpokenFive.set(exerciseIndex, true); // register that this 5 second warning has already been spoken
                                if (!tts.isSpeaking()) { // other speakers have higher priority
                                    tts.speak("5 seconds left", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }  else if (exerciseIndex < exerciseDurations.size() && millisUntilFinished < afterThisDuration.get(exerciseIndex)) { // if time to move onto the next exercise
                                Toast.makeText(getApplicationContext(), "Moving onto next exercise with " + timeRemForThis + " ms left.", Toast.LENGTH_SHORT).show();
                                timeRemForThis = millisUntilFinished - afterThisDuration.get(++exerciseIndex); // INCREMENT HERE
                                exerciseTitleText.setText(currentWorkout.getExercises().get(exerciseIndex).getName()); // update title of exercise
                                tts.speak(getStringOfCurrentExercise(), TextToSpeech.QUEUE_FLUSH, null);
                            }

                            //one second has passed since the last use of this if condition
                            if (millisUntilFinished < nextTimeLandmark) {
                                nextTimeLandmark -= 1000; // decrement one second

                                // update the notification
                                updateTimerNotification(timeRemForThis);
                            }

                            stopwatchText.setText(stringifyTime(timeRemForThis, false)); //setStopWatchText(timeRemForThis);
                            displayTenth(millisUntilFinished);
                        }

                        @Override
                        public void onFinish() { // the entire workouts has finihsed, go to the next exercise (and set that timer up and start it)
                            stopwatchText.setText(stringifyTime(0, false));
                            displayTenth(0);

                            Toast.makeText(getApplicationContext(), "FINISHED!", Toast.LENGTH_LONG).show();
                            tts.speak("Nice work!", TextToSpeech.QUEUE_FLUSH, null);
                            isFinished = true;// done with workout

                            // bring you to congratulatory screen
                            Intent intent = new Intent(activityContext, FinishedWorkout.class); //TODO: send workout information to this activity
                            intent.putExtra("STATE_OF_QUIT", "finisher");
                            intent.putExtra("WORKOUT", currentWorkout);
                            activityContext.startActivity(intent);

                            finish(); // deletes notification and destroys this activity
                        }
                    };


                    // START THE COUNTDOWNTIMER
                    workoutCountDownTimer.start();
                }

                runOrPause = 1 - runOrPause; // toggle between whether the timer is paused or running
            }
        });
        //endregion startButton

       // // reset the last timer
//        resetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startPauseButton.setText("Start");
//                workoutCountDownTimer.cancel();
//                runOrPause = PAUSED;
//                timeWhenResumes = COUNTDOWN_TIME;
//                setStopWatchText(COUNTDOWN_TIME);
//            }
//        });

        // TODO: only show the seconds if the time is less than a minute (stylistic reasons)

        // text to speech timer
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
//                    isTTSInitialized = true;
                    tts.setLanguage(Locale.US);
//                    tts.setPitch(20000);
                    tts.speak("Initialized.", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }

    @Override
    public void onBackPressed() { // make sure that the user actually wants to quit
        if (!isFinished) {
            new AlertDialog.Builder(activityContext) // needs to be activity context (this)
                    .setTitle(currentWorkout.getTitle())
                    .setMessage("Are you sure you want to stop this workout?")
                    .setPositiveButton("Yes, I am a slacker", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (runOrPause == RUNNING) {
                                workoutCountDownTimer.cancel();
                            }

                            // start activity for post-workout
                            Intent intent = new Intent(activityContext, FinishedWorkout.class); //TODO: send workout information to this activity
                            intent.putExtra("STATE_OF_QUIT", "slacker");
                            activityContext.startActivity(intent);

                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
//                    .setIcon(R.drawable.fitness)
                    .show();
        }
    }

    @Override
    public void onDestroy() { // called by finish()
        deleteNotification();
//        Toast.makeText(getApplicationContext(), "Destroying DoWorkout", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        Toast.makeText(getApplicationContext(), "Got new intent", Toast.LENGTH_SHORT).show();
    }

    private void displayTenth(long time) {
        int tenth = ((int) time % 1000) / 100;
        tenthText.setText(String.valueOf(tenth));

    }

    private String stringifyTime(long time, boolean isMillis) { // display milliseconds or not
        int sec = (int) time / 1000;
        int min = sec / 60;
        sec %= 60;

        if (isMillis) { // should include milliseconds (really tenth of a second now
            int millis = ((int) time % 1000) / 100;
            return String.format("%02d:%02d.%01d", min, sec, millis);
        } else {
//            Log.e("false", "asdfasdf");
            return String.format("%02d:%02d", min, sec);
        }
    }


    // NOTIFICAITON METHODS

    private void updateTimerNotification(long time) {
        Intent notifyIntent = new Intent(this, DoWorkout.class);
        notifyIntent.putExtra("PLAY_WORKOUT", currentWorkout);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // make it so you go to the existing activity instance of doworkout

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);

        // make notification
        Notification notification = new Notification.Builder(/*getApplicationContext()*/ this)
                // capitalize the first letter of each word to make the exercise title look better
                .setContentTitle(WordUtils.capitalize(currentWorkout.getExercises().get(exerciseIndex).getName()))
                .setContentText(stringifyTime(time, false))
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentIntent(pIntent)
                .setAutoCancel(false)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // start the notification
        notificationManager.notify(NOTIFICATION_CODE, notification); // code, notification
    }

    private void deleteNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_CODE);
    }



    // UTILITY

    private String getStringOfCurrentExercise() { // used for texttospeech in speaking what the next exercise is
        Exercise curExercise = currentWorkout.getExercises().get(exerciseIndex);
        String name = curExercise.getName();

        if (curExercise.getCountType() == Exercise.REPS_COUNT) {
            String pluralSuffix = "";
            if (name.toLowerCase().charAt(name.length()-1) != 's') { // if exercise name is current singular
                pluralSuffix = "s"; // make it plural lol
            }
            return "Do " + String.valueOf(curExercise.getReps()) + " " + name + pluralSuffix;

        } else if (curExercise.getCountType() == Exercise.DURATION_COUNT) {

            // TODO: if greater than a minute, make a string in the format of min/seconds instead of just seconds
            return name + " for " + String.valueOf(curExercise.getDuration()) + " seconds";
        }

        return "I have no idea sorry lol";
    }

    private void initArrayLists() {
        // "calculate" the duration needed for each exercise
        for (int i = 0; i < currentWorkout.getExercises().size(); i++) {
            Exercise exercise = currentWorkout.getExercises().get(i);
            if (exercise.getCountType() == Exercise.DURATION_COUNT) { // just add the time
                exerciseDurations.add(1000 * (long) exercise.getDuration());
            } else if (exercise.getCountType() == Exercise.REPS_COUNT) { // multiply to get time
                exerciseDurations.add(1000 * (long) exercise.getTimePerRep() * exercise.getReps());
            }

            hasSpokenFive.add(false);
            workoutDuration += exerciseDurations.get(i); // find workout duration
        }

        // Build the time-remaining-after-this-exercise ArrayList
        long runningSum = 0;
        for (int i = exerciseDurations.size()-1; i >= 1; i--) {
            runningSum += exerciseDurations.get(i);
            afterThisDuration.add(runningSum);
        }
        Collections.reverse(afterThisDuration);
        afterThisDuration.add(0L);
    }

}