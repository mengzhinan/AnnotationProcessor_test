package com.duke.viewinject;

import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-17 13:13
 * description:
 */
public class MessageHelper {

    public static void printLog(Messager messager, Diagnostic.Kind kind, CharSequence msg) {
        printLog(messager, kind, msg, null);
    }

    public static void printLog(Messager messager, Diagnostic.Kind kind, CharSequence msg, @Nullable Element e) {
        if (messager == null
                || kind == null
                || msg == null) {
            return;
        }
        if (e != null) {
            messager.printMessage(kind, msg);
        } else {
            messager.printMessage(kind, msg, e);
        }
    }

}
