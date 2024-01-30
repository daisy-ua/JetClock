package com.daisy.jetclock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.domain.SoundOption
import com.daisy.jetclock.ui.component.components.TextRadioButtonRowItem
import com.daisy.jetclock.ui.component.scaffold.JetClockFuncTopAppBar
import com.daisy.jetclock.ui.theme.JetClockTheme
import com.daisy.jetclock.utils.rememberLifecycleEvent
import com.daisy.jetclock.viewmodels.SelectedSoundViewModel
import com.daisy.jetclock.viewmodels.SoundPoolViewModel

@Composable
fun SelectSoundScreen(
    onUpClick: () -> Unit,
    viewModel: SelectedSoundViewModel = hiltViewModel<SelectedSoundViewModel>(),
    soundViewModel: SoundPoolViewModel = hiltViewModel<SoundPoolViewModel>(),
) {
    val context = LocalContext.current

    val sounds = rememberSaveable {
        context.assets.list(ConfigConstants.SOUND_ASSETS_DIR)?.toList() ?: listOf()
    }

    val soundNames = rememberSaveable {
        sounds.map { SoundOption.getSoundName(it)!! }
    }

    var soundSelectedIndex by rememberSaveable {
        mutableIntStateOf(sounds.indexOf(viewModel.selectedSound.value.soundFile))
    }

    val lifecycleEvent = rememberLifecycleEvent()

    LaunchedEffect(lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_PAUSE) {
            soundViewModel.stopSound()
        }
    }

    fun onApplySound() {
        val soundFile = if (soundSelectedIndex == -1) null
        else sounds[soundSelectedIndex]

        viewModel.updateSelectedSound(soundFile)
        onUpClick()
    }

    fun onPlaySound(index: Int, sound: String) {
        soundSelectedIndex = index
        soundViewModel.playSound(context, sound)
    }

    Scaffold(
        topBar = {
            JetClockFuncTopAppBar(
                title = "Select sound",
                onClose = onUpClick,
                onApply = ::onApplySound
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                TextRadioButtonRowItem(
                    name = SoundOption.NONE,
                    isSelected = soundSelectedIndex == -1,
                    onItemClick = {
                        soundSelectedIndex = -1
                        soundViewModel.stopSound()
                    }
                )
            }

            item { TitleItem() }

            itemsIndexed(soundNames) { index, sound ->
                TextRadioButtonRowItem(
                    name = sound,
                    isSelected = soundSelectedIndex == index,
                    onItemClick = { onPlaySound(index, sound) }
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
//        SelectSoundScreen({})
    }
}