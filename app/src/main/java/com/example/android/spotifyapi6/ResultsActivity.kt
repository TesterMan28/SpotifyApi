// TODO: Success request
package com.example.android.spotifyapi6

import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Layout
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.android.spotifyapi6.Api.VolleyBuilder
import com.example.android.spotifyapi6.Connectors.SongService
import com.example.android.spotifyapi6.Models.Track
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset

class ResultsActivity: AppCompatActivity() {
    // Get the context of the activity
    //val context = applicationContext


    // Declare a private RequestQueue variable
    lateinit var requestQueue: RequestQueue

    // View Components
    lateinit var searchEdit: EditText
    lateinit var searchButton: Button
    lateinit var showDialog: Button
    lateinit var resultsView: TextView

    lateinit var songService: SongService
    lateinit var searchResults: ArrayList<Track>

    // Getting the shared preferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Find views for XML components
        searchEdit = findViewById(R.id.search_edit)
        searchButton = findViewById(R.id.search_button)
        showDialog = findViewById(R.id.show_dialog)
        resultsView = findViewById(R.id.search_results)

        val sharedPreferences = this.getSharedPreferences("SPOTIFY", 0)

        searchButton.setOnClickListener {
            val searchQuery = searchEdit.text.toString()
            if (searchQuery.isNotEmpty()) {
                getResults(searchQuery)
            }
        }

        // Dialog button listener
        showDialog.setOnClickListener {
            // Declare instance of interface to deliver action events

            val viewGroup = findViewById<ViewGroup>(android.R.id.content)

            val inflater = this.layoutInflater

            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_signin, viewGroup, false)

            val builder = AlertDialog.Builder(this)

            val usernameEdit = findViewById<EditText>(R.id.username)
            val passwordEdit = this.findViewById<EditText>(R.id.password)

            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Sign In"
                ) { dialog, which ->
                    // Sign in the user
                    val username = dialogView.findViewById<EditText>(R.id.username).editableText.toString()
                    val password = dialogView.findViewById<EditText>(R.id.password).editableText.toString()

                    // List of coroutines context: IO, Main, Default
                    CoroutineScope(IO).launch {
                        loginServer(username, password)
                    }

                }


            val alertDialog = builder.create()
            alertDialog.show()


        }
    }

    fun getResults(query: String) {
        songService.createSearchUrl(query)
        songService.getSearchResults(object:VolleyCallBack {
            override fun onSuccess() {
                searchResults = songService.getTracks()
                Log.d("RESULTS", "GOT TRACK SEARCH RESULTS")
                // updateSong()
            }

        })
    }

    // Send credentials to server to login
    suspend fun loginServer(username: String, password: String) {
        // Send credentials to server to login
        val loginEndpoint = "http://www.extenview.com/api/v1/login"



        requestQueue = VolleyBuilder.getInstance(this).requestQueue
        // Create new JsonObjectRequest
        val postParams = JSONObject()
        postParams.put("email", username)
        postParams.put("password", password)
        val jsonObjectRequest = object:JsonObjectRequest(Request.Method.POST, loginEndpoint, postParams,
            Response.Listener<JSONObject> { response ->
                // Success callback
                Log.d("Success", response.toString())
                Log.d("TOKEN VALUE", response.getString("token"))
                //val sharedPreferences = this.getSharedPreferences("SPOTIFY", 0)

                // Saving jwt token to shared preferences
                editor = getSharedPreferences("SPOTIFY", 0).edit()
                editor.putString("token", response.getString("token"))
                editor.apply()

                // Getting the shared preferences
                val sharedPreferences = getSharedPreferences("SPOTIFY", 0)
                Log.d("JWT TOKEN", sharedPreferences.getString("token", "NO TOKEN"))
            },
            Response.ErrorListener { error ->
                // Error callback
                Log.d("Error", error.toString())
            }) {
            /*
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                var headers: MutableMap<String, String> = HashMap()
                headers.put("Content-Type", "application/json")
                val creds = String.format("%s:%s", username, password)
                val auth = "Basic ${Base64.encodeToString(creds.getBytes())}"
                //return super.getHeaders()
            }

             */
        }
        requestQueue.add(jsonObjectRequest)
    }

    // Get audio track from the website and play it in ExoPlayer
    suspend fun getTrack() {
        val trackEndpoint = "http://www.extenview.com/login.php"
    }

    interface ExampleDialogListener {
        //fun applyTexts(username: String, password: String)
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }
}