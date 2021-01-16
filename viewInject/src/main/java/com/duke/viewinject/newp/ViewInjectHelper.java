package com.duke.viewinject.newp;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-03 21:33
 * description:
 */
public class ViewInjectHelper {

    public static void injectView(Object activity) {
        findProxyActivity(activity).inject(activity, activity);
    }

    public static void injectView(Object fieldAffiliatedClass, Object view) {
        findProxyActivity(fieldAffiliatedClass).inject(fieldAffiliatedClass, view);
    }

    private static IViewInject findProxyActivity(Object object) {
        try {
            return (IViewInject) Class.forName(object.getClass().getName() + ViewInjectUtil.CLASS_PROXY).newInstance();
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
        String param1 = object.getClass().getSimpleName() + ViewInjectUtil.CLASS_PROXY;
        String param2 = ViewInjectHelper.class.getSimpleName();
        String error = String.format("Can not find %s in %s.", param1, param2);
        throw new RuntimeException(error);
    }
}
