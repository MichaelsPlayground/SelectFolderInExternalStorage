package de.androidcrypto.selectfolderinexternalstorage;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private final String TAG = "MainSelectExtFolder";
    Button selectFolder, listFiles;
    Button grantPermissions;
    TextView selectedFolder, listedFiles, selectFolderProvider;
    String selectedFolderFromIntent, parentFolderFromIntent;

    private final int REQUEST_CODE_SELECT_FOLDER = 100;

    Uri selectedFolderUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectFolder = findViewById(R.id.btnSelectFolder);
        selectedFolder = findViewById(R.id.tvSelectedFolder);
        selectFolderProvider = findViewById(R.id.btnSelectFolderProvider);

        listFiles = findViewById(R.id.btnListFiles);
        listedFiles = findViewById(R.id.tvListFiles);

        grantPermissions = findViewById(R.id.btnGrantPermissions);

        Bundle extras = getIntent().getExtras();
        System.out.println("get bundles");
        if (extras != null) {
            System.out.println("extras not null");
            selectedFolderFromIntent = (String) getIntent().getSerializableExtra("browsedFolder");
            parentFolderFromIntent = (String) getIntent().getSerializableExtra("parentFolder");
            if (parentFolderFromIntent != null) {
                Log.i(TAG, "parent folder: " + parentFolderFromIntent);
            }
            if (selectedFolderFromIntent != null) {
                Log.i(TAG, "received folder: " + selectedFolderFromIntent);
                System.out.println("folder not null");
                //folderFromListFolder = folder;
                System.out.println("ListFolder: " + selectedFolderFromIntent);
                // todo do what has todo when folder is selected
                //listFiles.setVisibility(View.GONE);
                //listFolder(getBaseContext(), folder);
                String resultString = "selectedFolder: " + selectedFolderFromIntent + "\n"
                        + "parentFolder: " + parentFolderFromIntent;
                Log.i(TAG, "resultString: " + resultString);
                selectedFolder.setText(resultString);

                // get files from selected folder
                listLocalFiles(getApplicationContext(), selectedFolderFromIntent);
            }
        }


        selectFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "selectFolder");
                selectedFolder.setText("no folder selected so far");

                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(i, "Choose directory"), REQUEST_CODE_SELECT_FOLDER);
            }
        });

        selectFolderProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "selectFolderProvider");

                Intent intent = new Intent(MainActivity.this, BrowseFolder.class);
                startActivity(intent);
                //finish();

            }
        });

        listFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "listFiles");
                if (selectedFolderUri == null) {
                    Log.e(TAG, "select a folder first before listing files");
                    return;
                }

                // https://commonsware.com/blog/2016/03/15/how-consume-content-uri.html

                System.out.println("* selectedFolderUri: " + selectedFolderUri);

                String path = getPath(view.getContext(), selectedFolderUri);
                System.out.println("* path: " + path);
                File localFolder = new File(path);
                System.out.println("* localFolder: " + localFolder.getAbsolutePath());

                //Creating a File object for directory
                File directoryPath = new File(selectedFolderUri.getPath());
                File[] storageFiles;
                storageFiles = localFolder.listFiles();
                if (storageFiles == null) {
                    Log.e(TAG, "no files found");
                    return;
                }
                System.out.println("storageFiles size: " + storageFiles.length);

                File[] externalFilesDirs = ContextCompat.getExternalFilesDirs(view.getContext(), selectedFolderUri.getPath());
                List<File> externalDirectories = new ArrayList<>();

                Set<String> stringSet = new HashSet<>();

                for (File file : externalFilesDirs) {
                    String[] split = file.getAbsolutePath().split("/");
                    if (split.length > 1) {
                        stringSet.add(split[1]);
                    }
                    System.out.println("file: " + file.getAbsolutePath());
                }

                for (String str : stringSet) {
                    externalDirectories.add(new File(str));
                    System.out.println("add: " + str);
                }
                System.out.println("* externalDirectories.size: " + externalDirectories.size());
            }
        });

        grantPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        startActivity(new Intent(view.getContext(), MainActivity.class));
                    } else { //request for the permission
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                } else {
                    //below android 11=======
                    startActivity(new Intent(view.getContext(), MainActivity.class));
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            }
        });
    }

    // https://stackoverflow.com/a/46889812/8166854
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = SDK_INT >= Build.VERSION_CODES.KITKAT;

        boolean isDocUri = DocumentsContract.isDocumentUri(context, uri);
        System.out.println("* isDocUri: " + isDocUri);

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            System.out.println("* isKitKat && DocumentsContract.isDocumentUri");
            System.out.println("getPath() uri: " + uri.toString());
            System.out.println("getPath() uri authority: " + uri.getAuthority());
            System.out.println("getPath() uri path: " + uri.getPath());

            // ExternalStorageProvider
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                System.out.println("getPath() docId: " + docId + ", split: " + split.length + ", type: " + type);

                // This is for checking Main Memory
                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1] + "/";
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                    // This is for checking SD Card
                } else {
                    return "storage" + "/" + docId.replace(":", "/");
                }

            }
        } else {
            System.out.println("* is NOT KitKat && DocumentsContract.isDocumentUri");
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case REQUEST_CODE_SELECT_FOLDER:
                Uri resultUri = data.getData();
                Log.i("Test", "Result URI " + resultUri);
                selectedFolder.setText("selectedFolder: " + resultUri);
                // Result URI content://com.android.externalstorage.documents/tree/primary%3AGdtest%2FTest1
                // get persistant access
                /*
                getContentResolver().takePersistableUriPermission(resultUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                */
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                // Check for the freshest data.
                getContentResolver().takePersistableUriPermission(resultUri, takeFlags);

                Log.i(TAG, "persistant access granted to URI: " + resultUri);
                selectedFolderUri = resultUri;
                break;
        }
    }


    /**
     * section for local files and folders
     */

    private void listLocalFiles(Context context, String startDirectory) {
        Log.i(TAG, "listLocalFiles startDirectory: " + startDirectory);
        String recursiveFolder = parentFolderFromIntent.replaceFirst("root", "");
        File externalStorageDir = new File(Environment.getExternalStoragePublicDirectory("")
                + recursiveFolder, startDirectory);
        File[] files = externalStorageDir.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String separatorString = "------------------------------------------------------------------\n";
        sb.append("files found in local folder:\n");
        sb.append(separatorString);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    // show nothing
                    fileNames.add(files[i].getName());
                    sb.append(files[i].getName()).append("\n");
                    sb.append(separatorString);
                } else {
                    // show nothing
                    //fileNames.add((files[i].getName()));
                }
            }
            listedFiles.setText(sb.toString());
        }
    }
}