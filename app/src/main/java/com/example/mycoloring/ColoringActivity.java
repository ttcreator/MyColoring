package com.example.mycoloring;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import uk.co.senab.photoview.ColourImageView;
import uk.co.senab.photoview.PhotoViewAttacher;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ColoringActivity extends AppCompatActivity implements View.OnClickListener {

    PhotoViewAttacher photoViewAttacher;
    Bitmap bitmap;
    TableLayout colorTable;
    ColourImageView colourImageView;
    ImageView currentColor, cColor1, cColor2, cColor3;
    int colorId;
    ImageButton buttonPickColor, undo, redo, clearAll, save;
    TableLayout tableColor;
    private Bitmap cachedBitmap;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coloring);

        Log.v("lifeCycle", "onCreate()");

        initViews();
        addEvent();
    }

    private void initViews() {

        colourImageView = (ColourImageView) findViewById(R.id.color_image_view);
        colorTable = (TableLayout) findViewById(R.id.colortable);
        cColor1 = (ImageView) findViewById(R.id.current_pen1);
        cColor2 = (ImageView) findViewById(R.id.current_pen2);
        cColor3 = (ImageView) findViewById(R.id.current_pen3);
        buttonPickColor = (ImageButton) findViewById(R.id.button_pick_color);
        tableColor = (TableLayout) findViewById(R.id.colortable);
        redo = (ImageButton) findViewById(R.id.redo_button);
        undo = (ImageButton) findViewById(R.id.undo_button);
        clearAll = (ImageButton) findViewById(R.id.clear_button);
        save = (ImageButton) findViewById(R.id.save_button);

    }

    public void addEvent() {

        bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.my_image);
        photoViewAttacher = new PhotoViewAttacher(colourImageView, bitmap);


        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colourImageView.undo();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colourImageView.redo();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSavedBitmap(colourImageView.getmBitmap());
                Toast.makeText(ColoringActivity.this, "You state image was saved",
                        Toast.LENGTH_LONG).show();
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("All progress was delete, are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                colourImageView.setImageBT(bitmap);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
        });

        currentColor = cColor1;
        getSavedColors(cColor1, cColor2, cColor3);
        getSavedBitmap();
        cColor1.setOnClickListener(checkCurrentColor);
        cColor2.setOnClickListener(checkCurrentColor);
        cColor3.setOnClickListener(checkCurrentColor);
        changeCurrentColor(currentColor);

        for (int i = 0; i < tableColor.getChildCount(); i++) {
            for (int j = 0; j < ((TableRow) tableColor.getChildAt(i)).getChildCount(); j++) {
                if (((TableRow) tableColor.getChildAt(i)).getChildAt(j) instanceof Button) {
                    ((TableRow) tableColor.getChildAt(i)).getChildAt(j).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
                            int colorId = buttonColor.getColor();
                            changeCurrentColor(colorId);
                        }
                    });
                }
            }
        }

        buttonPickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
    }


    private void openColorPicker() {

        if (currentColor != null) {

            AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, colorId,
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {
                        }

                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            colorId = color;
                            changeCurrentColor(color);
                        }
                    });
            ambilWarnaDialog.show();
        } else {
            Toast.makeText(this, "Please, set color", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {

    }


    View.OnClickListener checkCurrentColor = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.current_pen1:
                    v.setBackgroundResource(R.drawable.set_color_selected);
                    setDefaultBg(R.id.current_pen2, R.id.current_pen3);
                    currentColor = (ImageView) v;
                    changeCurrentColor(currentColor);
                    break;
                case R.id.current_pen2:
                    v.setBackgroundResource(R.drawable.set_color_selected);
                    setDefaultBg(R.id.current_pen1, R.id.current_pen3);
                    currentColor = ((ImageView) v);
                    changeCurrentColor(currentColor);
                    break;
                case R.id.current_pen3:
                    v.setBackgroundResource(R.drawable.set_color_selected);
                    setDefaultBg(R.id.current_pen1, R.id.current_pen2);
                    currentColor = ((ImageView) v);
                    changeCurrentColor(currentColor);
                    break;
            }
        }
    };

    public void changeCurrentColor(ImageView color) {
        colourImageView.setColor(((ColorDrawable) color.getDrawable()).getColor());
    }

    private void changeCurrentColor(int color) {
        colourImageView.setColor(color);
        currentColor.setImageDrawable(new ColorDrawable(color));
    }

    private void setDefaultBg(int view1, int view2) {
        findViewById(view1).setBackgroundResource(R.drawable.set_color_default);
        findViewById(view2).setBackgroundResource(R.drawable.set_color_default);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("lifeCycle", "onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setSaveColor();
        Log.v("lifeCycle", "onPause()");
        setSavedBitmap(colourImageView.getmBitmap());
    }

    private void setSavedBitmap(Bitmap finalBitmap) {

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "savedBitmap1.png");
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } finally {
                if (fos != null) fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferencesFactory.saveString(this, "savedBitmap1", file.getPath());

    }

    private void getSavedBitmap() {
        cachedBitmap = BitmapFactory.decodeFile(SharedPreferencesFactory.getString(this, "savedBitmap1"));
        if (cachedBitmap != null) {
            colourImageView.setImageBT(cachedBitmap);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("lifeCycle", "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("lifeCycle", "onStop()");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.v("lifeCycle", "onPostResume()");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("lifeCycle", "onRestart()");
    }

    private void setSaveColor() {
        SharedPreferencesFactory.saveInteger(this, SharedPreferencesFactory.SavedColor1,
                ((ColorDrawable) cColor1.getDrawable()).getColor());
        SharedPreferencesFactory.saveInteger(this, SharedPreferencesFactory.SavedColor2,
                ((ColorDrawable) cColor2.getDrawable()).getColor());
        SharedPreferencesFactory.saveInteger(this, SharedPreferencesFactory.SavedColor3,
                ((ColorDrawable) cColor3.getDrawable()).getColor());
    }

    private void getSavedColors(ImageView cColor1, ImageView cColor2, ImageView cColor3) {
        cColor1.setImageDrawable(new ColorDrawable(SharedPreferencesFactory.getInteger(this,
                SharedPreferencesFactory.SavedColor1, getResources().getColor(R.color.white))));
        cColor2.setImageDrawable(new ColorDrawable(SharedPreferencesFactory.getInteger(this,
                SharedPreferencesFactory.SavedColor2, getResources().getColor(R.color.white))));
        cColor3.setImageDrawable(new ColorDrawable(SharedPreferencesFactory.getInteger(this,
                SharedPreferencesFactory.SavedColor3, getResources().getColor(R.color.white))));
    }

}