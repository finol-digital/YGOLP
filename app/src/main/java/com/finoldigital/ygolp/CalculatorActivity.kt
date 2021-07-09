package com.finoldigital.ygolp

import android.graphics.Color
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView

const val EXTRA_CALC_MODE = "com.finoldigital.ygolp.EXTRA_CALC_MODE"

class CalculatorActivity : WearableActivity() {

    private var ygolp: Int = DEFAULT_LIFE_POINTS
    private var mode: Int = 0 // 0:=> 1:- 2:+

    private lateinit var operator: Button
    private lateinit var operand: TextView
    private var text: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        ygolp = intent.getIntExtra(EXTRA_YGOLP, DEFAULT_LIFE_POINTS)
        mode = intent.getIntExtra(EXTRA_CALC_MODE, 0)

        operator = findViewById(R.id.buttonMode)

        operand = findViewById(R.id.operand)
        operand.text = text

        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)
        val button4: Button = findViewById(R.id.button4)
        val button5: Button = findViewById(R.id.button5)
        val button6: Button = findViewById(R.id.button6)
        val button7: Button = findViewById(R.id.button7)
        val button8: Button = findViewById(R.id.button8)
        val button9: Button = findViewById(R.id.button9)
        val button0: Button = findViewById(R.id.button0)
        val button00: Button = findViewById(R.id.button00)
        val button000: Button = findViewById(R.id.button000)

        val buttonC: Button = findViewById(R.id.buttonC)
        val buttonX: Button = findViewById(R.id.buttonX)
        val buttonEquals: Button = findViewById(R.id.buttonEquals)

        applyMode()

        button1.setOnClickListener { append("1") }
        button2.setOnClickListener { append("2") }
        button3.setOnClickListener { append("3") }
        button4.setOnClickListener { append("4") }
        button5.setOnClickListener { append("5") }
        button6.setOnClickListener { append("6") }
        button7.setOnClickListener { append("7") }
        button8.setOnClickListener { append("8") }
        button9.setOnClickListener { append("9") }
        button0.setOnClickListener { append("0") }
        button00.setOnClickListener { append("00") }
        button000.setOnClickListener { append("000") }

        operator.setOnClickListener { nextMode() }
        buttonC.setOnClickListener { pop() }
        buttonX.setOnClickListener { setResult(RESULT_OK, intent); finish() }
        buttonEquals.setOnClickListener { submit() }
    }

    private fun nextMode() {
        mode++
        if (mode > 2)
            mode = 0
        applyMode()
    }

    private fun applyMode() {
        when(mode) {
            2 ->  {
                operator.text = "+"
                operator.setTextColor(Color.GREEN)
                operand.setTextColor(Color.GREEN)
            }
            1 ->  {
                operator.text = "-"
                operator.setTextColor(Color.RED)
                operand.setTextColor(Color.RED)
            }
            0 ->  {
                operator.text = "=>"
                operator.setTextColor(Color.YELLOW)
                operand.setTextColor(Color.YELLOW)
            }
        }
    }

    private fun append(char: String) {
        text = text.trimStart('0')
        text += char
        if (text.toInt() == 0)
            text = "0"
        operand.text = text
    }

    private fun pop() {
        text = text.substring(0, text.length - 1)
        if (text.isEmpty())
            text = "0"
        operand.text = text
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1 -> {
                    submit()
                    true
                }
                KeyEvent.KEYCODE_STEM_2 -> {
                    setResult(RESULT_OK, intent); finish()
                    true
                }
                else -> {
                    super.onKeyDown(keyCode, event)
                }
            }
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun submit() {
        val input = text.toInt()
        intent.apply {
            var result = ygolp
            when (mode) {
                0 -> result = input
                1 -> result = ygolp - input
                2 -> result = ygolp + input
            }
            putExtra(EXTRA_YGOLP, result)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

}