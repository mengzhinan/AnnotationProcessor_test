package com.duke.viewinject;


/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-02 21:33
 * description:
 */
public class ViewInjectHelper {

    public static void injectView(Object activity) {
        findProxyActivity(activity).inject(activity, activity);
    }

    public static void injectView(Object holder, Object view) {
        findProxyActivity(holder).inject(holder, view);
    }

    private static IInjectInterface findProxyActivity(Object host) {
        try {
            return (IInjectInterface) Class.forName(host.getClass().getName() + AnnotationBean.CLASS_PROXY).newInstance();
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
            String param1 = host.getClass().getSimpleName() + AnnotationBean.CLASS_PROXY;
            String param2 = ViewInjectHelper.class.getSimpleName();
            String error = String.format("Can not find %s in %s.", param1, param2);
            throw new RuntimeException(error);
        }
    }
}
