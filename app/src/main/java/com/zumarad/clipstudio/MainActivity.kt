package com.zumarad.clipstudio

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

private val Bg=Color(0xFF080D18)
private val Panel=Color(0xFF111A27)
private val Cyan=Color(0xFF22D3EE)
private val Muted=Color(0xFF94A3B8)
private val White=Color(0xFFF1F5F9)

class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{ZMotionApp()}}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZMotionApp(){
 val context= LocalContext.current
 val prefs= remember(context){context.getSharedPreferences("z_motion_project_v02",Context.MODE_PRIVATE)}
 var selectedVideo by remember{mutableStateOf(prefs.getString("video_uri",null)?.let(Uri::parse))}
 var videoName by remember{mutableStateOf(prefs.getString("video_name","No video selected")?:"No video selected")}
 var projectName by remember{mutableStateOf(prefs.getString("project_name","My first clip")?:"My first clip")}
 var storyText by remember{mutableStateOf(prefs.getString("story_text","")?:"")}
 var startSeconds by remember{mutableStateOf(prefs.getString("start_seconds","0")?:"0")}
 var endSeconds by remember{mutableStateOf(prefs.getString("end_seconds","30")?:"30")}
 var status by remember{mutableStateOf("Project ready — save your changes on this device")}
 var storyFeedback by remember{mutableStateOf("")}
 val picker= rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->
  if(uri!=null){
   try{context.contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)}catch(_:SecurityException){}
   selectedVideo=uri;videoName=uri.lastPathSegment?:"Selected video";status="Video selected. Tap SAVE PROJECT."
  }
 }
 val player= remember{ExoPlayer.Builder(context).build()}
 DisposableEffect(selectedVideo){
  if(selectedVideo!=null){try{player.setMediaItem(MediaItem.fromUri(selectedVideo!!));player.prepare();player.playWhenReady=false}catch(_:Exception){status="Saved video unavailable. Import it again."}}
  else{player.stop();player.clearMediaItems()}
  onDispose{}
 }
 DisposableEffect(Unit){onDispose{player.release()}}
 fun saveProject(){
  prefs.edit().putString("project_name",projectName).putString("story_text",storyText)
   .putString("start_seconds",startSeconds).putString("end_seconds",endSeconds)
   .putString("video_uri",selectedVideo?.toString()).putString("video_name",videoName).apply()
  status="Project saved on this device."
 }
 MaterialTheme(colorScheme=darkColorScheme(primary=Cyan,secondary=Color(0xFF9B8CFF),background=Bg,surface=Panel,onPrimary=Bg,onBackground=White,onSurface=White)){
  Scaffold(containerColor=Bg,topBar={TopAppBar(title={Column{
   Text("Z MOTION",color=White,fontWeight=FontWeight.Black,letterSpacing=1.5.sp)
   Text("CLIP STUDIO • V0.2.0",color=Cyan,fontSize=11.sp,letterSpacing=1.5.sp)
  }},colors=TopAppBarDefaults.topAppBarColors(containerColor=Bg))}){padding->
   Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal=16.dp,vertical=8.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Pill("ANDROID V0.2");Pill("LOCAL PROJECT");Pill("AI: PENDING")}
    Block{
     Heading("PROJECT","Save and restore your current edit")
     OutlinedTextField(projectName,{projectName=it},label={Text("Project name")},singleLine=true,modifier=Modifier.fillMaxWidth())
     Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
      Icon(Icons.Default.FolderOpen,null,tint=Muted);Text(videoName,color=Muted,fontSize=12.sp,maxLines=1)
     }
     Button(onClick={picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))},modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Cyan,contentColor=Bg)){
      Icon(Icons.Default.Add,null);Spacer(Modifier.width(8.dp));Text("IMPORT VIDEO",fontWeight=FontWeight.Bold)
     }
     Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
      OutlinedButton(onClick={saveProject()},modifier=Modifier.weight(1f)){Icon(Icons.Default.Save,null);Spacer(Modifier.width(5.dp));Text("SAVE PROJECT")}
      OutlinedButton(onClick={
       selectedVideo=null;videoName="No video selected";projectName="New project";storyText="";startSeconds="0";endSeconds="30";storyFeedback=""
       prefs.edit().clear().apply();status="New empty project created."
      },modifier=Modifier.weight(1f)){Icon(Icons.Default.Refresh,null);Spacer(Modifier.width(5.dp));Text("NEW PROJECT")}
     }
    }
    Block{
     Heading("VIDEO PREVIEW","Preview imported source footage")
     if(selectedVideo!=null) AndroidView(factory={ctx->PlayerView(ctx).apply{this.player=player;useController=true}},modifier=Modifier.fillMaxWidth().height(210.dp))
     else Box(Modifier.fillMaxWidth().height(180.dp).background(Bg,RoundedCornerShape(14.dp)),contentAlignment=Alignment.Center){
      Column(horizontalAlignment=Alignment.CenterHorizontally){Icon(Icons.Default.Movie,null,tint=Cyan,modifier=Modifier.size(40.dp));Text("Your video preview appears here",color=Muted)}
     }
    }
    Block{
     Heading("QUICK CLIP RANGE","Set an intended section in seconds")
     Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
      OutlinedTextField(startSeconds,{startSeconds=it.filter(Char::isDigit).take(6)},label={Text("Start (sec)")},modifier=Modifier.weight(1f),singleLine=true)
      OutlinedTextField(endSeconds,{endSeconds=it.filter(Char::isDigit).take(6)},label={Text("End (sec)")},modifier=Modifier.weight(1f),singleLine=true)
     }
     Text("V0.2: values save locally. Actual trimming/export is not implemented yet.",color=Muted,fontSize=11.sp)
    }
    Block{
     Heading("STORY WORKSPACE","Your narration stays on this device")
     OutlinedTextField(storyText,{storyText=it;storyFeedback=""},modifier=Modifier.fillMaxWidth(),minLines=3,label={Text("Write your story / voice-over")})
     OutlinedButton(onClick={
      if(storyText.isBlank())storyFeedback="Pehle apni story likho."
      else{prefs.edit().putString("story_text",storyText).apply();storyFeedback="Story successfully saved on this device."}
     },modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.AutoAwesome,null);Spacer(Modifier.width(8.dp));Text("SAVE STORY NOTE")}
     if(storyFeedback.isNotBlank())Text(storyFeedback,color=Cyan,fontSize=12.sp)
    }
    Block{
     Heading("AI STORY RECOMMENDATIONS","Coming in a later phase")
     Text("Whisper, OpenCV, Gemma and Gemini are not connected in this version.",color=Muted,fontSize=12.sp)
     Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Pill("WHISPER: PENDING");Pill("GEMMA: PENDING")}
    }
    Button(onClick={
     val s=startSeconds.toLongOrNull();val e=endSeconds.toLongOrNull()
     status=when{selectedVideo==null->"Import a video first.";s==null||e==null||s<0||e<=s->"Enter a valid clip range.";else->"Clip range noted: ${s}s to ${e}s. Export is not implemented yet."}
    },modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF9B8CFF),contentColor=Bg)){
     Icon(Icons.Default.ContentCut,null);Spacer(Modifier.width(8.dp));Text("CHECK CLIP RANGE",fontWeight=FontWeight.Bold)
    }
    Text(status,color=Cyan,fontSize=12.sp)
    Text("ZUMARAD STUDIO • PERSONAL EDITION • V0.2.0",color=Color(0xFF64748B),fontSize=10.sp,modifier=Modifier.align(Alignment.CenterHorizontally).padding(bottom=18.dp))
   }
  }
 }
}
@Composable private fun Pill(t:String){Surface(color=Color(0xFF102637),shape=RoundedCornerShape(50)){Text(t,color=Cyan,fontSize=9.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(horizontal=9.dp,vertical=6.dp))}}
@Composable private fun Heading(t:String,s:String){Column(verticalArrangement=Arrangement.spacedBy(3.dp)){Text(t,color=Cyan,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=1.2.sp);Text(s,color=Muted,fontSize=11.sp)}}
@Composable private fun Block(content:@Composable ColumnScope.()->Unit){Card(colors=CardDefaults.cardColors(containerColor=Panel),shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(12.dp),content=content)}}
