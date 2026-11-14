package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.TransactionRefValidationError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentConfirmationForm(
  methodOptions: List<String>,
  selectedMethod: String,
  transactionRef: String,
  validationError: TransactionRefValidationError,
  isSubmitting: Boolean,
  onMethodSelected: (String) -> Unit,
  onTransactionRefChanged: (String) -> Unit,
  onConfirmPayment: () -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var dropdownExpanded by remember { mutableStateOf(false) }
  val keyboardController = LocalSoftwareKeyboardController.current

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Text(
        text = stringResource(id = R.string.section_confirm_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )

      ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = { dropdownExpanded = !dropdownExpanded },
      ) {
        OutlinedTextField(
          value = selectedMethod,
          onValueChange = {},
          readOnly = true,
          label = { Text(stringResource(id = R.string.label_payment_method)) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Rounded.Payments,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
            )
          },
          trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
          },
          colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
          shape = RoundedCornerShape(14.dp),
          modifier =
            Modifier.fillMaxWidth()
              .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
              .testTag("pay_method_dropdown"),
        )

        ExposedDropdownMenu(
          expanded = dropdownExpanded,
          onDismissRequest = { dropdownExpanded = false },
        ) {
          methodOptions.forEachIndexed { index, option ->
            DropdownMenuItem(
              text = {
                Text(text = option, style = MaterialTheme.typography.bodyLarge)
              },
              onClick = {
                onMethodSelected(option)
                dropdownExpanded = false
              },
              modifier = Modifier.testTag("pay_method_option_$index"),
            )
          }
        }
      }

      val errorMessage =
        when (validationError) {
          TransactionRefValidationError.EMPTY ->
            stringResource(id = R.string.error_empty_transaction_ref)
          TransactionRefValidationError.TOO_SHORT ->
            stringResource(id = R.string.error_short_transaction_ref)
          TransactionRefValidationError.NONE -> null
        }

      OutlinedTextField(
        value = transactionRef,
        onValueChange = onTransactionRefChanged,
        label = { Text(stringResource(id = R.string.label_transaction_ref)) },
        placeholder = { Text(stringResource(id = R.string.placeholder_transaction_ref)) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Rounded.Receipt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
          )
        },
        trailingIcon = {
          if (transactionRef.isNotEmpty()) {
            IconButton(
              onClick = { onTransactionRefChanged("") },
              modifier = Modifier.testTag("clear_ref_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Clear,
                contentDescription = stringResource(id = R.string.cd_clear_input),
              )
            }
          }
        },
        isError = errorMessage != null,
        supportingText =
          errorMessage?.let { msg ->
            { Text(text = msg, color = MaterialTheme.colorScheme.error) }
          },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions =
          KeyboardActions(
            onDone = {
              keyboardController?.hide()
              onConfirmPayment()
            }
          ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag("pay_ref_input"),
      )

      Spacer(modifier = Modifier.height(2.dp))

      Button(
        onClick = {
          keyboardController?.hide()
          onConfirmPayment()
        },
        enabled = !isSubmitting,
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 52.dp)
            .testTag("confirm_payment_button"),
        shape = RoundedCornerShape(14.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
      ) {
        if (isSubmitting) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.2.dp,
            color = MaterialTheme.colorScheme.onPrimary,
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = stringResource(id = R.string.action_confirming),
            style = MaterialTheme.typography.labelLarge,
          )
        } else {
          Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(id = R.string.action_confirm_unlock),
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }

      FilledTonalButton(
        onClick = onBackClick,
        enabled = !isSubmitting,
        modifier =
          Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("back_button"),
        shape = RoundedCornerShape(14.dp),
        colors =
          ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
          ),
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
