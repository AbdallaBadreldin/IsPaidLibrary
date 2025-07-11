package com.fstech.ispaid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fstech.ispaid.ui.theme.IsPaidTheme
import com.fstech.ispaidlibrary.IsPaid

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IsPaidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        IsPaid(this).apply { setMessage("Please Contact Developer!!!...."); setUrl("https://raw.githubusercontent.com/AbdallaBadreldin/IsPaid/refs/heads/main/ISPaide.txt/");build() }
    }

    override fun onPause() {
        super.onPause()
        IsPaid(this).apply { setMessage("Please Contact Developer!!!...."); setUrl("https://raw.githubusercontent.com/AbdallaBadreldin/IsPaid/refs/heads/main/ISPaide.txt/");build() }
    }
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        IsPaidTheme {
            Greeting("Android")
        }
    }
}