package com.daisy.jetclock.ui.component.alarm


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AlarmCard() {
    val context = LocalContext.current

    val checkedState = remember {
        mutableStateOf(true)
    }

    Surface(
        modifier = Modifier
            .clickable {
                Toast
                    .makeText(
                        context,
                        "Alarm item clicked!",
                        Toast.LENGTH_LONG)
                    .show()
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Column {
                Row {
                    Text(
                        text = "6:30",
                        fontWeight = FontWeight.Medium,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "AM",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    Text(
                        text = "Alarm",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Mon Tue Wed",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        color = Color.DarkGray
                    )
                }
            }

            Switch(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmCardPreview() {
    AlarmCard()
}