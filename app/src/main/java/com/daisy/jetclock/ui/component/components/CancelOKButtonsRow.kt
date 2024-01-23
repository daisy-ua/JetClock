package com.daisy.jetclock.ui.component.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.component.scaffold.TextFloatingActionButton

@Composable
fun CancelOKButtonsRow(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    onSubmitRequest: () -> Unit,
) {
    val shadowColor = MaterialTheme.colors.onSurface.copy(1f, .5f, .5f, .5f)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextFloatingActionButton(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            actionText = "Cancel",
            backgroundColor = MaterialTheme.colors.onBackground.copy(.3f),
            shadowColor = shadowColor,
            onItemClick = onDismissRequest
        )

        TextFloatingActionButton(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            actionText = "OK",
            backgroundColor = MaterialTheme.colors.primary,
            shadowColor = shadowColor,
            onItemClick = onSubmitRequest
        )
    }
}