package com.duke.viewinject.newp;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-03 19:02
 * description:
 */
public interface IViewInject<T> {
    void inject(T t, Object source);
}
