package com.finoldigital.ygolp

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class CalculatorActivity : WearableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
    }
}