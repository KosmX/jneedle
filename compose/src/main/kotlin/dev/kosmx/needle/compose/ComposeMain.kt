package dev.kosmx.needle.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
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
        targetFolder = path ?: targetFolder
    }

    MaterialTheme {
        Column(horizontalAlignment = Alignment.End){
            //chosen folder as text
            TextField(
                targetFolder, onValueChange = {}, readOnly = true,
                singleLine = true, textStyle = TextStyle(fontSize = TextUnit(20f, TextUnitType.Sp)),
                modifier = Modifier
                    .background(
                        MaterialTheme.colors.surface,
                        RoundedCornerShape(percent = 50)
                    )
                    .padding(10.dp, 10.dp, 10.dp, 5.dp)
                    .fillMaxWidth(1f)
            )
            //row of function buttons
            Row {
                LocalTextStyle provides TextStyle()
                Button(
                    modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 5.dp),
                    onClick = { showDirectoryPicker = true}) {
                    Text("Select folder")
                }
                Button(colors = ButtonDefaults.buttonColors(Color(85, 187, 47), Color(255,255,255)),
                    modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 5.dp),
                    onClick = {}) {//TODO: call into API
                    Text("Confirm")
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}