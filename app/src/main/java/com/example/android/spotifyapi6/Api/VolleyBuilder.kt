package com.example.android.spotifyapi6.Api

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue



// Build a Volley request queue to make API requests
// Taken from: https://developer.android.com/training/volley/requestqueue
class VolleyBuilder constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: VolleyBuilder? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleyBuilder(context).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, keeps from leaking the
        // Activity or BroadcastReceiver if someone passes one in
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}