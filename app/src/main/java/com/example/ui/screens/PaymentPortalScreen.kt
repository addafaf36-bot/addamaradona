package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.BankAccount
import com.example.data.PortalScreenMode
import com.example.ui.PaymentUiState
import com.example.ui.PaymentViewModel
import com.example.ui.components.BankAccountCard
import com.example.ui.components.PaymentConfirmationForm
import com.example.ui.components.PaymentConfirmedContent
import com.example.ui.components.PaymentHeaderBanner
import com.example.ui.components.StatusOverviewContent
import kotlinx.coroutines.launch

@Composable
fun PaymentPortalScreen(
  viewModel: PaymentViewModel,
  modifier: Modifier = Modifier,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  BackHandler(enabled = uiState.screenMode != PortalScreenMode.STATUS_OVERVIEW) {
    viewModel.closeModal()
  }

  val onCopyAccount: (BankAccount) -> Unit = { account ->
    clipboardManager.setText(AnnotatedString(account.accountNumber))
    viewModel.onAccountCopied(account)
    coroutineScope.launch {
      snackbarHostState.currentSnackbarData?.dismiss()
      snackbarHostState.showSnackbar(
        message = context.getString(R.string.snackbar_copied_account, account.accountNumber),
        duration = SnackbarDuration.Short,
      )
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
  ) { innerPadding ->
    BoxWithConstraints(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      val isWideScreen = maxWidth >= 720.dp
      val scrollState = rememberScrollState()

      Column(
        modifier =
          Modifier.fillMaxWidth()
            .widthIn(max = if (isWideScreen) 1040.dp else 600.dp)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        PaymentHeaderBanner(isUnlocked = uiState.isUnlocked)

        AnimatedContent(
          targetState = uiState.screenMode,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "portal_screen_mode",
        ) { mode ->
          when (mode) {
            PortalScreenMode.PAYMENT_FORM -> {
              if (isWideScreen) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(18.dp),
                  verticalAlignment = Alignment.Top,
                ) {
                  BankAccountsSection(
                    uiState = uiState,
                    onSelectAccount = viewModel::selectBankAccount,
                    onCopyAccount = onCopyAccount,
                    modifier = Modifier.weight(1.05f),
                  )
                  PaymentConfirmationForm(
                    methodOptions = uiState.methodOptions,
                    selectedMethod = uiState.selectedMethod,
                    transactionRef = uiState.transactionRef,
                    validationError = uiState.validationError,
                    isSubmitting = uiState.isSubmitting,
                    onMethodSelected = viewModel::selectMethod,
                    onTransactionRefChanged = viewModel::onTransactionRefChanged,
                    onConfirmPayment = viewModel::confirmPayment,
                    onBackClick = viewModel::closeModal,
                    modifier = Modifier.weight(0.95f),
                  )
                }
              } else {
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                  BankAccountsSection(
                    uiState = uiState,
                    onSelectAccount = viewModel::selectBankAccount,
                    onCopyAccount = onCopyAccount,
                  )
                  PaymentConfirmationForm(
                    methodOptions = uiState.methodOptions,
                    selectedMethod = uiState.selectedMethod,
                    transactionRef = uiState.transactionRef,
                    validationError = uiState.validationError,
                    isSubmitting = uiState.isSubmitting,
                    onMethodSelected = viewModel::selectMethod,
                    onTransactionRefChanged = viewModel::onTransactionRefChanged,
                    onConfirmPayment = viewModel::confirmPayment,
                    onBackClick = viewModel::closeModal,
                  )
                }
              }
            }

            PortalScreenMode.STATUS_OVERVIEW -> {
              StatusOverviewContent(
                isUnlocked = uiState.isUnlocked,
                receiptHistory = uiState.receiptHistory,
                onOpenPaymentForm = viewModel::openPaymentForm,
                onClearHistory = viewModel::clearHistory,
              )
            }

            PortalScreenMode.CONFIRMED_RECEIPT -> {
              val receipt = uiState.latestReceipt
              if (receipt != null) {
                Box(
                  modifier = Modifier.fillMaxWidth(),
                  contentAlignment = Alignment.Center,
                ) {
                  PaymentConfirmedContent(
                    receipt = receipt,
                    onNewPaymentClick = viewModel::openPaymentForm,
                    onBackClick = viewModel::closeModal,
                    modifier = Modifier.widthIn(max = 560.dp),
                  )
                }
              } else {
                StatusOverviewContent(
                  isUnlocked = uiState.isUnlocked,
                  receiptHistory = uiState.receiptHistory,
                  onOpenPaymentForm = viewModel::openPaymentForm,
                  onClearHistory = viewModel::clearHistory,
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(56.dp))
      }
    }
  }
}

@Composable
private fun BankAccountsSection(
  uiState: PaymentUiState,
  onSelectAccount: (BankAccount) -> Unit,
  onCopyAccount: (BankAccount) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = stringResource(id = R.string.section_accounts_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
      )
      Text(
        text = stringResource(id = R.string.section_accounts_hint),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    uiState.accounts.forEach { account ->
      BankAccountCard(
        account = account,
        isSelected = uiState.selectedMethod == account.methodOption,
        isCopied = uiState.copiedAccountId == account.id,
        onSelect = { onSelectAccount(account) },
        onCopyClick = { onCopyAccount(account) },
      )
    }
  }
}
