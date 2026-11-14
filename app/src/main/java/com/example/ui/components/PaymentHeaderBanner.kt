package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.GoldLight

@Composable
fun PaymentHeaderBanner(
  isUnlocked: Boolean,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .heightIn(min = 156.dp)
        .clip(RoundedCornerShape(24.dp))
  ) {
    Image(
      painter = painterResource(id = R.drawable.img_payment_banner),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier.matchParentSize(),
    )

    Box(
      modifier =
        Modifier.matchParentSize()
          .background(
            Brush.verticalGradient(
              colors =
                listOf(
                  EmeraldDeep.copy(alpha = 0.72f),
                  EmeraldDeep.copy(alpha = 0.92f),
                )
            )
          )
    )

    Column(
      modifier = Modifier.fillMaxWidth().padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Surface(
        shape = RoundedCornerShape(50),
        color =
          if (isUnlocked) EmeraldBright.copy(alpha = 0.24f)
          else GoldLight.copy(alpha = 0.22f),
        modifier = Modifier.testTag("status_badge"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Icon(
            imageVector = if (isUnlocked) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
            contentDescription = null,
            tint = if (isUnlocked) EmeraldBright else GoldLight,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text =
              stringResource(
                id = if (isUnlocked) R.string.status_unlocked else R.string.status_locked
              ),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
          )
        }
      }

      Text(
        text = stringResource(id = R.string.portal_title),
        style = MaterialTheme.typography.headlineSmall,
        color = Color.White,
      )

      Text(
        text = stringResource(id = R.string.portal_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.88f),
      )
    }
  }
}
