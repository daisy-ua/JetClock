package com.daisy.jetclock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.daisy.jetclock.ui.component.components.TextRadioButtonRowItem
import com.daisy.jetclock.ui.component.scaffold.JetClockFuncTopAppBar
import com.daisy.jetclock.ui.theme.JetClockTheme
import com.daisy.jetclock.utils.SoundPoolManager
import com.daisy.jetclock.utils.rememberLifecycleEvent

@Composable
fun SelectSoundScreen() {
    val context = LocalContext.current

    val sounds = rememberSaveable {
        context.assets.list("sounds")
            ?.map { it.split(".")[0] }?.toList() ?: listOf()
    }

    val soundSelected = rememberSaveable { mutableStateOf<String?>(null) }

    val soundPoolManager = remember { SoundPoolManager() }

    val lifecycleEvent = rememberLifecycleEvent()

    LaunchedEffect(lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_PAUSE) {
            soundPoolManager.stopSound()
        }
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            soundPoolManager.release()
        }
    }

    Scaffold(
        topBar = { JetClockFuncTopAppBar(title = "Select sound") },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                TextRadioButtonRowItem(
                    name = "None",
                    isSelected = soundSelected.value?.equals("None") ?: true,
                    onItemClick = {
                        soundSelected.value = null
                        soundPoolManager.stopSound()
                    }
                )
            }

            item { TitleItem() }

            items(sounds) { sound ->
                TextRadioButtonRowItem(
                    name = sound,
                    isSelected = soundSelected.value?.equals(sound) ?: false,
                    onItemClick = {
                        soundSelected.value = sound

                        val soundAsset = context.assets.openFd("sounds/$sound.mp3")
                        soundPoolManager.let { manager ->
                            manager.changeSound(soundAsset)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TitleItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colors.background.copy(.2f, .8f, .8f, .8f))
    ) {
        Text(
            text = "CLASSIC RINGTONES",
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectSoundScreenPreview() {
    JetClockTheme(darkTheme = false) {
        SelectSoundScreen()
    }
}