package com.example.pokedex

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.Headers
import org.json.JSONObject
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var pokemonInput: TextInputLayout
    private lateinit var pokemonFindButton: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pokemonFindButton = findViewById(R.id.pokemon_finder)
        pokemonInput = findViewById(R.id.input_pokemon)

        pokemonFindButton.setOnClickListener {
            val pokemonInputEditText = pokemonInput.editText
            val pokemonName = pokemonInputEditText?.text.toString().trim()
            if (pokemonName.isNotEmpty()) {
                callPokemonAPI(pokemonName)
            } else {
                Toast.makeText(this@MainActivity, "Please enter a Pokemon name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callPokemonAPI(pokemonName: String) {
        val client = AsyncHttpClient()
        Log.d("Pokemon Name", "pokemon: $pokemonName")
        client["https://pokeapi.co/api/v2/pokemon/$pokemonName", object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                var pokemon = json.jsonObject
                Log.d("response", json.jsonObject.toString())
                Log.d("pokemon", "pokemon found: $json")
                renderPokemonInfo(pokemon)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Pokemon Error", errorResponse)
            }
        }]
    }

    private fun renderPokemonInfo(pokemon: JSONObject) {
        val abilitiesArray = pokemon.getJSONArray("abilities")
        val abilitiesList = mutableListOf<String>()
        for (i in 0 until abilitiesArray.length()) {
            val abilityObject = abilitiesArray.getJSONObject(i).getJSONObject("ability")
            Log.d("ability", "$abilityObject")
            println(abilityObject)
            val abilityName = abilityObject.getString("name")
            abilitiesList.add(abilityName)
        }

        val typesArray = pokemon.getJSONArray("types")
        val typesList = mutableListOf<String>()
        for (i in 0 until typesArray.length()) {
            val typeObject = typesArray.getJSONObject(i).getJSONObject("type")
            val typeName = typeObject.getString("name")
            typesList.add(typeName)
        }

        val baseExperience = pokemon.getInt("base_experience")

        findViewById<TextView>(R.id.pokemon_name).text = pokemon.getString("name").toString().capitalize(Locale.ROOT)
        findViewById<TextView>(R.id.pokemon_types).text = "Abilities: ${abilitiesList.joinToString(", ")}"
        findViewById<TextView>(R.id.pokemon_base_exp).text = "Types: ${typesList.joinToString(", ")}"
        findViewById<TextView>(R.id.pokemon_abilities).text = "Base Experience: $baseExperience"


    }
}