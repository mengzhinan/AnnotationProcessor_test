package com.duke.viewinject;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-03 19:02
 * description:
 */
public interface IInjectInterface<T> {
    void inject(T host, Object view);
}
