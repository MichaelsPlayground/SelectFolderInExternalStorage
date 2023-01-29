# Select folder in external/shared storage

This app shows a folder picker for external/shared storage. It starts in the root of the device and 
recursivly walks through folders and subfolders.

For selection press the button on top "select folder ROOT/..." and the MainActivity is called using an 
intent and the selected folder and parent folder are given through the internet bundle. When the MainActivity 
receives the bundle it will list all files in the selected folder.

**Important note**: do not forget to press the button "GRANT PERMISSIONS" and provide the "ALL FILES" permission to 
the app, otherwise the app cannot list the files in the selected folder. This permission needs to set only once.

AndroidManifest.xml:
```plaintext
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
```

The project was tested on an emulator device running Android 13 / SDK 33.
