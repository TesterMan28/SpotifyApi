package com.example.android.spotifyapi6.Models

class Song(id: String, name: String) {

    private var id: String = id
    private var name: String = name

    fun getId(): String {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }
}