package com.daisy.jetclock.presentation.ui.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.R

@Composable
fun DismissButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black.copy(alpha = 0.7f),
    iconSize: Dp = 30.dp,
    iconTint: Color = Color.White,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(50.dp)
            )
            .size(60.dp)
            .run {
                if (onClick != null) clickable(onClick = onClick) else this
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = Icons.Rounded.Close,
            contentDescription = stringResource(id = R.string.dismiss_alarm_description),
            tint = iconTint
        )
    }
}