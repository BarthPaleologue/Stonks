package com.example.stonks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import org.json.JSONObject
import java.util.concurrent.Executors

class RateActivity : AppCompatActivity() {
    private var rates = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate)
        val executor = Executors.newSingleThreadExecutor()
        rates = executor.submit(LoadRates()).get()
    }

    fun sendBackData(view: View) {
        val fromEuro = findViewById<RadioButton>(R.id.fromEuro)
        val fromPound = findViewById<RadioButton>(R.id.fromPound)
        val toEuro = findViewById<RadioButton>(R.id.toEuro)
        val toPound = findViewById<RadioButton>(R.id.toPound)

        var rate = 1.0;
        val fromCurrency: String
        if (fromEuro.isChecked) {
            rate /= rates.get("EUR").toString().toDouble()
            fromCurrency = "€"
        } else if (fromPound.isChecked) {
            rate /= rates.get("GBP").toString().toDouble()
            fromCurrency = "£"
        } else {
            rate /= rates.get("USD").toString().toDouble()
            fromCurrency = "$"
        }

        val toCurrency: String
        if (toEuro.isChecked) {
            rate *= rates.get("EUR").toString().toDouble()
            toCurrency = "€"
        } else if (toPound.isChecked) {
            rate *= rates.get("GBP").toString().toDouble()
            toCurrency = "£"
        } else {
            rate *= rates.get("USD").toString().toDouble()
            toCurrency = "$"
        }

        intent.putExtra("rate", rate)
        intent.putExtra("fromCurrency", fromCurrency)
        intent.putExtra("toCurrency", toCurrency)
        setResult(RESULT_OK, intent);
        finish();
    }

    fun loadRatesFromFile(): JSONObject {
        val inputString =
            resources.openRawResource(R.raw.taux_2017_11_02).bufferedReader().use { it.readText() }
        return JSONObject(inputString)
    }
}