package com.zumarad.clipstudio

import androidx.activity.result.PickVisualMediaRequest

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Bg = Color(0xFF080D18)
private val Panel = Color(0xFF111A27)
private val Cyan = Color(0xFF22D3EE)
private val Muted = Color(0xFF94A3B8)
private val White = Color(0xFFF1F5F9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZMotionApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZMotionApp() {
    val context = LocalContext.current
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    var videoName by remember { mutableStateOf("No video selected") }
    var projectName by remember { mutableStateOf("My first clip") }
    var storyText by remember { mutableStateOf("") }
    var startSeconds by remember { mutableStateOf("0") }
    var endSeconds by remember { mutableStateOf("30") }
    var status by remember { mutableStateOf("Ready — import a video to begin") }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedVideo = uri
            videoName = uri.lastPathSegment ?: "Selected video"
            status = "Video loaded. Preview it and set your clip range."
        }
    }

    val player = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(selectedVideo) {
        val uri = selectedVideo
        if (uri != null) {
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.playWhenReady = false
        } else {
            player.stop()
            player.clearMediaItems()
        }
        onDispose { }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Cyan,
            secondary = Color(0xFF9B8CFF),
            background = Bg,
            surface = Panel,
            onPrimary = Bg,
            onBackground = White,
            onSurface = White
        )
    ) {
        Scaffold(
            containerColor = Bg,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Z MOTION", color = White, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                            Text("CLIP STUDIO", color = Cyan, fontSize = 11.sp, letterSpacing = 2.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeaturePill("ANDROID V0.1")
                    FeaturePill("LOCAL PROJECT")
                    FeaturePill("AI: NOT CONNECTED")
                }

                CardBlock {
                    Text("PROJECT", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                    OutlinedTextField(
                        value = projectName,
                        onValueChange = { projectName = it },
                        label = { Text("Project name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Muted)
                        Text(videoName, color = Muted, fontSize = 12.sp, maxLines = 1)
                    }
                    Button(
                        onClick = { picker.launch(ActivityResultContracts.PickVisualMedia.VideoOnly) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Bg)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("IMPORT VIDEO", fontWeight = FontWeight.Bold)
                    }
                }

                CardBlock {
                    SectionTitle("VIDEO PREVIEW", "Preview imported source footage")
                    if (selectedVideo != null) {
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    this.player = player
                                    useController = true
                                }
                            },
                            update = { it.player = player },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .background(Color(0xFF080D18), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Movie, contentDescription = null, tint = Cyan, modifier = Modifier.size(42.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Your video preview appears here", color = Muted, fontSize = 13.sp)
                            }
                        }
                    }
                }

                CardBlock {
                    SectionTitle("QUICK CLIP RANGE", "Set an intended section in seconds")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = startSeconds,
                            onValueChange = { startSeconds = it.filter(Char::isDigit).take(6) },
                            label = { Text("Start (sec)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endSeconds,
                            onValueChange = { endSeconds = it.filter(Char::isDigit).take(6) },
                            label = { Text("End (sec)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Text(
                        "V0.1 note: range is currently a saved editing note. Actual trim/export engine is the next build step.",
                        color = Muted,
                        fontSize = 11.sp
                    )
                }

                CardBlock {
                    SectionTitle("STORY WORKSPACE", "Add your own angle and narration")
                    OutlinedTextField(
                        value = storyText,
                        onValueChange = { storyText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text("Write your story / voice-over") },
                        placeholder = { Text("Example: Jason ko andaza nahi tha ke aglay kuch seconds mein...") }
                    )
                    OutlinedButton(
                        onClick = { status = "Story saved in this session. AI recommendations will be connected in a later build." },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("SAVE STORY NOTE")
                    }
                }

                CardBlock {
                    SectionTitle("AI STORY RECOMMENDATIONS", "Coming in the AI integration phase")
                    Text(
                        "Whisper transcription, highlight detection, Gemma local story ideas and Gemini API are not connected in this starter build. No API key is required yet.",
                        color = Muted,
                        fontSize = 12.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FeaturePill("WHISPER: PENDING")
                        FeaturePill("GEMMA: PENDING")
                    }
                }

                Button(
                    onClick = {
                        val start = startSeconds.toLongOrNull()
                        val end = endSeconds.toLongOrNull()
                        status = when {
                            selectedVideo == null -> "Import a video first."
                            start == null || end == null || start < 0 || end <= start -> "Enter a valid clip range."
                            else -> "Clip range noted: ${start}s to ${end}s. Export is not implemented yet."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B8CFF), contentColor = Bg)
                ) {
                    Icon(Icons.Default.ContentCut, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("CHECK CLIP RANGE", fontWeight = FontWeight.Bold)
                }

                Text(status, color = Cyan, fontSize = 12.sp)
                Text(
                    "ZUMARAD STUDIO • PERSONAL EDITION • V0.1.0",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun FeaturePill(text: String) {
    Surface(color = Color(0xFF102637), shape = RoundedCornerShape(50)) {
        Text(text, color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Text(subtitle, color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun CardBlock(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}
