package com.daisy.jetclock.presentation.ui.component.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.R
import com.daisy.jetclock.presentation.ui.component.scaffold.TextFloatingActionButton

@Composable
fun CancelOKButtonsRow(
    modifier: Modifier,
    @StringRes cancelAction: Int = R.string.cancel_action,
    @StringRes okAction: Int = R.string.ok_action,
    onDismissRequest: () -> Unit,
    onSubmitRequest: () -> Unit,
) {
    val shadowColor = MaterialTheme.colors.onSurface.copy(1f, .5f, .5f, .5f)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextFloatingActionButton(
            modifier = Modifier
                .weight(1f),
            actionText = stringResource(id = cancelAction),
            backgroundColor = MaterialTheme.colors.onBackground.copy(.3f),
            shadowColor = shadowColor,
            onItemClick = onDismissRequest
        )

        TextFloatingActionButton(
            modifier = Modifier
                .weight(1f),
            actionText = stringResource(id = okAction),
            backgroundColor = MaterialTheme.colors.primary,
            shadowColor = shadowColor,
            onItemClick = onSubmitRequest
        )
    }
}