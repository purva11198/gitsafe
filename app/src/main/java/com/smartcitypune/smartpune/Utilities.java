package com.smartcitypune.smartpune;

/**
 * Created by Kapil on 26-09-2018.
 */

public class Utilities {
    public static String getDate(Integer date) {
        String dateString = String.valueOf(date);
        return dateString.substring(6) + "/" + dateString.substring(4, 6) + "/" + dateString.substring(0, 4);
    }
}
