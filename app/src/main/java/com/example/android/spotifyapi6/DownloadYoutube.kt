package com.example.android.spotifyapi6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils
import com.yausername.youtubedl_android.DownloadProgressCallback
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DownloadYoutube : AppCompatActivity(), View.OnClickListener {

    lateinit var btnStartDownload: Button
    lateinit var etUrl: EditText
    lateinit var progressBar: ProgressBar
    lateinit var tvDownloadStatus: TextView

    private var downloading = false
    private val compositeDisposable = CompositeDisposable()

    private val callback =
        DownloadProgressCallback { progress, etaInSeconds ->
            runOnUiThread {
                progressBar.progress = progress.toInt()
                tvDownloadStatus.text = "${progress.toString()} % (ETA) ${etaInSeconds.toString()} seconds"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_youtube)

        initViews()
        initListeners()
    }

    fun initViews() {
        btnStartDownload = findViewById(R.id.btn_start_download)
        etUrl = findViewById(R.id.et_url)
        progressBar = findViewById(R.id.progress_bar)
        tvDownloadStatus = findViewById(R.id.tv_status)
    }

    fun initListeners() {
        btnStartDownload.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_start_download -> {
                startDownload()
            }
        }
    }

    fun startDownload() {
        if (downloading) {
            Toast.makeText(this, "cannot start download. a download is already in progress", Toast.LENGTH_LONG)
            return
        }

        if (!isStoragePermissionGranted()) {
            Toast.makeText(this, "grant storage permission and retry", Toast.LENGTH_LONG).show()
        }

        var url = etUrl.text.toString()
        if (url.isBlank()) {
            etUrl.error = "Invalid query"
            return
        }

        val request = YoutubeDLRequest(url)
        val youtubeDLDir = getDownloadLocation()
        //request.setOption("-o", "${youtubeDLDir.getAbsolutePath()}/%(title)s.%(ext)s")

        showStart()

        downloading = true
        val disposable: Disposable = Observable.fromCallable {
            YoutubeDL.getInstance().execute(request, callback)}
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                progressBar.setProgress(100)
                tvDownloadStatus.text = "Download complete"
                Toast.makeText(this, "download successful", Toast.LENGTH_LONG).show()
                downloading = false
            }, {
                tvDownloadStatus.text = "Download failed"
                Toast.makeText(this, "download failed", Toast.LENGTH_LONG).show()
                Log.e("ERROR", "failed to download")
                downloading = false
            })
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    @NonNull
    fun getDownloadLocation() {
        // val downloadsDir = Environment.getExternalStoragePublicDirectory()
    }

    fun showStart() {

    }

    fun isStoragePermissionGranted(): Boolean {
        return true
    }
}
