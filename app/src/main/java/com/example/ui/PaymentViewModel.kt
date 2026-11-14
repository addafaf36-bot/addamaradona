package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BankAccount
import com.example.data.PaymentAccountsCatalog
import com.example.data.PaymentPreferencesRepository
import com.example.data.PaymentReceipt
import com.example.data.PortalScreenMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TransactionRefValidationError {
  NONE,
  EMPTY,
  TOO_SHORT,
}

data class PaymentUiState(
  val accounts: List<BankAccount> = PaymentAccountsCatalog.accounts,
  val methodOptions: List<String> = PaymentAccountsCatalog.paymentMethodOptions,
  val selectedMethod: String = PaymentAccountsCatalog.paymentMethodOptions.first(),
  val transactionRef: String = "",
  val validationError: TransactionRefValidationError = TransactionRefValidationError.NONE,
  val copiedAccountId: String? = null,
  val isSubmitting: Boolean = false,
  val isUnlocked: Boolean = false,
  val latestReceipt: PaymentReceipt? = null,
  val receiptHistory: List<PaymentReceipt> = emptyList(),
  val screenMode: PortalScreenMode = PortalScreenMode.PAYMENT_FORM,
)

class PaymentViewModel(
  private val repository: PaymentPreferencesRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(PaymentUiState())
  val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

  private var copyResetJob: Job? = null

  init {
    viewModelScope.launch {
      repository.paymentState.collect { stored ->
        _uiState.update { current ->
          current.copy(
            isUnlocked = stored.isUnlocked,
            receiptHistory = stored.receipts,
            latestReceipt = current.latestReceipt ?: stored.receipts.firstOrNull(),
          )
        }
      }
    }
  }

  fun selectMethod(method: String) {
    _uiState.update { it.copy(selectedMethod = method) }
  }

  fun selectBankAccount(account: BankAccount) {
    _uiState.update { it.copy(selectedMethod = account.methodOption) }
  }

  fun onTransactionRefChanged(value: String) {
    _uiState.update {
      it.copy(
        transactionRef = value,
        validationError = TransactionRefValidationError.NONE,
      )
    }
  }

  fun onAccountCopied(account: BankAccount) {
    _uiState.update {
      it.copy(
        copiedAccountId = account.id,
        selectedMethod = account.methodOption,
      )
    }
    copyResetJob?.cancel()
    copyResetJob =
      viewModelScope.launch {
        delay(2000L)
        _uiState.update { current ->
          if (current.copiedAccountId == account.id) current.copy(copiedAccountId = null)
          else current
        }
      }
  }

  fun confirmPayment() {
    val current = _uiState.value
    if (current.isSubmitting) return

    val trimmed = current.transactionRef.trim()
    val error =
      when {
        trimmed.isEmpty() -> TransactionRefValidationError.EMPTY
        trimmed.length < 4 -> TransactionRefValidationError.TOO_SHORT
        else -> TransactionRefValidationError.NONE
      }

    if (error != TransactionRefValidationError.NONE) {
      _uiState.update { it.copy(validationError = error) }
      return
    }

    viewModelScope.launch {
      _uiState.update { it.copy(isSubmitting = true, validationError = TransactionRefValidationError.NONE) }
      val formattedTime =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
      val receipt =
        PaymentReceipt(
          id = UUID.randomUUID().toString(),
          method = current.selectedMethod,
          transactionRef = trimmed,
          submittedAt = formattedTime,
        )
      repository.saveConfirmedPayment(receipt)
      _uiState.update {
        it.copy(
          isSubmitting = false,
          isUnlocked = true,
          transactionRef = "",
          latestReceipt = receipt,
          screenMode = PortalScreenMode.CONFIRMED_RECEIPT,
        )
      }
    }
  }

  fun closeModal() {
    _uiState.update {
      it.copy(
        screenMode = PortalScreenMode.STATUS_OVERVIEW,
        validationError = TransactionRefValidationError.NONE,
      )
    }
  }

  fun openPaymentForm() {
    _uiState.update {
      it.copy(
        screenMode = PortalScreenMode.PAYMENT_FORM,
        validationError = TransactionRefValidationError.NONE,
      )
    }
  }

  fun clearHistory() {
    viewModelScope.launch {
      repository.clearHistory()
      _uiState.update {
        it.copy(
          isUnlocked = false,
          latestReceipt = null,
          receiptHistory = emptyList(),
        )
      }
    }
  }

  companion object {
    fun factory(repository: PaymentPreferencesRepository): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return PaymentViewModel(repository) as T
        }
      }
  }
}
