package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.PaymentPreferencesRepository
import com.example.ui.PaymentViewModel
import com.example.ui.screens.PaymentPortalScreen
import com.example.ui.theme.KefiyaTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val repository = PaymentPreferencesRepository(applicationContext)
    setContent {
      KefiyaTheme {
        val paymentViewModel: PaymentViewModel =
          viewModel(factory = PaymentViewModel.factory(repository))
        PaymentPortalScreen(viewModel = paymentViewModel)
      }
    }
  }
}
