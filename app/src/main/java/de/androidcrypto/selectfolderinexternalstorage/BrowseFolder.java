package de.androidcrypto.selectfolderinexternalstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This is the starting class for selecting a folder in (external) shared storage.
 * Select a folder or subfolder and press "select this folder" button
 * It returns to the MainActivity.class with an Intent
 * fields: selectedFolder (Extra String)
 */
public class BrowseFolder extends AppCompatActivity implements Serializable {

    ListView listViewFolder;

    private String[] folderList;

    Intent startListFileActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_folder);

        listViewFolder = findViewById(R.id.lvBrowseFolder);

        listFolder();
    }

    private void listFolder() {
        //Environment.getExternalStoragePublicDirectory("");
        File externalStorageDir = new File(Environment.getExternalStoragePublicDirectory(""), "");
        File[] files = externalStorageDir.listFiles();
        ArrayList<String> folderNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                folderNames.add(files[i].getName());
            }
        }
        folderList = folderNames.toArray(new String[0]);
        System.out.println("fileList size: " + folderList.length);
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, folderList);
        listViewFolder.setAdapter(adapter);
        listViewFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                System.out.println("The selected folder is : " + selectedItem);
                Bundle bundle = new Bundle();
                bundle.putString("selectedFolder", selectedItem);
                bundle.putString("parentFolder", "root");
                startListFileActivityIntent = new Intent(BrowseFolder.this, ListFolder.class);
                startListFileActivityIntent.putExtras(bundle);
                startActivity(startListFileActivityIntent);
            }
        });
    }

}
