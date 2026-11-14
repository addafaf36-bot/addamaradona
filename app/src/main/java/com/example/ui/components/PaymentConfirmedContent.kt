package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.PaymentReceipt

@Composable
fun PaymentConfirmedContent(
  receipt: PaymentReceipt,
  onNewPaymentClick: () -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth().testTag("confirmed_receipt_card"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(22.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Box(
        modifier =
          Modifier.size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Rounded.Verified,
          contentDescription = stringResource(id = R.string.receipt_title),
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(40.dp),
        )
      }

      Text(
        text = stringResource(id = R.string.receipt_title),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
      )

      Text(
        text = stringResource(id = R.string.receipt_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
          CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          ),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          ReceiptRow(
            label = stringResource(id = R.string.receipt_recipient_label),
            value = stringResource(id = R.string.receipt_recipient_value),
          )
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
          ReceiptRow(
            label = stringResource(id = R.string.receipt_method_label),
            value = receipt.method,
          )
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
          ReceiptRow(
            label = stringResource(id = R.string.receipt_ref_label),
            value = receipt.transactionRef,
          )
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
          ReceiptRow(
            label = stringResource(id = R.string.receipt_time_label),
            value = receipt.submittedAt,
          )
        }
      }

      Button(
        onClick = onNewPaymentClick,
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 50.dp)
            .testTag("receipt_new_payment_button"),
        shape = RoundedCornerShape(14.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.AddCard,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stringResource(id = R.string.action_new_payment),
          style = MaterialTheme.typography.labelLarge,
        )
      }

      FilledTonalButton(
        onClick = onBackClick,
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag("receipt_back_button"),
        shape = RoundedCornerShape(14.dp),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stringResource(id = R.string.action_back),
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface,
    )
  }
}
