package com.example.android.spotifyapi6

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageLoader
import com.example.android.spotifyapi6.Models.Track
import com.squareup.picasso.Picasso

class SongsAdapter(val dataset: ArrayList<Track>, onSongListener: OnSongListener): RecyclerView.Adapter<SongsAdapter.ViewHolder>()  {

    // val values = dataset
    val mOnSongListener = onSongListener
    // lateinit var context: Context =


    inner class ViewHolder(itemView: View, onSongListener: OnSongListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val context = itemView.context

        val container = itemView.findViewById<RelativeLayout>(R.id.container)
        val mImageView = itemView.findViewById<ImageView>(R.id.logo)
        val mTextView1 = itemView.findViewById<TextView>(R.id.title)
        val mTextView2 = itemView.findViewById<TextView>(R.id.description)
        val onSongListener = onSongListener

        val test = itemView.setOnClickListener(this)

        override fun onClick(v: View?) {
            onSongListener.onSongClick(adapterPosition)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that element

        // Apply animations to views here
        // Animation for photo
        holder.mImageView.animation = AnimationUtils.loadAnimation(holder.context, R.anim.fade_transition_animation)

        // Animation for RelativeLayout container
        holder.container.animation = AnimationUtils.loadAnimation(holder.context, R.anim.fade_scale_animation)

        //holder.mImageView.setImageResource(dataset[position].getImageResource())
        //holder.mTextView1.text = dataset[position].getText1()
        //holder.mTextView2.text = dataset[position].getText2()

        Picasso.get().load(dataset[position].getAlbum().getImages()[0].getURL()).into(holder.mImageView)
        holder.mTextView1.text = dataset[position].getName()

        // Get ArrayList of artists
        //val artists = dataset[position].getArtists().size
        val artists = dataset[position].getArtists()


        // Compile the string to set holder.mTextView2

        var artist_string = ""
        for (artist in artists) {
            // TODO: When it is the last artist, do not put a "," symbol behind it
            artist_string += artist.getName() + ", "
        }
        Log.d("ARTIST STRING", artist_string)
        for (artist in artists) {
            // holder.mTextView2.text = artist.getName()
            holder.mTextView2.text = artist_string
        }
        /*
        val artist_string: String = ""
        // Compile the string of artists
        for (artist in artists) {
            artist_string.plus(artist.getName())
        }
        holder.mTextView2.text = artist_string
        */

        /*
        for (artist in artists) {
            holder.mTextView2.text = artist.getName()
        }

         */


        //holder.mTextView2.text = dataset[position].getArtists().
    }



    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        // Set the view's size, margins, paddings and layout parameters below as needed

        return ViewHolder(v, mOnSongListener)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    interface OnSongListener {
        fun onSongClick(position: Int)
    }
}