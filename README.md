# Z MOTION CLIP STUDIO — Android V0.1

Personal-use Android starter project. This is source code, not a prebuilt APK.

## Included in this starter
- Premium dark + electric cyan UI
- Gallery video picker using Android Photo Picker
- Video preview using AndroidX Media3 ExoPlayer
- Project name field
- Clip start/end range input (currently a note/validation only)
- Story / voice-over text workspace
- AI integration status placeholders

## Not connected yet
- Whisper.cpp transcription
- OpenCV scene detection
- Gemma local story model
- Gemini API
- Actual clip trimming and MP4 export
- Persistent project database

These are intentionally marked as pending. No API key is needed for this starter.

## Build
Requirements:
- JDK 17
- Android SDK Platform 35
- Gradle 8.9
- Android device with Android 8.0 or newer

From the project root:
```bash
gradle assembleDebug
```
APK output:
`app/build/outputs/apk/debug/app-debug.apk`

You can also push this project to GitHub and use the included Actions workflow to build the APK.

## GitHub Actions
1. Create a GitHub repository.
2. Upload the contents of this folder.
3. Open the Actions tab.
4. Run `Build Android APK`.
5. Download the `Z-MOTION-CLIP-STUDIO-debug-apk` artifact.

## Notes
- Android Photo Picker is used, so broad storage permission is not requested.
- Media3 dependencies are downloaded from Google's Maven repository during build.
- This starter has not been built or device-tested in this environment.
- For personal use, install the generated debug APK directly. Play Store publishing is not required.
