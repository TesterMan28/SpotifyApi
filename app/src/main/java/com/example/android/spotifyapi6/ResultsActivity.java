package com.example.android.spotifyapi6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.spotifyapi6.Connectors.SongService;
import com.example.android.spotifyapi6.Models.Song;
import com.example.android.spotifyapi6.Models.Track;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private EditText searchEdit;
    private Button searchButton;
    private TextView resultsView;

    private SongService songService;
    private ArrayList<Track> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        songService = new SongService(getApplicationContext());

        searchEdit = findViewById(R.id.search_edit);
        searchButton = findViewById(R.id.search_button);
        resultsView = findViewById(R.id.search_results);

        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);

        searchButton.setOnClickListener(addListener);
    }

    private void getResults(String query) {
        songService.createSearchUrl(query);
        songService.getSearchResults(() -> {
            searchResults = songService.getTracks();
            Log.d("RESULTS", "GOT TRACK SEARCH RESULTS");
            //updateSong();
        });
    }

    private View.OnClickListener addListener = v -> {
        String searchQuery = searchEdit.getText().toString();
        if (searchQuery.length() > 0) {
            getResults(searchQuery);
        }

    };
}
