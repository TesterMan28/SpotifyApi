package com.example.android.spotifyapi6.Models

class Image(height: Int, url: String, width: Int) {

    private var height: Int = height
    private var url: String = url
    private var width: Int = width

    fun getHeight(): Int {
        return height
    }

    fun getURL(): String {
        return url
    }

    fun getWidth(): Int {
        return width
    }
}