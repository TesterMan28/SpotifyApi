package com.example.android.spotifyapi6


import com.example.android.spotifyapi6.Api.Auth
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchResult
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * Print a list of videos matching a search term.
 *
 * Credits to : https://github.com/htchien/youtube-api-samples-kotlin/blob/master/src/main/kotlin/tw/htchien/youtube/api/data/Search.kt
 */
object YoutubeSearch {

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private val PROPERTIES_FILENAME = "youtube.properties"

    private val NUMBER_OF_VIDEOS_RETURNED: Long = 25

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private var youtube: YouTube? = null

    /*
     * Prompt the user to enter a query term and return the user-specified term.
     */
    // Use the string "YouTube Developers Live" as a default.
    private
    val inputQuery: String
    @Throws(IOException::class)
    get() {
        var inputQuery = ""

        print("Please enter a search term: ")
        var bReader = BufferedReader(InputStreamReader(System.`in`))
        inputQuery = bReader.readLine()

        if (inputQuery.isEmpty()) {
            inputQuery = "Youtube Developers Live"
        }
        return inputQuery
    }

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     * @param args command line args.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        // Read the developer key from the properties file
        val properties = Properties()
        try {
            val `in` = YoutubeSearch::class.java.getResourceAsStream("/$PROPERTIES_FILENAME")
            properties.load(`in`)
        } catch (e: IOException) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.cause + " : " + e.message)
            System.exit(1)
        }

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, HttpRequestInitializer { }).setApplicationName("youtube-cmdline-search-sample").build()

            // Prompt the user to enter a query term.
            val queryTerm = inputQuery

            // Define the API request for retrieving search results.
            val search = youtube!!.search().list("id,snippet")

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            val apiKey = properties.getProperty("youtube.apikey")
            search.key = apiKey
            search.q = queryTerm

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.type = "video"

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.fields = "items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)"
            search.maxResults = NUMBER_OF_VIDEOS_RETURNED

            // Call the API and print results.
            val searchResponse = search.execute()
            val searchResultList = searchResponse.items
            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm)
            }
        } catch (e: GoogleJsonResponseException) {
            System.err.println("There was a service error: " + e.details.code + " : "
                    + e.details.message)
        } catch (e: IOException) {
            System.err.println("There was an IO error: " + e.cause + " : " + e.message)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private fun prettyPrint(iteratorSearchResults: Iterator<SearchResult>, query: String) {

        println("\n=============================================================")
        println(
            "   First $NUMBER_OF_VIDEOS_RETURNED videos for search on \"$query\".")
        println("=============================================================\n")

        if (!iteratorSearchResults.hasNext()) {
            println(" There aren't any results for your query.")
        }

        while (iteratorSearchResults.hasNext()) {

            val singleVideo = iteratorSearchResults.next()
            val rId = singleVideo.id

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.kind == "youtube#video") {
                val thumbnail = singleVideo.snippet.thumbnails.default

                println(" Video Id" + rId.videoId)
                println(" Title: " + singleVideo.snippet.title)
                println(" Thumbnail: " + thumbnail.url)
                println("\n-------------------------------------------------------------\n")
            }
        }
    }
}