# Z MOTION CLIP STUDIO — V0.2.0
Personal-use Android source project; this ZIP is not a prebuilt APK.

## V0.2.0
- Save story notes locally and restore them on app restart
- Save project name, clip range and last selected video URI
- Save Project and New Project actions
- New adaptive Android launcher icon
- App version 0.2.0

## Existing
- Android Photo Picker video import
- Media3 ExoPlayer preview
- Clip range fields (validation/note only)

## Not implemented
- Actual video trimming or MP4 export
- 9:16 crop/render pipeline
- Whisper, OpenCV, Gemma or Gemini AI
- Multiple project database/library

Build with JDK 17, Android SDK 35 and Gradle 8.9:
`gradle assembleDebug`
APK: `app/build/outputs/apk/debug/app-debug.apk`
