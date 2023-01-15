package com.ttcreator.mycoloring;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.data.MCDataContract;

public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

        setPreferencesFromResource(R.xml.coloring_preferences, rootKey);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Preference clearAllImagesButton = findPreference("clearMyImages");
        SeekBarPreference seekBar = findPreference("seekBarMaxSize");
        CheckBoxPreference checkBoxPreference = findPreference("setVibration");
        Preference disabeAds = findPreference("disableAds");

        clearAllImagesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("All you images was delete, are you sure?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentResolver contentResolver = getActivity().getContentResolver();
                                String[] projection = {MCDataContract.NewImages._ID,
                                        MCDataContract.NewImages.MC_NEW_IMAGE_STATE};
                                Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI,
                                        projection, null, null,
                                        null);
                                while (cursor.moveToNext()) {
                                    int columnIndexState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
                                    int columnIndexId = cursor.getColumnIndex(MCDataContract.NewImages._ID);
                                    String valueState = cursor.getString(columnIndexState);
                                    String valueId = cursor.getString(columnIndexId);
                                    if (valueState != null) {
                                        ContentValues cv = new ContentValues();
                                        cv.putNull(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
                                        Uri rowUri = Uri.withAppendedPath(MCDataContract.CONTENT_URI, valueId);
                                        contentResolver.update(rowUri, cv, null, null);
                                        MCDBHelper mcdbHelper = new MCDBHelper(getActivity().getApplicationContext());
                                        mcdbHelper.getAllRows(MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.show();
                return true;
            }
        });
        


        seekBar.setMin(3);
        seekBar.setMax(30);
        seekBar.getShowSeekBarValue();
        seekBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if (newValue instanceof Integer) {
                    Integer newValueInt;
                    try {
                        newValueInt = (Integer) newValue;
                    } catch (NumberFormatException nfe) {
                        Toast.makeText(getActivity(),
                                "SeekBarPreference is a Integer, but it caused a NumberFormatException",
                                Toast.LENGTH_SHORT).show();

                        return false;
                    }
                    SharedPreferencesFactory.saveInteger(getContext(), SharedPreferencesFactory.StackSize, newValueInt);
                    //seekBar.setValue(newValueInt);
                    return true;
                } else {
                    String objType = newValue.getClass().getName();
                    Toast.makeText(getActivity(),
                            "SeekBarPreference is not a Integer, it is " +objType,
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }); seekBar.setValue(SharedPreferencesFactory.getInteger(getContext(), (SharedPreferencesFactory.StackSize), 3));


        disabeAds.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                PurchaseDialog purchaseDialog = new PurchaseDialog();
                purchaseDialog.show(((AppCompatActivity) getContext()).getSupportFragmentManager(),
                        "PurchaseDialogFragment");

                return false;
            }
        });


    }
}
