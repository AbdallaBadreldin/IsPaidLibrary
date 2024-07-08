package com.jetawy.ispaidlibrary


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
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

class IsPaid(private val context: Context) {
    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences(
            "com.example.ipcd.isPaid_preferences", Context.MODE_PRIVATE);
    }

    companion object {
        object RetrofitHelper {

            val baseUrl = "https://raw.githubusercontent.com/AbdallaBadreldin/IsPaid/main/"

            fun getInstance(): Retrofit {
                val gson = GsonBuilder().setLenient().create()
                return Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    // we need to add converter factory to
                    // convert JSON object to Java object
                    .build()
            }
        }
    }

    val handler = CoroutineExceptionHandler { context, exception ->
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

    init {
        // may use instead of global
        // CoroutineScope(Dispatchers.IO).launch(handler) {

        val quotesApi = RetrofitHelper.getInstance().create(QuotesApi::class.java)
        // launching a new coroutine
        GlobalScope.launch(handler) {
            val result = quotesApi.getIsUserPaid()
            if (result != null) {
                sharedPref.edit().putBoolean("isPaid", result.body()!!.isPaid).apply()
                Log.d("IsPaid", "isPaid: ${result.body()!!.isPaid}")
                // Checking the results if false shutdown the whole work if not continue
                if (!result.body()!!.isPaid) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "Please Contact Developer!!!....", Toast.LENGTH_LONG
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