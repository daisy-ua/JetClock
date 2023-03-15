package com.daisy.jetclock.ui.component.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daisy.jetclock.ui.theme.AfricanViolet
import com.daisy.jetclock.ui.theme.AntiFlashWhite
import com.daisy.jetclock.ui.theme.PortGore
import com.daisy.jetclock.ui.theme.UltraViolet

@Composable
fun NextAlarmCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(Brush.linearGradient(
                    colors = listOf(
                        PortGore, UltraViolet, AfricanViolet
                    ),
                ))
                .padding(horizontal = 20.dp, vertical = 28.dp)
                .fillMaxWidth()
        ) {
            Column {
                Row {
                    Text(
                        text = "6:30",
                        fontWeight = FontWeight.Bold,
                        color = AntiFlashWhite,
                        fontSize = 34.sp
                    )
                    Text(
                        text = "AM",
                        fontSize = 26.sp,
                        color = AntiFlashWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Text(
                    text = "Ring in 6 days 15 hours 21 minutes.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AntiFlashWhite,
                    letterSpacing = (-1).sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(CenterHorizontally),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NextAlarmCardPreview() {
    NextAlarmCard()
}