package com.example.android.spotifyapi6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.spotifyapi6.Connectors.SongService
import com.example.android.spotifyapi6.Models.Song
import com.example.android.spotifyapi6.Models.Track


class SongsActivity : AppCompatActivity(), SongsAdapter.OnSongListener {

    private lateinit var recyclerView: RecyclerView
    private var viewAdapter: RecyclerView.Adapter<*>? = null
    private lateinit var viewManager: RecyclerView.LayoutManager

    // Temp variables
    private var recentlyPlayedTracks: java.util.ArrayList<Song>? = null
    lateinit var searchResults: ArrayList<Track>

    // private var emptyAdapter:

    // XML Components
    lateinit var search_box: EditText
    lateinit var search_button: Button

    var exampleList: ArrayList<SongItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_songs)

        /*
        exampleList.add(SongItem(R.drawable.ic_android, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_file, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_money, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_android, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_file, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_money, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_android, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_file, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_money, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_android, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_file, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_money, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_android, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_file, "Line 1", "Line 2"))
        exampleList.add(SongItem(R.drawable.ic_attach_money, "Line 1", "Line 2"))

         */



        // Find views for XML components
        search_box = findViewById(R.id.search_box)
        search_button = findViewById(R.id.search_button)
        /*
        search_button.setOnClickListener(View.OnClickListener {
            fun onClick(v: View) {
                Toast.makeText(this, "Button clicked", Toast.LENGTH_LONG).show()
            }
        })

         */

        viewManager = LinearLayoutManager(this)
        //viewAdapter = SongsAdapter(search_results, this)

        recyclerView = findViewById<RecyclerView>(R.id.songsView).apply {
            // Improves performance if changes in content
            // do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // Set layout manager
            layoutManager = viewManager

            // Specify adapter
            adapter = viewAdapter
        }


        search_button.setOnClickListener {
            // Store results from SongService.getSearchResult(): ArrayList<Tracks>
            val songService: SongService = SongService(applicationContext)
            songService.createSearchUrl(search_box.text.toString())


            /*
            songService.getSearchResults()
                searchResults = songService.getTracks()
                Log.d("NAME", searchResults[0].getName())
                Log.d("ARTIST NAME", searchResults[0].getArtists()[0].getName())
                Log.d("TRACK IMAGE", searchResults[0].getAlbum().getImages()[0].getURL())
            }

             */


            songService.getSearchResults(object:VolleyCallBack, SongsAdapter.OnSongListener {
                override fun onSongClick(position: Int) {
                    Log.d("CLICKED", "Item clicked: $position")
                }

                override fun onSuccess() {
                    searchResults = songService.getTracks()
                    //Log.d("NAME", searchResults[0].getName())
                    //Log.d("ARTIST NAME", searchResults[0].getArtists()[0].getName())
                    //Log.d("TRACK IMAGE", searchResults[0].getAlbum().getImages()[0].getURL())

                    // Reset the layout manager and view adapter?
                    viewAdapter = SongsAdapter(searchResults, this)
                    recyclerView.layoutManager = viewManager
                    recyclerView.adapter = viewAdapter

                    // TODO: Send the data to youtube query api
                }

            })


            //searchResults = songService.getTracks()

            /*
            songService.getSearchResults {
                searchResults = songService.getTracks()
            }

             */
            //songService.getSearchResults {  }

            // Print log information


            /*
            viewAdapter = SongsAdapter(search_results, this)
            recyclerView.adapter = viewAdapter
            */
        }
    }

    override fun onSongClick(position: Int) {
        // If we wanted to link to a new activity, we would do it here
        // eg. val intent = Intent(etc..)

        Log.d("CLICKED", "Item clicked: $position")
    }
}
