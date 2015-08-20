package ase.activityminder;

import android.text.InputFilter;
import android.text.Spanned;

public class UsernameFilter implements InputFilter {
    public static final String ILLEGAL_STRING = "~/\n\r\t\0\f`?*\\<>|\": ";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int asdf, int lol) {

        if (source != null && ILLEGAL_STRING.contains(("" + source))) {
            return "";
        }
        return null;
    }
}
