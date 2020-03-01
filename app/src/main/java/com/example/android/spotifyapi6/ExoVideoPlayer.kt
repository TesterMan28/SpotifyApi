package com.example.android.spotifyapi6

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsManifest
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util


class ExoVideoPlayer : AppCompatActivity() {

    lateinit var playerView: PlayerView
    lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_video_player)

        // 1. Create a default TrackSelector
        /*
        val mainHandler = Handler()
        val bandwidthMeter = DefaultBandwidthMeter.Builder(this)
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)

         */

        playerView = findViewById(R.id.player_view)
    }

    override fun onStart() {
        super.onStart()

        /*
        player = ExoPlayerFactory.newSimpleInstance(this,
            DefaultTrackSelector())

         */


        player = SimpleExoPlayer.Builder(this).build()

        playerView.player = player

        // Add a listener to the player
        player.addListener(
            object:Player.EventListener {
                override fun onTimelineChanged(timeline: Timeline, @Player.TimelineChangeReason reason: Int) {
                    val manifest = player.currentManifest
                    if (manifest != null) {
                        val hlsManifest = manifest as HlsManifest
                        // Do something with the manifest
                    }
                }
            }
        )

        // Produces DataSource instances through which media data is loaded
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoVideoPlayer"))

        // This is the MediaSource representing the media to be played
        val audioSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            //.createMediaSource(Uri.parse("http://www.largesound.com/ashborytour/sound/AshboryBYU.mp3"))
            .createMediaSource(Uri.parse("http://extenview.com/convert"))

        player.prepare(audioSource)



        // Create a data source factory
        /*
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory(
            Util.getUserAgent(this, "exo-demo"))

         */

        // Create a HLS media source pointing to a playlist uri
        /*
        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .setAllowChunklessPreparation(true)
            //.createMediaSource(Uri.parse("http://184.72.239.149/vod/smil:BigBuckBunny.smil/playlist.m3u8"))
            .createMediaSource(Uri.parse(""))

         */

        /*
        val dataSourceFactory:DefaultDataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "exo-demo"))

        val mediaSource: ExtractorMediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Samples.MR4_URI)

        player.prepare(mediaSource)

         */
        //player.prepare(hlsMediaSource)
        player.playWhenReady = true


    }

    // Function to setup player. https://www.youtube.com/watch?v=5Jn8h9VAryA
    fun setupPlayer() {
        val rendererFactory = DefaultRenderersFactory(this)
        rendererFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)

        val trackSelector = DefaultTrackSelector(this)
        //player = SimpleExoPlayer.Builder(this, rendererFactory, trackSelector)
    }
}
