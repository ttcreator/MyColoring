package com.ttcreator.mycoloring;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.data.MCDataContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SaveAsyncBitmap extends Thread {

    private String TAG = "Save Error";

    public String inBackgroundSaveSD(Bitmap finalBitmap, Context context, String fileName, String key) {

        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context.getApplicationContext(), null);
        File primaryExternalStorage = externalStorageVolumes[0];
        File appSpecificExternalDir = new File(primaryExternalStorage.getPath(), fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(appSpecificExternalDir);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            // Обработка ошибки файла
            e.printStackTrace();
            return "FAILED";
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // Обработка ошибки закрытия файла
                    e.printStackTrace();
                    return "FAILED";
                }
            }
        }
        SharedPreferencesFactory.saveStringUrl(context.getApplicationContext(), key,
                appSpecificExternalDir.getPath());
        return appSpecificExternalDir.getPath();
    }

    public String inBackgroundSaveLocal(Bitmap finalBitmap, Context context, String fileName, String key) {
        File mypath = new File(context.getFilesDir(), fileName);
        if (mypath == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
        }
        try {
            if (finalBitmap != null) {
                FileOutputStream fos = context.openFileOutput(mypath.getName(), Context.MODE_PRIVATE);
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
                SharedPreferencesFactory.saveStringUrl(context.getApplicationContext(), key,
                        mypath.getPath());
                return mypath.getPath();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return "FAILED";
        }
        return mypath.getPath() + fileName;
    }

}
