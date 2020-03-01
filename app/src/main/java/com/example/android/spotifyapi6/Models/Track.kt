// TODO: In the future, convert this class to type data class

package com.example.android.spotifyapi6.Models

class Track(name: String, artists: ArrayList<Artist>, album: Album) {
    private var name: String = name     // Track name
    // lateinit var artists: Artist        // List of artist, newly added
    private var artists: ArrayList<Artist> = artists
    // private var images: ArrayList<Image> = images
    private var album: Album = album


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

    // Get album object
    fun getAlbum(): Album {
        return album
    }

    // Add images to Track class
    /*
    fun addImage(image: Image) {
        images.add(image)
    }

     */

    // Get list of images
    /*
    fun getImages(): ArrayList<Image> {
        return images
    }

     */

    // TODO: Maybe add setter and getter for album
}