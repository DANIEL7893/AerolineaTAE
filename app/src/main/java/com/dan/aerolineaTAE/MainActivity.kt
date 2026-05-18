package com.dan.aerolineaTAE

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dan.aerolineaTAE.navigation.NavGraph
import com.dan.aerolineaTAE.ui.theme.TaeTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val authViewModel: AuthViewModel = viewModel()
                    NavGraph(authViewModel)
                }
            }
        }
    }
}
