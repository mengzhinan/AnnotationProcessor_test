package com.duke.annotationprocessor_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.duke.inject_api.ViewInjectHelper
import com.duke.inject_processor.ViewInject

class MainActivity : AppCompatActivity() {

    @JvmField
    @ViewInject(R.id.text_view)
    var myView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewInjectHelper.inject(this)
        myView!!.text = "赋值成功了89"

    }



}