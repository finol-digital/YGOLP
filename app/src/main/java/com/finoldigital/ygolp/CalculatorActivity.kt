package com.finoldigital.ygolp

import android.graphics.Color
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import android.widget.TextView
import androidx.wear.widget.BoxInsetLayout

const val EXTRA_ADD = "com.finoldigital.ygolp.EXTRA_ADD"

class CalculatorActivity : WearableActivity() {

    private var ygolp: Int = DEFAULT_LIFE_POINTS
    private var add: Boolean = false

    private lateinit var operand: TextView
    private var s: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        ygolp = intent.getIntExtra(EXTRA_YGOLP, DEFAULT_LIFE_POINTS)
        add = intent.getBooleanExtra(EXTRA_ADD, false)

        operand = findViewById(R.id.operand)

        val background: BoxInsetLayout = findViewById(R.id.background)
        background.setBackgroundColor(if (add) Color.GREEN else Color.RED)

        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)
        val button4: Button = findViewById(R.id.button4)
        val button5: Button = findViewById(R.id.button5)
        val button6: Button = findViewById(R.id.button6)
        val button7: Button = findViewById(R.id.button7)
        val button8: Button = findViewById(R.id.button8)
        val button9: Button = findViewById(R.id.button9)
        val buttonX: Button = findViewById(R.id.buttonX)
        val button0: Button = findViewById(R.id.button0)
        val buttonY: Button = findViewById(R.id.buttonY)

        button1.setOnClickListener { s += "1"; operand.text = s }
        button2.setOnClickListener { s += "2"; operand.text = s }
        button3.setOnClickListener { s += "3"; operand.text = s }
        button4.setOnClickListener { s += "4"; operand.text = s }
        button5.setOnClickListener { s += "5"; operand.text = s }
        button6.setOnClickListener { s += "6"; operand.text = s }
        button7.setOnClickListener { s += "7"; operand.text = s }
        button8.setOnClickListener { s += "8"; operand.text = s }
        button9.setOnClickListener { s += "9"; operand.text = s }
        buttonX.setOnClickListener { setResult(RESULT_OK, intent); finish() }
        button0.setOnClickListener { s += "0"; operand.text = s }
        buttonY.setOnClickListener {
            val input = s.toInt()
            intent.apply { putExtra(EXTRA_YGOLP, if (add) (ygolp + input) else (ygolp - input)) }
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}