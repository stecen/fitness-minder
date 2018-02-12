package ase.activityminder.serializables;

import java.io.Serializable;

public class Exercise implements Serializable {
    public final static int REPS_COUNT = 0;
    public final static int DURATION_COUNT = 1;
    public int id; // id (for use with database);
    private String type;
    private int duration = -1;
    private int reps = -1;
    private int time = -1;
    private int countType;

    public Exercise() {
        type = "";
    }

    public String getName() {
        return type;
    }

    public void setName(String type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getTimePerRep() {
        return time;
    }

    public void setTimePerRep(int time) {
        this.time = time;
    }

    public int getCountType() {
        return countType;
    }

    public void setCountType(int c) {
        this.countType = c;
    }
}
