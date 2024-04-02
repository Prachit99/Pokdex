package com.example.pokedex

import PokemonAdapter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.material.textfield.TextInputLayout
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    private lateinit var pokemonInput: TextInputLayout
    private lateinit var pokemonFindButton: Button
    private lateinit var pokemonRecycleView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pokemonFindButton = findViewById(R.id.pokemon_finder)
        pokemonInput = findViewById(R.id.input_pokemon)
        pokemonRecycleView = findViewById(R.id.pokemonRecycleView)
        pokemonRecycleView.layoutManager = LinearLayoutManager(this)

        pokemonFindButton.setOnClickListener {
            val pokemonType = pokemonInput.editText?.text.toString().trim()
            if (pokemonType.isNotEmpty()) {
                callPokemonByTypeAPI(pokemonType)
            } else {
                Toast.makeText(this@MainActivity, "Please enter a Pokemon type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callPokemonByTypeAPI(pokemonType: String) {
        val client = AsyncHttpClient()
        Log.d("Pokemon Type", "pokemon type: $pokemonType")

        client.get("https://pokeapi.co/api/v2/type/$pokemonType", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON?) {
                json?.let {
                    val pokemonsArray = it.jsonObject.getJSONArray("pokemon")
                    val pokemonList = mutableListOf<Pokemon>()
                    for (i in 0 until pokemonsArray.length()) {
                        val pokemonObject = pokemonsArray.getJSONObject(i).getJSONObject("pokemon")
                        val name = pokemonObject.getString("name")
                        val url = pokemonObject.getString("url")
                        val pokemon = Pokemon(name, url)
                        pokemonList.add(pokemon)
                    }
                    Log.d("pokemon", "pokemon type found: $pokemonList")
                    callPokemonDetailsAPI(pokemonList)
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String?, throwable: Throwable?) {
                Log.d("Pokemon Type Error", errorResponse ?: "Unknown error")
                Toast.makeText(this@MainActivity, "No results found", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callPokemonDetailsAPI(pokemonList: List<Pokemon>) {
        val client = AsyncHttpClient()
        Log.d("Pokemon Details", "Details APi called $pokemonList");
        for (pokemon in pokemonList) {
            client.get(pokemon.url, object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON?) {
                    json?.let {
                        Log.d("Pokemon Details", "Pokemon found: ${it.jsonObject}")
                        val name = it.jsonObject.getString("name")
                        val imageUrl = it.jsonObject.getJSONObject("sprites").getString("front_default")
                        val typesArray = it.jsonObject.getJSONArray("types")
                        val typesList = mutableListOf<String>()
                        for (i in 0 until typesArray.length()) {
                            val typeObject = typesArray.getJSONObject(i).getJSONObject("type")
                            val typeName = typeObject.getString("name")
                            typesList.add(typeName)
                        }
                        pokemon.imageUrl = imageUrl
                        pokemon.types = typesList
                        if (pokemonList.indexOf(pokemon) == pokemonList.size - 1) {
                            renderPokemonList(pokemonList)
                        }
                    }
                }

                override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String?, throwable: Throwable?) {
                    Log.d("Pokemon Details Error", errorResponse ?: "Unknown error")
                    Toast.makeText(this@MainActivity, "Failed to fetch Pokemon details", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun renderPokemonList(pokemons: List<Pokemon>) {
        val recyclerView = findViewById<RecyclerView>(R.id.pokemonRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this) // Ensure proper layout manager is set
        recyclerView.adapter = PokemonAdapter(pokemons)
//        recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
        addDividerItemDecoration()
    }

    private fun addDividerItemDecoration() {
        val itemDecoration: DividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val divider: Drawable? = ContextCompat.getDrawable(this, R.drawable.divider_line) // Use your divider drawable
        divider?.let {
            itemDecoration.setDrawable(it)
            pokemonRecycleView.addItemDecoration(itemDecoration)
        }
    }

}
