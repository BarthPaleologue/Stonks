package com.example.stonks

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private var currentRate = 1.0
    private var toCurrency = ""
    private var fromCurrency = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<EditText>(R.id.startingAmount).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                findViewById<TextView>(R.id.textView).text = getConvertedString(currentRate)
            }
        })

        // update every second
        /*Thread {
            var theta = 0.0
            while (true) {
                theta += 0.5
                Thread.sleep(16)
                runOnUiThread {
                    findViewById<TextView>(R.id.convertButton).rotationY = theta.toFloat()
                }
            }
        }.start()*/
    }

    fun isNumber(s: String?): Boolean {
        return if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) || it == '.' }
    }

    fun getStartingAmount(): Double {
        val amountStr = findViewById<EditText>(R.id.startingAmount).text.toString();
        amountStr.replace(Regex("[^€$£]"), "")
        if(amountStr.isEmpty() || !isNumber(amountStr)) return 0.0
        return amountStr.toDouble()
    }

    fun getConvertedString(rate: Double): String {
        return DecimalFormat("#.##").format(getStartingAmount() * rate) + toCurrency
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // get rate from result
        if (requestCode == 666 && data != null) {
            currentRate = data.getDoubleExtra("rate", 1.0)
            toCurrency = data.getStringExtra("toCurrency") ?: ""
            fromCurrency = data.getStringExtra("fromCurrency") ?: ""
            findViewById<TextView>(R.id.fromCurrency).text = fromCurrency
            findViewById<TextView>(R.id.textView).text = getConvertedString(currentRate)
        }
    }

    fun chooseRate(view: View) {
        val intent = Intent(this, RateActivity::class.java);
        this.startActivityForResult(intent, 666);
    }

    fun copyResult(view: View) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", getConvertedString(currentRate))
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }
}