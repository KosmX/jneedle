package dev.kosmx.needle.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker

@Preview
@Composable
fun App() {
    var showDirectoryPicker by remember { mutableStateOf(false) }
    var targetFolder by remember { mutableStateOf("no folder!") }

    DirectoryPicker(showDirectoryPicker) { path ->
        showDirectoryPicker = false
        targetFolder = path ?: "no folder!"
    }

    MaterialTheme {
        Button(onClick = {
            showDirectoryPicker = true
        }) {
            Text(targetFolder)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}