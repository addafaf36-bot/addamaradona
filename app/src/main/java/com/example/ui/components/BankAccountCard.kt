package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BankAccount
import com.example.data.BankProvider
import com.example.ui.theme.AbyssiniaAmber
import com.example.ui.theme.AbyssiniaContainer
import com.example.ui.theme.CbeContainer
import com.example.ui.theme.CbePurple
import com.example.ui.theme.TelebirrContainer
import com.example.ui.theme.TelebirrGreen

@Composable
fun BankAccountCard(
  account: BankAccount,
  isSelected: Boolean,
  isCopied: Boolean,
  onSelect: () -> Unit,
  onCopyClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val accentColor =
    when (account.provider) {
      BankProvider.TELEBIRR -> TelebirrGreen
      BankProvider.CBE -> CbePurple
      BankProvider.ABYSSINIA -> AbyssiniaAmber
    }
  val iconBgColor =
    when (account.provider) {
      BankProvider.TELEBIRR -> TelebirrContainer
      BankProvider.CBE -> CbeContainer
      BankProvider.ABYSSINIA -> AbyssiniaContainer
    }
  val providerIcon =
    when (account.provider) {
      BankProvider.TELEBIRR -> Icons.Rounded.PhoneAndroid
      BankProvider.CBE, BankProvider.ABYSSINIA -> Icons.Rounded.AccountBalance
    }

  val borderColor by
    animateColorAsState(
      targetValue =
        if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
      label = "bank_card_border",
    )

  Card(
    onClick = onSelect,
    modifier = modifier.fillMaxWidth().testTag("bank_card_${account.id}"),
    shape = RoundedCornerShape(18.dp),
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (isSelected) MaterialTheme.colorScheme.surface
          else MaterialTheme.colorScheme.surface,
      ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
    border = BorderStroke(width = if (isSelected) 1.8.dp else 1.dp, color = borderColor),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier =
          Modifier.size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(iconBgColor),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = providerIcon,
          contentDescription = account.displayName,
          tint = accentColor,
          modifier = Modifier.size(24.dp),
        )
      }

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(3.dp),
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = iconBgColor,
        ) {
          Text(
            text = account.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
          )
        }

        Text(
          text = account.accountNumber,
          style =
            MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.6.sp,
            ),
          color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
          text = stringResource(id = R.string.bank_owner_label, account.ownerName),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      val copyButtonContainer by
        animateColorAsState(
          targetValue =
            if (isCopied) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primaryContainer,
          label = "copy_btn_bg",
        )
      val copyButtonContent by
        animateColorAsState(
          targetValue =
            if (isCopied) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
          label = "copy_btn_fg",
        )

      FilledTonalButton(
        onClick = onCopyClick,
        modifier =
          Modifier.minimumInteractiveComponentSize().testTag("copy_button_${account.id}"),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        colors =
          ButtonDefaults.filledTonalButtonColors(
            containerColor = copyButtonContainer,
            contentColor = copyButtonContent,
          ),
      ) {
        Icon(
          imageVector = if (isCopied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
        )
        Text(
          text =
            stringResource(
              id = if (isCopied) R.string.action_copied else R.string.action_copy
            ),
          style = MaterialTheme.typography.labelLarge,
          modifier = Modifier.padding(start = 6.dp),
        )
      }
    }
  }
}
