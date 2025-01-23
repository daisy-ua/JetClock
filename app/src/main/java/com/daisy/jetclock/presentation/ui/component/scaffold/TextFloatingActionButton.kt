package com.daisy.jetclock.presentation.ui.component.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Button(
        modifier = Modifier
            .then(modifier)
            .drawColoredShadow(
                color = shadowColor,
                alpha = 0.2f,
                borderRadius = 10.dp,
                offsetY = 8.dp,
            ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = null,
        onClick = onItemClick
    ) {
        Text(
            text = actionText.uppercase(),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
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