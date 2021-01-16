package com.duke.viewinject.newp;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-03 21:33
 * description:
 */
public class ViewInjectUtil {

    public static final String CLASS_PROXY = "$ViewInject";

    static boolean isPrivate(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(Modifier.PRIVATE);
    }

    static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen);
    }

}
