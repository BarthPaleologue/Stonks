package com.example.stonks

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
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
    private var toCurrency = "€"
    private var fromCurrency = "€"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<EditText>(R.id.startingAmount).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (getStartingAmount() > 1000000) rireDeDroite()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                findViewById<TextView>(R.id.toCurrency).text =
                    getConvertedString(currentRate) + toCurrency
            }
        })

        findViewById<TextView>(R.id.fromCurrency).text = fromCurrency
        findViewById<TextView>(R.id.toCurrency).text = "0$toCurrency"

        money()
    }

    private fun rireDeDroite() {
        val resId = getResources().getIdentifier(
            R.raw.rire_de_droite.toString(),
            "raw", getPackageName()
        )
        val mediaPlayer = MediaPlayer.create(this, resId)
        mediaPlayer.start()
    }

    private fun money() {
        val resId = getResources().getIdentifier(
            R.raw.money.toString(),
            "raw", getPackageName()
        )
        val mediaPlayer = MediaPlayer.create(this, resId)
        mediaPlayer.start()
    }

    private fun isNumber(s: String?): Boolean {
        return if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) || it == '.' }
    }

    private fun getStartingAmount(): Double {
        val amountStr = findViewById<EditText>(R.id.startingAmount).text.toString()
        if (amountStr.isEmpty() || !isNumber(amountStr)) return 0.0
        return amountStr.toDouble()
    }

    fun getConvertedString(rate: Double): String {
        return DecimalFormat("#.##").format(getStartingAmount() * rate).replace(",", ".")
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // get rate from result
        if (requestCode == 666 && data != null) {
            currentRate = data.getDoubleExtra("rate", 1.0)
            toCurrency = data.getStringExtra("toCurrency") ?: ""
            fromCurrency = data.getStringExtra("fromCurrency") ?: ""
            findViewById<TextView>(R.id.fromCurrency).text = fromCurrency
            findViewById<TextView>(R.id.toCurrency).text =
                getConvertedString(currentRate) + toCurrency
        }
    }

    @SuppressLint("SetTextI18n")
    fun invert(view: View) {
        val temp = fromCurrency
        fromCurrency = toCurrency
        toCurrency = temp

        findViewById<EditText>(R.id.startingAmount).setText(getConvertedString(currentRate))
        currentRate = 1 / currentRate

        findViewById<TextView>(R.id.fromCurrency).text = fromCurrency
        findViewById<TextView>(R.id.toCurrency).text =
            "${getConvertedString(currentRate)}$toCurrency"
    }

    fun chooseRate(view: View) {
        val intent = Intent(this, RateActivity::class.java)
        this.startActivityForResult(intent, 666)
    }

    fun copyResult(view: View) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            "label",
            "${getStartingAmount().toString()}$fromCurrency is ${getConvertedString(currentRate)}$toCurrency"
        )
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
        money()
    }
}