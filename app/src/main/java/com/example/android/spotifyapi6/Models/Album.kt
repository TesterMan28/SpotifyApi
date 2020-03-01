package com.example.android.spotifyapi6.Models

class Album(images: ArrayList<Image>) {
    private var images: ArrayList<Image> = images

    fun addImage(image: Image) {
        images.add(image)
    }

    fun getImages(): ArrayList<Image> {
        return images
    }
}