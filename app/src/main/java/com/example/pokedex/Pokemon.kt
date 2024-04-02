package com.example.pokedex

data class Pokemon(
    val name: String,
    val url: String,
    var types: List<String> = emptyList(),
    var imageUrl: String = ""
)