package com.example.android.spotifyapi6.Models

class Artist(id: String, name: String) {

    private var id: String = id
    private var name: String = name

    fun getId(): String {
        return id
    }

    fun getName(): String {
        return name
    }
}