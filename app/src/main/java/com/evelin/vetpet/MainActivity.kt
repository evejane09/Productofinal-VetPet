package com.evelin.vetpet
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.evelin.vetpet.ui.theme.VetPetTheme
import com.evelin.vetpet.vistas.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VetPetTheme {
                MainScreen()
            }
        }
    }
}