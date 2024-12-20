package com.daisy.jetclock.presentation.ui.component.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.daisy.jetclock.presentation.ui.component.components.ListRowComponent

@Composable
fun TextRadioButtonRowItem(
    name: String,
    isSelected: Boolean,
    onItemClick: () -> Unit = { },
) {
    ListRowComponent(
        onItemClick = onItemClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium
        )

        RadioButton(
            selected = isSelected,
            onClick = null,
        )
    }
}