package com.daisy.jetclock.ui.component.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ListRowComponent(
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 20.dp,
    content: @Composable () -> Unit,
) {
    Card(
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(0.dp),
        elevation = 4.dp,
        modifier = modifier
            .clickable { onItemClick.invoke() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = verticalPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}