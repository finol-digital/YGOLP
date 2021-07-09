package com.finoldigital.ygolp

import android.graphics.Color
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import androidx.wear.widget.BoxInsetLayout

const val EXTRA_ADD = "com.finoldigital.ygolp.EXTRA_ADD"

class CalculatorActivity : WearableActivity() {

    private var ygolp: Int = DEFAULT_LIFE_POINTS
    private var add: Boolean = false

    private lateinit var operand: TextView
    private var text: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        ygolp = intent.getIntExtra(EXTRA_YGOLP, DEFAULT_LIFE_POINTS)
        add = intent.getBooleanExtra(EXTRA_ADD, false)

        operand = findViewById(R.id.operand)
        operand.text = text

        val background: BoxInsetLayout = findViewById(R.id.background)
        background.setBackgroundColor(if (add) Color.GREEN else Color.YELLOW)

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
        buttonX.setOnClickListener { setResult(RESULT_OK, intent); finish() }
        buttonY.setOnClickListener {
            val input = text.toInt()
            intent.apply { putExtra(EXTRA_YGOLP, if (add) (ygolp + input) else (ygolp - input)) }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun append(char: String) {
        if("0" == text)
            text = ""
        text += char
        operand.text = text
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1 -> {
                    val input = text.toInt()
                    intent.apply { putExtra(EXTRA_YGOLP, if (add) (ygolp + input) else (ygolp - input)) }
                    setResult(RESULT_OK, intent)
                    finish()
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

}