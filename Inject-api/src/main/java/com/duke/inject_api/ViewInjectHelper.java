package com.duke.inject_api;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-02 21:33
 * description:
 */
public class ViewInjectHelper {
    public static final String SUFFIX = "_ViewInject";

    public static void inject(Activity target) {
        ClassLoader classLoader = target.getClass().getClassLoader();
        try {

            String proxyClazzName = target.getClass().getName() + SUFFIX;
            Log.e("ViewInjectHelper#inject", proxyClazzName);
            Class<?> aClass = classLoader.loadClass(proxyClazzName);
            View rootView = target.getWindow().getDecorView();
            Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
            declaredConstructors[0].setAccessible(true);
            declaredConstructors[0].newInstance(target, rootView);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
