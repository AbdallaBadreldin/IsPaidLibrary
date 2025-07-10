package com.fstech.ispaidlibrary

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import kotlin.system.exitProcess


//Don't forget to add internet permission in manifest
/*
<uses-permission android:name="android.permission.INTERNET" />
*/

class IsPaid(private val context: Context) : Builder {
    private var message: String = "Please Contact Developer!!!...."
    private var url: String =
        "https://raw.githubusercontent.com/AbdallaBadreldin/IsPaid/refs/heads/main/ISPaide.txt"

    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences(
            "com.example.ipcd.isPaid_preferences", Context.MODE_PRIVATE
        )
    }

    override fun setMessage(message: String) {
        this.message = message
    }

    override fun setUrl(url: String) {
        this.url = url
    }

    private val handler = CoroutineExceptionHandler { context, exception ->
        println("Caught $exception")
        //ifOffline and shared prefrences is false
        val isPaid = sharedPref.getBoolean("isPaid", true)
        if (isPaid == null) {
            //never connected to internet
            return@CoroutineExceptionHandler
        } else if (isPaid) {
            //use paid his fees and continue
            return@CoroutineExceptionHandler
        } else {
            //Toast him and close it NOW!!!
            runBlocking {
                Toast.makeText(
                    this@IsPaid.context, "Please Contact Developer!!!....", Toast.LENGTH_LONG
                ).show()
                delay(10000)
//                        moveTaskToBack(true);
                exitProcess(-1)
            }
        }

    }

    fun build() {
        // may use instead of global
        // CoroutineScope(Dispatchers.IO).launch(handler) {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val quotesApi = retrofit.create(QuotesApi::class.java)
        // launching a new coroutine
        GlobalScope.launch(handler) {
            val result = quotesApi.getIsUserPaid()
            if (result != null) {
                sharedPref.edit { putBoolean("isPaid", result.body()!!.isPaid) }
                Log.d("IsPaid", "isPaid: ${result.body()!!.isPaid}")
                // Checking the results if false shutdown the whole work if not continue
                if (!result.body()!!.isPaid) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, message, Toast.LENGTH_LONG
                        ).show()
                        delay(10000)
//                        moveTaskToBack(true);
                        exitProcess(-1)
                    }
                }
            }
        }
    }

    interface QuotesApi {
        @GET("ISPaide.txt")
        suspend fun getIsUserPaid(): Response<PaidResponse>
    }

    data class PaidResponse(
        val isPaid: Boolean
    )

}