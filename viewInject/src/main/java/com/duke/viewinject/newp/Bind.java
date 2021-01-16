package com.duke.viewinject.newp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-03 21:33
 * description:
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Bind {
    int value();
}
