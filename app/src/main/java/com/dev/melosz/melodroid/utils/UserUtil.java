package com.dev.melosz.melodroid.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by marek.kozina on 8/25/2015.
 * TODO: remove this class, moving all methods to FragmentUtil
 */
public class UserUtil {

    /**
     * Helper method to print an object in pretty JSON format
     * @param obj AppUser the user object to display
     * @return String the JSON formatted output
     */
    public String prettyPrintObject(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(obj);
        return json;
    }
}
