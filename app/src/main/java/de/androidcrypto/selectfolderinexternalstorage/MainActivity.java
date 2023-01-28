package de.androidcrypto.selectfolderinexternalstorage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainSelectExtFolder";
    Button selectFolder, listFiles;
    TextView selectedFolder, listedFiles;

    private final int REQUEST_CODE_SELECT_FOLDER = 100;

    Uri selectedFolderUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectFolder = findViewById(R.id.btnSelectFolder);
        selectedFolder = findViewById(R.id.tvSelectedFolder);
        listFiles = findViewById(R.id.btnListFiles);
        listedFiles = findViewById(R.id.tvListFiles);

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

        listFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "listFiles");
                if (selectedFolderUri == null) {
                    Log.e(TAG, "select a folder first before listing files");
                    return;
                }
                //Creating a File object for directory
                File directoryPath = new File(selectedFolderUri.getPath());
                //List of all files and directories
                String[] contents = directoryPath.list();
                System.out.println("List of files and directories in the specified directory:");
                for (int i = 0; i < contents.length; i++) {
                    System.out.println(contents[i]);
                }
            }
        });
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
}