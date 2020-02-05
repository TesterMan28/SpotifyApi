// TODO: Allow spaced queries to be searched

package com.example.android.spotifyapi6.Connectors

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.android.spotifyapi6.Models.Artist
import com.example.android.spotifyapi6.Models.Song
import com.example.android.spotifyapi6.Models.Track
import com.example.android.spotifyapi6.VolleyCallBack
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class SongService(context: Context) {

    private var songs: ArrayList<Song> = ArrayList()
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("SPOTIFY", 0)
    private var queue: RequestQueue = Volley.newRequestQueue(context)
    private var tracks: ArrayList<Track> = ArrayList()       // Stores track search results

    // Create variable to hold search endpoint base url
    private var search_endpoint = "https://api.spotify.com/v1/search?"

    fun getSongs(): ArrayList<Song> {
        return songs
    }

    fun getTracks(): ArrayList<Track> {
        return tracks
    }

    fun getRecentlyPlayedTracks(callback: VolleyCallBack): ArrayList<Song> {
        val endpoint = "https://api.spotify.com/v1/me/player/recently-played"
        val jsonObjectRequest: JsonObjectRequest = object: JsonObjectRequest(
            Method.GET, endpoint, null,
            Response.Listener<JSONObject?> { response ->
                var gson = Gson()
                val jsonArray: JSONArray = response!!.optJSONArray("items")
                for (n in 0 until jsonArray.length()) {
                    try {
                        var jsonObject = jsonArray.getJSONObject(n)
                        jsonObject = jsonObject.optJSONObject("track")
                        val song = gson.fromJson<Song>(jsonObject.toString(), Song::class.java)
                        Log.d("SONG NAME", song.getName())
                        Log.d("SONG ID", song.getId())
                        println("Value: ${jsonObject.toString()}")
                        songs.add(song)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                callback.onSuccess()
            },
            Response.ErrorListener { error ->

            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                // return super.getHeaders()
                var headers: MutableMap<String, String> = HashMap()
                val token = sharedPreferences.getString("token", "")
                val auth = "Bearer $token"
                headers.put("Authorization", auth)
                return headers
            }
        }
        queue.add(jsonObjectRequest)
        return songs
    }

    fun addSongToLibrary(song: Song) {
        var payload: JSONObject = preparePutPayload(song)
        val jsonObjectRequest = prepareSongLibraryRequest(payload)
        queue.add(jsonObjectRequest);
    }

    fun prepareSongLibraryRequest(payload: JSONObject): JsonObjectRequest {
        return object :
            JsonObjectRequest(Request.Method.PUT, "https://api.spotify.com/v1/me/tracks", payload,
                Response.Listener<JSONObject?> { response ->

                },
                Response.ErrorListener { error ->

                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                // return super.getHeaders()
                var headers: MutableMap<String, String> = HashMap()
                val token = sharedPreferences.getString("token", "")
                val auth = "Bearer $token"
                headers.put("Authorization", auth)
                headers.put("Content-Type", "application/json")
                return headers
            }
        }
    }

    fun preparePutPayload(song: Song): JSONObject {
        var idarray = JSONArray()
        idarray.put(song.getId())
        var ids = JSONObject()
        try {
            ids.put("ids", idarray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ids
    }

    // Custom function. Get the search results from an artists name
    fun getSearchResults(callback: VolleyCallBack): ArrayList<Track> {
        //val endpoint = "https://api.spotify.com/v1/search?q=$query&type=artist"
        Log.d("ENDPOINT VALUE", search_endpoint)
        val jsonObjectRequest: JsonObjectRequest = object: JsonObjectRequest(
            Method.GET, search_endpoint, null,
            Response.Listener<JSONObject?> { response ->
                var gson = Gson()
                //val jsonArray: JSONArray = response!!.optJSONArray("tracks")
                var jsonObject = response!!.optJSONObject("tracks")
                var jsonArray = jsonObject.optJSONArray("items")

                var artistJSONArray: JSONArray
                var artistJSONObject: JSONObject
                for (n in 0 until jsonArray.length()) {
                    try {
                        jsonObject = jsonArray.get(n) as JSONObject
                        val track = gson.fromJson<Track>(jsonObject.toString(), Track::class.java)

                        // Deserialize the artists JSON data
                        //artistJSONArray = jsonObject.optJSONArray("artists")
                        //Log.d("ARTIST ARRAY", artistJSONArray.toString())


                        val trackArtist = track.getArtists()
                        for (i in trackArtist) {
                            Log.d("TRACK ARTIST ID", i.getId())
                            Log.d("TRACK ARTIST NAME", i.getName())
                        }


                        Log.d("ALBUM NAME", jsonObject.getString("name"))
                        println("JSON: ${jsonObject.toString()}")
                        Log.d("TRACK VALUE", track.getName())
                        Log.d("ARTIST JSON", artistJSONArray.toString())
                        tracks.add(track)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                callback.onSuccess()
            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                //return super.getHeaders()
                var headers: MutableMap<String, String> = HashMap()
                val token = sharedPreferences.getString("token", "")
                val auth = "Bearer $token"
                headers.put("Accept", "application/json")
                headers.put("Content-Type", "application/json")
                headers.put("Authorization", auth)
                return headers
            }
        }
        queue.add(jsonObjectRequest)
        return tracks
    }

    // Custom function. Prepare the search query JSON
    fun prepareSearchPayload(artist: String) {
        val queryArray = JSONArray()
        //queryArray.put()
    }

    // Custom function. Add parameters to search url
    fun createSearchUrl(query: String) {
        search_endpoint = "https://api.spotify.com/v1/search?"        // Temp reset of the base url
        search_endpoint += "q=$query&type=track"
    }

}