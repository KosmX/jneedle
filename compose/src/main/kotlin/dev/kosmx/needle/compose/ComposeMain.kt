package dev.kosmx.needle.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import dev.kosmx.needle.CheckWrapper
import dev.kosmx.needle.ScanResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

object ComposeMain {
    private var foundFilesResult by mutableStateOf(listOf<ScanResult>())
    private var loggerText by mutableStateOf("")

    @Preview
    @Composable
    fun App() {

        var showDirectoryPicker by remember { mutableStateOf(false) }
        var showFilePicker by remember { mutableStateOf(false) }
        var targetPath by remember { mutableStateOf("C:\\") }
        var validTarget by remember { mutableStateOf(true) }
        CheckWrapper.init()

        FilePicker(showFilePicker) { path ->
            showFilePicker = false
            targetPath = path?.path ?: targetPath
            validTarget = File(targetPath).exists()
        }

        DirectoryPicker(showDirectoryPicker) { path ->
            showDirectoryPicker = false
            targetPath = path ?: targetPath
            validTarget = File(targetPath).exists()
        }

        MaterialTheme {
            Column(horizontalAlignment = Alignment.End) {

                //chosen folder/file as text
                OutlinedTextField(
                    targetPath, onValueChange = {
                        targetPath = it
                        validTarget = File(targetPath).exists()
                    },
                    singleLine = true, textStyle = TextStyle(fontSize = TextUnit(20f, TextUnitType.Sp)),
                    label = { Text(text = "Choose a file/folder") },
                    placeholder = { Text(text = "C:\\") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        errorBorderColor = Red
                    ),
                    isError = !validTarget,
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
                    if (!validTarget) {
                        Text(
                            modifier = Modifier.padding(top = 15.dp, end = 15.dp),
                            text = "Invalid folder/file",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.error
                        )
                    }
                    LocalTextStyle provides TextStyle()
                    Button(
                        modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 5.dp),
                        onClick = { showDirectoryPicker = true }) {
                        Text("Select folder")
                    }
                    Button(
                        modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 5.dp),
                        onClick = { showFilePicker = true }) {
                        Text("Select file")
                    }
                    Button(colors = ButtonDefaults.buttonColors(Color(85, 187, 47), Color(1f, 1f, 1f)),
                        modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 5.dp),
                        onClick = {
                            callAPIChecker(Path(targetPath))
                        }) {
                        Text("Confirm")
                    }
                }
                TextField(
                    loggerText, onValueChange = {}, readOnly = true,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(0.9f, 0.9f, 0.9f)),
                    modifier = Modifier.padding(10.dp).fillMaxWidth(1f).fillMaxHeight(1f)
                )
            }
        }
    }

    private fun callAPIChecker(path: Path) {
        CoroutineScope(Dispatchers.Default).launch {
            if (path.toFile().let { it.isFile || it.isDirectory })
                log("Scan started on $path")
                foundFilesResult = CheckWrapper.checkPath(path)
                log("Scan finished")
        }
    }
    private fun log(s: String){
        val logTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val formattedLogTime = logTime.let {
            buildString {
                append(it.hour.toString().padStart(2, '0'))
                append(":")
                append(it.minute.toString().padStart(2, '0'))
                append(":")
                append(it.second.toString().padStart(2, '0'))
            }
        }
        loggerText += "[$formattedLogTime] - $s\n"
    }

}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        ComposeMain.App()
    }
}