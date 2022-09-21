package com.example.stonks

import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Callable

class LoadRates : Callable<JSONObject> {
    override fun call(): JSONObject {
        val url = URL("https://perso.telecom-paristech.fr/eagan/cours/igr201/data/taux_2017_11_02.json")
        return JSONObject(url.readText()).getJSONObject("rates")
    }
}