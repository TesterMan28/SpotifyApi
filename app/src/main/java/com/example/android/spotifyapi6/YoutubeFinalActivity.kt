package com.example.android.spotifyapi6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android.spotifyapi6.R
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport

import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.GeneralSecurityException

class YoutubeFinalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_final)

        CoroutineScope(IO).launch {
            makeRequest("Test")
        }

        //YoutubeSearch.main(ArrayList("Test"))
    }

    suspend fun makeRequest(query: String) {
        CoroutineScope(IO).launch {
            var mLastError: Exception? = null

            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            var mService: YouTube = YouTube.Builder(transport, jsonFactory, null)
                .setApplicationName("Youtube Data API Android Quickstart")
                .build()

            val data = getDataFromApi(mService)
            for (i in data) {
                println("Value of i $i")
            }
        }
    }

    @Throws(IOException::class)
    suspend fun getDataFromApi(mService: YouTube): ArrayList<String> {
        var channelInfo = ArrayList<String>()
        var result = mService.channels().list("snippet, contentDetails, statistics")
            .setForUsername("Angryjoeshow")
            .execute()
        var channels = result.items
        if (channels != null) {
            val channel = channels.get(0)
            channelInfo.add("This channel's ID is ${channel.id}. Its title is " +
                    "${channel.snippet.title} and it has ${channel.statistics.viewCount} views. Subscriber count ${channel.statistics.subscriberCount}")
        }
        return channelInfo
    }
    /*

    // Need to set this value for code to compile
    // Setting API KEY for Youtube Data
    val DEVELOPER_KEY = "AIzaSyACmfEcZ7Vbq1rL8HKZX-xvu2nZnQbArfM"

    val APPLICATION_NAME = "Spotify API"
    val JSON_FACTORY = JacksonFactory.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_final)

        createService()
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    @Throws(GeneralSecurityException::class, IOException::class)
        fun getService(): YouTube {
            val httpTransport = NetHttpTransport()
            return YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build()
        }

    @Throws(GeneralSecurityException::class, IOException::class, GoogleJsonResponseException::class)
    fun createService() {
        val youtubeService = getService()

        // Define and execute the API request
        val request = youtubeService.search()
            .list("snippet")
        val response = request.setKey(DEVELOPER_KEY)
            .setQ("angryjoeshow")
            .setType("video")
            .execute()

        print(response)
    }


     */
}
