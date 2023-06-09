package dev.kosmx.needle.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import dev.kosmx.needle.CheckWrapper
import dev.kosmx.needle.ScanResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.jvm.internal.Ref.IntRef

object ComposeMain {
    private var foundFilesResult by mutableStateOf(listOf<ScanResult>())
    private var loggerText by mutableStateOf("")
    private var scanRunnable = true
    private val scrollState by mutableStateOf(ScrollState(0))
    private const val fontSize = 20f

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
                    singleLine = true, textStyle = TextStyle(fontSize = TextUnit(fontSize, TextUnitType.Sp)),
                    label = { Text(text = "Choose a file/folder") },
                    placeholder = { Text(text = "C:\\") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        errorBorderColor = Red
                    ),
                    isError = !validTarget,
                    enabled = scanRunnable,
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
                    Button(colors = ButtonDefaults.buttonColors(Color(0.4f, 0.4f, 0.4f)),
                        modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 5.dp),
                        onClick = {
                            loggerText = ""
                            log("Logs cleared")
                        }) {
                        Text("Clear logs")
                    }
                    Button(
                        modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 5.dp),
                        onClick = { showDirectoryPicker = true },
                        enabled = scanRunnable
                    ) {
                        Text("Select folder")
                    }
                    Button(
                        modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 5.dp),
                        onClick = { showFilePicker = true },
                        enabled = scanRunnable
                    ) {
                        Text("Select file")
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(Color(85, 187, 47), Color(1f, 1f, 1f)),
                        modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 5.dp),
                        onClick = {
                            callAPIChecker(Path(targetPath))
                        },
                        enabled = scanRunnable
                    ) {
                        Text("Scan")
                    }
                }
                TextField(
                    loggerText, onValueChange = {

                    }, readOnly = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(0.9f, 0.9f, 0.9f),
                        focusedIndicatorColor = Transparent, unfocusedIndicatorColor = Transparent
                    ),
                    modifier = Modifier.padding(10.dp).fillMaxWidth(1f).fillMaxHeight(1f)
                        .verticalScroll(scrollState, reverseScrolling = true)
                        .background(
                            MaterialTheme.colors.surface,
                            RoundedCornerShape(percent = 50)
                        )
                )
            }
        }
    }

    private fun callAPIChecker(path: Path) {
        val infectedCallback: ((ScanResult) -> Unit) = {
            //if any infections are found (just to make sure)
            if (it.second.isNotEmpty()) {
                //log them all
                log(buildString {
                    append("Detected the following in ${it.first.name}: ")
                    it.second.forEach { infection ->
                        append("${infection.getMessage()} ")
                    }
                })
            }
        }

        //start a scan with logging at the start and end
        CoroutineScope(Dispatchers.Default).launch {
            //extra cautious check to avoid hogging resources with multiple scans
            if (path.toFile().exists() && scanRunnable) {
                scanRunnable = false
                log("Scan started on $path")

                val count = IntRef()
                foundFilesResult = CheckWrapper.checkPath(
                    path,
                    jarVisitCallback = infectedCallback, scannedCount = count
                )

                scanRunnable = true
                log(
                    "Scan finished, $count ${if (count.element == 1) "file was" else "files were"} tested, " +
                            "${foundFilesResult.count()} malicious file(s) found"
                )
            } else {
                log("Error! The most likely cause is an invalid path")
            }
        }
    }

    private fun log(s: String) {
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
        if (loggerText.isNotEmpty()) loggerText += '\n'
        loggerText += "[$formattedLogTime] - $s"
        CoroutineScope(Dispatchers.Default).launch {
            scrollState.scrollTo(0)
        }
    }
}

fun main() = application {
    Window(
        state = WindowState(width = 1000.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
        title = "JNeedle Malware Detection Tool"
    ) {
        ComposeMain.App()
    }
}