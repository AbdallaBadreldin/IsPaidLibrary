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
import retrofit2.http.Path
import kotlin.system.exitProcess


//Don't forget to add internet permission in manifest
/*
<uses-permission android:name="android.permission.INTERNET" />
*/

class IsPaid(private val context: Context) : com.fstech.utils.Builder {
    private var message: String? = null
    private var url: String =
        "https://raw.githubusercontent.com/AbdallaBadreldin/IsPaid/refs/heads/main/ISPaide.txt"

    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences(
            "com.example.isPaid_preferences", Context.MODE_PRIVATE
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
//        Log.e("IsPaidResponse", "failed", exception)

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
                if (message != null) {
                    Toast.makeText(
                        this@IsPaid.context, message, Toast.LENGTH_LONG
                    ).show()
                }
                delay(10000)
//                        moveTaskToBack(true);
                exitProcess(-1)
            }
        }

    }

    fun build() {
        // may use instead of global
        // CoroutineScope(Dispatchers.IO).launch(handler) {
        val splitUrl = extractUrlParts(url)
        val instance: QuotesApi by lazy {

            val gson = GsonBuilder().create()
            val retrofit = Retrofit.Builder().baseUrl(splitUrl?.first ?: "")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            retrofit.create(QuotesApi::class.java)

        }

        // launching a new coroutine
        GlobalScope.launch(handler) {
            val result = instance.getIsUserPaid(splitUrl?.second ?: "")
            if (result.isSuccessful) {
                sharedPref.edit { putBoolean("isPaid", result.body()!!.isPaid) }
                Log.d("IsPaidResponse", "isPaid: ${result.body()!!.isPaid}")
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
            } else {
//                Log.d("IsPaidResponse", "isPaid: failed")
            }
        }
    }


    interface QuotesApi {
        // "{fileName}" is the placeholder in the path.
        // The value of the 'fileName' parameter in the function will replace {fileName}.
        @GET("{fileName}")
        suspend fun getIsUserPaid(@Path("fileName") dynamicFileName: String = "ISPaide.txt"): Response<PaidResponse>

        // If your "parameter" was meant to be a query parameter instead of a path segment:
        // @GET("some/fixed/endpoint")
        // suspend fun getIsUserPaidWithQuery(@Query("fileName") dynamicFileName: String = "ISPaide.txt"): Response<PaidResponse>
    }

    data class PaidResponse(
        val isPaid: Boolean
    )

}

fun extractUrlParts(fullUrl: String): Pair<String, String>? {
    val lastSlashIndex = fullUrl.lastIndexOf('/')
    if (lastSlashIndex == -1 || lastSlashIndex == fullUrl.length - 1) {
        // No slash found, or it's the last character (so no filename after it)
        return null
    }

    val basePath = fullUrl.substring(0, lastSlashIndex + 1) // Include the trailing slash
    val endpoint = fullUrl.substring(lastSlashIndex + 1)

    return Pair(basePath, endpoint)
}