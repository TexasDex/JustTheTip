package com.texasdex.justthetip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.texasdex.justthetip.ui.theme.JustTheTipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val preferencesManager = PreferencesManager(applicationContext)
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CalculatorViewModel(preferencesManager) as T
            }
        }
        val viewModel = ViewModelProvider(this, viewModelFactory)[CalculatorViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            JustTheTipTheme(dynamicColor = false) {
                TipAppNav(viewModel)
            }
        }
    }
}
