package ase.activityminder.search;


import java.util.HashMap;

/**
 * Created by Steven on 7/30/2015.
 */

// CONTAINS INFORMATION FOR ALL EXERCISE DATA


public class ExerciseData {
    public HashMap<String, String> info = new HashMap<>();
    //    String name, rating, type, muscle, otherMuscles, equipment, mechanics, level, guide, url;
    public static final String[] HASH_KEYS = {"name", "rating", "type", "muscle", "otherMuscles", "equipment", "mechanics", "level", "guide", "url"};
    public static final String[] ALL_TABLE_COLUMNS = {"_id", "name", "rating", "type", "muscle", "otherMuscles", "equipment", "mechanics", "level", "guide", "url"};


    public ExerciseData(String n, String r, String t, String m, String o, String e, String mu, String l, String g, String u) {
//        name = n;
//        rating = r;
//        type = t;
//        muscle = mu;
//        otherMuscles = o;
//        equipment = e;
//        mechanics = m;
//        level = l;
//        guide = g;
//        url = u;

        info.put("name", n);
        info.put("rating", r);
        info.put("type", t);
        info.put("muscle", mu);
        info.put("otherMuscles", o);
        info.put("equipment", e);
        info.put("mechanics", m);
        info.put("level", l);
        info.put("guide", g);
        info.put("url", u);
    }
}
