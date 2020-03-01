package com.example.android.spotifyapi6

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.*
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.android.spotifyapi6.Models.User
import com.fasterxml.jackson.core.JsonFactory
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class YoutubeActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    lateinit var mCredential: GoogleAccountCredential
    lateinit var mOutputText: TextView
    lateinit var mCallApiButton: Button
    lateinit var mProgress: ProgressBar




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 11:23, 10/02/2020, Creating Layout from code directly instead of XML?
        val activityLayout = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        activityLayout.layoutParams = lp
        activityLayout.orientation = LinearLayout.VERTICAL
        activityLayout.setPadding(16, 16, 16, 16)

        val tlp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        mCallApiButton = Button(this)
        mCallApiButton.text = BUTTON_TEXT
        mCallApiButton.setOnClickListener {
            mCallApiButton.isEnabled = false
            mOutputText.text = ""
            getResultsFromApi()
            fetchJson()
            mCallApiButton.isEnabled = true
        }

        activityLayout.addView(mCallApiButton)

        mOutputText = TextView(this)
        mOutputText.layoutParams = tlp
        mOutputText.setPadding(16, 16, 16, 16)
        mOutputText.isVerticalScrollBarEnabled = true
        mOutputText.movementMethod = ScrollingMovementMethod()
        mOutputText.text = "Click the $BUTTON_TEXT button to test the API"
        activityLayout.addView(mOutputText)

        // Not implementing ProgressBar
        mProgress = ProgressBar(this)


        //setContentView(R.layout.activity_youtube)
        setContentView(activityLayout)

        // Initialize credentials and service object.
        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
            applicationContext, listOf(*SCOPES)
        )
            .setBackOff(ExponentialBackOff())


    }

    /**
     * Code from https://www.youtube.com/watch?v=53BsyxwSBJk
     * to fetch Youtube JSON data
     */
    fun fetchJson() {

    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    fun getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices()
        } else if (mCredential.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline()) {
            mOutputText.text = "No network connection available"
        } else {
            MakeRequestTask(mCredential).execute()
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    fun chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                mCredential.selectedAccountName = accountName
                getResultsFromApi()
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER
                )
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                this,
                "This app needs to access your Google account (via Contacts).",
                REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS)
        }
    }


    /**
    * Called when an activity launched here (specifically, AccountPicker
    * and authorization) exits, giving you the requestCode you started it with,
    * the resultCode it returned, and any additional data from it.
    * @param requestCode code indicating which activity result is incoming.
    * @param resultCode code indicating the result of the incoming
    *     activity result.
    * @param data Intent (containing result data) returned by incoming
    *     activity result.
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode != Activity.RESULT_OK) {
                    mOutputText.text = "This app requires Google Play Services. Please install" +
                            "Google Play Services on your device and relaunch this app"
                } else {
                    getResultsFromApi()
                }
            }
            REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    if (accountName != null) {
                        val settings: SharedPreferences = getPreferences(Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = settings.edit()
                        editor.putString(PREF_ACCOUNT_NAME, accountName)
                        editor.apply()
                        mCredential.selectedAccountName = accountName
                        getResultsFromApi()
                    }
                }
            }
            REQUEST_AUTHORIZATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    getResultsFromApi()
                }
            }
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this
        )
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param perms The requested permission list. Never null.
     */
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param perms The requested permission list. Never null.
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     * Updated function for api 29 and above
     */
    private fun isDeviceOnline(): Boolean {
        val connMgr: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()

        connMgr.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.i("Activity", "onAvailable")

                    // check if NetworkCapabilities has TRANSPORT_WIFI
                    val isWifi: Boolean = connMgr.getNetworkCapabilities(network).hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )


                }

                override fun onLost(network: Network) {
                    Log.i("Activity", "onLost")
                }
            }
        )
        // Temp return value
        return true
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
            this,
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog.show()
    }

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    inner class MakeRequestTask(credential: GoogleAccountCredential): AsyncTask<Void, Void, List<String>>() {
        var mLastError: Exception? = null

        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        var mService = com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
            .setApplicationName("Youtube Data API Android Quickstart")
            .build()

        /**
         * Background task to call YouTube Data API.
         * @param params no parameters needed for this task.
         */
        override fun doInBackground(vararg params: Void?): List<String>? {
            return try {
                getDataFromApi()
            } catch (e: Exception) {
                mLastError = e
                cancel(true)
                null
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        @Throws(IOException::class)
        fun getDataFromApi(): ArrayList<String> {
            // Get a list of up to 10 files.
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

        override fun onPreExecute() {
            mOutputText.text = ""
        }

        override fun onPostExecute(result: List<String>?) {
            if (result == null || result.isEmpty()) {
                mOutputText.text = "No results returned"
            } else {
                // Commented it out as List does not have .add method
                //result.add(0, "Data retrieved using the YouTube Data API")
                mOutputText.text = TextUtils.join("\n", result)
            }
        }

        override fun onCancelled() {
            if (mLastError != null) {
                if (mLastError is GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                        (mLastError as GooglePlayServicesAvailabilityIOException)
                            .connectionStatusCode
                    )
                } else if (mLastError is UserRecoverableAuthIOException) {
                    startActivityForResult(
                        (mLastError as UserRecoverableAuthIOException).intent,
                        YoutubeActivity.REQUEST_AUTHORIZATION
                    )
                } else {
                    mOutputText.text = "The following error occurred:\n ${mLastError!!.message}"
                }
            } else {
                mOutputText.text = "Request cancelled"
            }
        }
    }



    companion object {
        const val REQUEST_ACCOUNT_PICKER = 1000
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

        private const val BUTTON_TEXT = "Call YouTube Data API"
        private const val PREF_ACCOUNT_NAME = "accountName"
        private val SCOPES = arrayOf<String>(YouTubeScopes.YOUTUBE_READONLY)
    }
}


