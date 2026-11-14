package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.paymentDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "kefiya_payment_prefs")

data class StoredPaymentState(
  val isUnlocked: Boolean = false,
  val receipts: List<PaymentReceipt> = emptyList(),
)

class PaymentPreferencesRepository(private val context: Context) {

  private object Keys {
    val IS_UNLOCKED = booleanPreferencesKey("is_unlocked")
    val RECEIPTS_SERIALIZED = stringPreferencesKey("receipts_serialized")
  }

  val paymentState: Flow<StoredPaymentState> =
    context.paymentDataStore.data.map { prefs ->
      val isUnlocked = prefs[Keys.IS_UNLOCKED] ?: false
      val raw = prefs[Keys.RECEIPTS_SERIALIZED].orEmpty()
      val receipts = decodeReceipts(raw)
      StoredPaymentState(isUnlocked = isUnlocked, receipts = receipts)
    }

  suspend fun saveConfirmedPayment(receipt: PaymentReceipt) {
    context.paymentDataStore.edit { prefs ->
      prefs[Keys.IS_UNLOCKED] = true
      val existing = decodeReceipts(prefs[Keys.RECEIPTS_SERIALIZED].orEmpty())
      val updated = (listOf(receipt) + existing).take(15)
      prefs[Keys.RECEIPTS_SERIALIZED] = encodeReceipts(updated)
    }
  }

  suspend fun clearHistory() {
    context.paymentDataStore.edit { prefs ->
      prefs[Keys.IS_UNLOCKED] = false
      prefs.remove(Keys.RECEIPTS_SERIALIZED)
    }
  }

  private fun encodeReceipts(receipts: List<PaymentReceipt>): String {
    return receipts.joinToString(separator = RECORD_SEPARATOR) { receipt ->
      listOf(
          sanitize(receipt.id),
          sanitize(receipt.method),
          sanitize(receipt.transactionRef),
          sanitize(receipt.submittedAt),
        )
        .joinToString(separator = FIELD_SEPARATOR)
    }
  }

  private fun decodeReceipts(raw: String): List<PaymentReceipt> {
    if (raw.isBlank()) return emptyList()
    return raw.split(RECORD_SEPARATOR).mapNotNull { record ->
      val parts = record.split(FIELD_SEPARATOR)
      if (parts.size == 4) {
        PaymentReceipt(
          id = parts[0],
          method = parts[1],
          transactionRef = parts[2],
          submittedAt = parts[3],
        )
      } else {
        null
      }
    }
  }

  private fun sanitize(value: String): String {
    return value.replace(FIELD_SEPARATOR, " ").replace(RECORD_SEPARATOR, " ")
  }

  private companion object {
    const val FIELD_SEPARATOR = "\u001F"
    const val RECORD_SEPARATOR = "\u001E"
  }
}
