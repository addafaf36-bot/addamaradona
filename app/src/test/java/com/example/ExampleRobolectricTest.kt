package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.PaymentPreferencesRepository
import com.example.ui.PaymentViewModel
import com.example.ui.screens.PaymentPortalScreen
import com.example.ui.theme.KefiyaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeRule = createComposeRule()

  @Test
  fun `app name matches Amharic branding`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ክፍያ", appName)
  }

  @Test
  fun `payment confirmation flow validates and unlocks with receipt`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = PaymentViewModel(PaymentPreferencesRepository(context))

    composeRule.setContent {
      KefiyaTheme { PaymentPortalScreen(viewModel = viewModel) }
    }

    // All 4 accounts are visible (scroll lower cards into Robolectric viewport)
    composeRule.onNodeWithText("0942320888").performScrollTo().assertIsDisplayed()
    composeRule.onNodeWithText("0925792262").performScrollTo().assertIsDisplayed()
    composeRule.onNodeWithText("1000282540634").performScrollTo().assertIsDisplayed()
    composeRule.onNodeWithText("66833787").performScrollTo().assertIsDisplayed()

    // Select CBE card and copy account
    composeRule.onNodeWithTag("copy_button_cbe").performScrollTo().performClick()
    composeRule.waitForIdle()
    val clipboard =
      context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    assertEquals("1000282540634", clipboard.primaryClip?.getItemAt(0)?.text?.toString())
    composeRule.onNodeWithText("ንግድ ባንክ (CBE)").performScrollTo().assertIsDisplayed()

    // Submitting empty reference shows validation error
    composeRule.onNodeWithTag("back_button").performScrollTo()
    composeRule.onNodeWithTag("confirm_payment_button").performScrollTo().performClick()
    composeRule
      .onNodeWithText(
        context.getString(R.string.error_empty_transaction_ref),
        useUnmergedTree = true,
      )
      .performScrollTo()
      .assertIsDisplayed()

    // Enter valid Transaction ID and submit
    composeRule.onNodeWithTag("pay_ref_input").performScrollTo().performTextInput("FT24112XYZ")
    composeRule.onNodeWithTag("confirm_payment_button").performScrollTo().performClick()

    composeRule.waitUntil(timeoutMillis = 3_000L) {
      composeRule
        .onAllNodes(androidx.compose.ui.test.hasTestTag("confirmed_receipt_card"))
        .fetchSemanticsNodes()
        .isNotEmpty()
    }

    composeRule.onNodeWithText("FT24112XYZ").assertIsDisplayed()

    // Navigate back to status overview
    composeRule.onNodeWithTag("receipt_back_button").performScrollTo().performClick()
    composeRule.onNodeWithTag("open_payment_form_button").performScrollTo().assertIsDisplayed()
  }
}
