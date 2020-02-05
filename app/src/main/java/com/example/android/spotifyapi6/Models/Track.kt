// TODO: In the future, convert this class to type data class

package com.example.android.spotifyapi6.Models

class Track(name: String, artists: ArrayList<Artist>) {
    private var name: String = name     // Track name
    // lateinit var artists: Artist        // List of artist, newly added
    private var artists: ArrayList<Artist> = artists


    // Get name of Track
    fun getName(): String {
        return name
    }

    // Add artist to Track class
    fun addArtist(artist: Artist) {
        artists.add(artist)
    }

    // Get list of artists for Track
    fun getArtists(): ArrayList<Artist> {
        return artists
    }
}