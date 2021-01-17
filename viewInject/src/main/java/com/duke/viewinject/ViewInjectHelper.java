package com.duke.viewinject;

import javax.annotation.Nonnull;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-02 21:33
 * description:
 */
public class ViewInjectHelper {

    public static void injectView(Object activity) {
        if (activity == null) {
            return;
        }
        findProxyActivity(activity).inject(activity, activity);
    }

    public static void injectView(Object holder, Object view) {
        if (holder == null || view == null) {
            return;
        }
        findProxyActivity(holder).inject(holder, view);
    }

    private static IInjectInterface findProxyActivity(@Nonnull Object host) {
        String className = host.getClass().getName() + AnnotationBean.CLASS_PROXY;
        try {
            return (IInjectInterface) Class.forName(className).newInstance();
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Can not find %s in %s.",
                    className,
                    ViewInjectHelper.class.getSimpleName()));
        }
    }
}
