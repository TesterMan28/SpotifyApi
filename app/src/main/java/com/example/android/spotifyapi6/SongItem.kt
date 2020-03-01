package com.example.android.spotifyapi6

class SongItem(imageResource: Int, text1: String, text2: String) {
    private val mImageResource = imageResource
    private val mText1 = text1
    private val mText2 = text2

    fun getImageResource(): Int {
        return mImageResource
    }

    fun getText1(): String {
        return mText1
    }

    fun getText2(): String {
        return mText2
    }
}