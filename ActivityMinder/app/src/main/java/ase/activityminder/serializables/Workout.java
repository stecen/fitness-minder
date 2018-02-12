package ase.activityminder.serializables;

import java.io.Serializable;
import java.util.ArrayList;

public class Workout implements Serializable, Comparable {
    private String title;
    private ArrayList<Exercise> exercises = new ArrayList<>();
    private int numberOfPlays; // used to determine the user's favorite workouts

    public Workout() {
        title = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    public int getNumberOfPlays() {
        return numberOfPlays;
    }

    public void setNumberOfPlays(int i) {
        numberOfPlays = i;
    }

    public void resetNumberOfPlays() {
        numberOfPlays = 0;
    }

    public void incrementNumberOfPlays() {
        numberOfPlays++;
    }


    @Override
    public int compareTo(Object o) {
        Workout w2 = (Workout) o;

        if (this.numberOfPlays < w2.getNumberOfPlays()) {
            return 1;
        } else if (this.numberOfPlays == w2.getNumberOfPlays()) {
            return 0;
        } else {
            return -1;
        }
    }
}
