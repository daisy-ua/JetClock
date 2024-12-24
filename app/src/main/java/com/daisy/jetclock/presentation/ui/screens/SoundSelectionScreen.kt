package com.daisy.jetclock.presentation.ui.screens

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daisy.jetclock.R
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.presentation.ui.component.components.TextRadioButtonRowItem
import com.daisy.jetclock.presentation.ui.component.scaffold.JetClockFuncTopAppBar
import com.daisy.jetclock.presentation.viewmodel.SoundSelectionViewModel

@Composable
fun SoundSelectionScreen(
    onUpClick: () -> Unit,
    soundFile: String?,
    onSoundSelected: (SoundOption) -> Unit,
    viewModel: SoundSelectionViewModel = hiltViewModel<SoundSelectionViewModel>(),
) {
    val sounds by viewModel.sounds.collectAsStateWithLifecycle()
    val selectedSoundOption by viewModel.selectedSoundOption.collectAsStateWithLifecycle()

    LaunchedEffect(soundFile) {
        viewModel.updateSelectedSoundOption(SoundOption(soundFile))
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        viewModel.attachLifecycle(lifecycleOwner.lifecycle)
    }

    fun onApplyChanges() {
        onSoundSelected(selectedSoundOption)
        onUpClick()
    }

    SoundSelectionScreenContent(
        sounds = sounds,
        isSoundSelected = { current -> current == selectedSoundOption },
        onSoundClicked = viewModel::onSoundClicked,
        onApply = ::onApplyChanges,
        onUpClick = onUpClick
    )
}

@Composable
fun SoundSelectionScreenContent(
    sounds: List<SoundOption>,
    isSoundSelected: (SoundOption) -> Boolean,
    onSoundClicked: (SoundOption) -> Unit,
    onApply: () -> Unit,
    onUpClick: () -> Unit,
) {
    val noSoundOption = remember { SoundOption.none }

    Scaffold(
        topBar = {
            JetClockFuncTopAppBar(
                title = stringResource(id = R.string.select_sound),
                onClose = onUpClick,
                onApply = onApply
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                TextRadioButtonRowItem(
                    name = SoundOption.NONE,
                    isSelected = isSoundSelected(noSoundOption),
                    onItemClick = { onSoundClicked(noSoundOption) }
                )
            }

            item { TitleItem() }

            items(sounds) { sound ->
                TextRadioButtonRowItem(
                    name = sound.displayName,
                    isSelected = isSoundSelected(sound),
                    onItemClick = { onSoundClicked(sound) }
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
            text = stringResource(id = R.string.ringtones_category_classic),
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}