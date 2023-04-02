package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DataSource dataSource;

    private boolean checkAndRequestPermissions() {
        int permissionReadStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    0);
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!this.checkAndRequestPermissions()) {
            return;
        }

        dataSource = new DataSource(
                this,
                Environment.getExternalStorageDirectory() + "/Pictures/liwad1.db"
        );
        dataSource.open();
    }

    public void onExportTxt(View view) {
        try {
            dataSource.exportToTxt(
                    Environment.getExternalStorageDirectory().getPath() + "/Pictures/dummyexportreadbill.txt",
                    "dummyexportreadbill"
            );
            Toast.makeText(this, "Successfully export to txt.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            Toast.makeText(this, "Failed to export to txt.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onImportTxt(View view) {
        try {
            dataSource.importFromText(
                    Environment.getExternalStorageDirectory().getPath() + "/Pictures/dummyexportreadbill.txt",
                    "dummyexportreadbill"
            );
            Toast.makeText(this, "Successfully import from txt.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            Toast.makeText(this, "Failed to import from txt.", Toast.LENGTH_SHORT).show();
        }
    }
}