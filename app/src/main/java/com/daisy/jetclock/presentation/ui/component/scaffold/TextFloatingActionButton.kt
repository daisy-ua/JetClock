package com.daisy.jetclock.presentation.ui.component.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daisy.jetclock.presentation.ui.theme.JetClockTheme


@Composable
fun TextFloatingActionButton(
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionText: String = "Delete",
    backgroundColor: Color = MaterialTheme.colors.error,
    shadowColor: Color = MaterialTheme.colors.error,
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .drawColoredShadow(
                color = shadowColor,
                alpha = 0.2f,
                borderRadius = 10.dp,
                offsetY = 8.dp,
            )
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .clickable { onItemClick() }
            .padding(horizontal = 30.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = actionText.uppercase(),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SetAlarmScreenPreview() {
    JetClockTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            TextFloatingActionButton({})
        }
    }
}