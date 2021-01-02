package com.duke.inject_api;

import android.view.View;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-02 21:33
 * description:
 */
public class Utils {
    public static <T> T getViewById(View parentView, int id) {
        return (T) parentView.findViewById(id);
    }
}
