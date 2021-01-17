package com.duke.annotationprocessor_test

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.duke.viewinject.ViewInject
import com.duke.viewinject.ViewInjectHelper

class MainActivity : AppCompatActivity() {

    @JvmField
    @ViewInject(R.id.text_view)
    var myView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewInjectHelper.injectView(this)
        myView?.text = "赋值成功了89"

    }


}