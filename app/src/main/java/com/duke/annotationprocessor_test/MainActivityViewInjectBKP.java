// Generated code. Do not modify!
package com.duke.annotationprocessor_test;

import com.duke.viewinject.IInjectInterface;

public class MainActivityViewInjectBKP implements IInjectInterface<MainActivity> {

    @Override
    public void inject(MainActivity host, Object view) {
        if (host instanceof android.app.Activity) {
            host.myView = (android.widget.TextView) (((android.app.Activity) view).findViewById(2131231057));
        } else {
            host.myView = (android.widget.TextView) (((android.view.View) view).findViewById(2131231057));
        }
    }

}
