package com.ttcreator.mycoloring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CacheImageModel;
import com.ttcreator.mycoloring.utill.ImageLoaderUtill;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import kotlinx.coroutines.internal.MissingMainCoroutineDispatcherFactory;
import uk.co.senab.photoview.ColourImageView;
import uk.co.senab.photoview.PhotoViewAttacher;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ColoringActivity extends AppCompatActivity implements View.OnClickListener {

    private PhotoViewAttacher photoViewAttacher;
    private TableLayout colorTable;
    private ColourImageView colourImageView = null;
    private ImageView currentColor, cColor1, cColor2, cColor3;
    private int colorId;
    private ImageButton buttonPickColor, undo, redo, clearAll, save;
    private TableLayout tableColor;
    private Bitmap cachedBitmap;
    private boolean fromSdCard;
    private String imgUrls, nameImages, category, stateUrl;
    private int imageKey;
    private ProgressBar progressBar;
    private Bitmap startBitmap;
    private Uri contentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coloring);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        imgUrls = intent.getStringExtra("urlImagePosition");
        nameImages = intent.getStringExtra("nameImage");
        imageKey = intent.getIntExtra("keyPosition", 0);
        category = intent.getStringExtra("categoryPosition");
        stateUrl = intent.getStringExtra("statePosition");
        contentUri = intent.getData();

        ContentResolver contentResolver = getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS, "0");
        contentResolver.update(contentUri, cv, MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS, null);


        checkSdCard();
        initImageLoader(this);
        initViews();
        addEvent();

        if (cachedBitmap == null) {

            LoadAsyncBitmap loadAsyncBitmap = new LoadAsyncBitmap(colourImageView, imgUrls);
            Thread loadThreadBitmap = new Thread(loadAsyncBitmap);
            loadThreadBitmap.start();
            try {
                loadThreadBitmap.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cachedBitmap = loadAsyncBitmap.getBtmImage();
            loadAsyncBitmap.showLagreImageAsynWithNoCacheOpen(colourImageView, imgUrls, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    photoViewAttacher = new PhotoViewAttacher(colourImageView, cachedBitmap);
                    if (cachedBitmap != null) {
                        colourImageView.setImageBT(cachedBitmap);
                        colourImageView.clearStack();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else {
            photoViewAttacher = new PhotoViewAttacher(colourImageView, cachedBitmap);
            colourImageView.setImageBitmap(cachedBitmap);

        }
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

    }

    public void addEvent() {

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
                String path = setSavedBitmap();
                Toast.makeText(ColoringActivity.this,
                        "You state image was saved in: " + path, Toast.LENGTH_LONG).show();
            }
        });

        colourImageView.setOnRedoUndoListener(new ColourImageView.OnRedoUndoListener() {
            @Override
            public void onRedoUndo(int undoSize, int redoSize) {
                ContentResolver contentResolver = getContentResolver();
                if (undoSize != 0) {
                    undo.setEnabled(true);
                } else {
                    undo.setEnabled(false);
                }
                if (redoSize != 0) {
                    redo.setEnabled(true);
                } else {
                    redo.setEnabled(false);
                }
                if (undoSize != 0 || redoSize != 0) {
                    ContentValues cv = new ContentValues();
                    cv.put(MCDataContract.NewImages.MC_NEW_IMAGE_STATE, stateUrl);
                    contentResolver.update(contentUri, cv, MCDataContract.NewImages.MC_NEW_IMAGE_STATE,
                            null);
                }
                if (redoSize == 0 && undoSize == 0) {
                    ContentValues cv = new ContentValues();
                    cv.putNull(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
                    contentResolver.update(contentUri, cv,
                            MCDataContract.NewImages.MC_NEW_IMAGE_STATE, null);
                    MCDBHelper mcdbHelper = new MCDBHelper(getApplicationContext());
                    mcdbHelper.getAllRows(MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME);
                }
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("All progress was delete, are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LoadAsyncBitmap loadAsyncBitmap = new LoadAsyncBitmap(colourImageView, imgUrls);
                                Thread loadThreadBitmap = new Thread(loadAsyncBitmap);
                                loadThreadBitmap.start();
                                try {
                                    loadThreadBitmap.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startBitmap = loadAsyncBitmap.getBtmImage();
//                                colourImageView.setImageBitmap(startBitmap);
                                loadAsyncBitmap.showLagreImageAsynWithNoCacheOpen(colourImageView, imgUrls,
                                        new ImageLoadingListener() {
                                            @Override
                                            public void onLoadingStarted(String imageUri, View view) {
                                                progressBar.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                            }

                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                photoViewAttacher = new PhotoViewAttacher(colourImageView, startBitmap);
                                                if (startBitmap != null) {
                                                    colourImageView.setImageBT(startBitmap);
                                                    colourImageView.clearStack();

                                                }
                                                progressBar.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onLoadingCancelled(String imageUri, View view) {
                                            }
                                        });
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

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(100 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoaderUtill.getInstance().init(config);
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
    protected void onPause() {
        super.onPause();
        setSaveColor();
       // setSavedBitmap();
    }

    private String setSavedBitmap() {
        String path = null;
        SaveAsyncBitmap saveAsyncBitmap = new SaveAsyncBitmap();

        if (colourImageView != null && colourImageView.bmstackIsNull() == true) {
            if (fromSdCard) {
                String pathFromSD = saveAsyncBitmap.inBackgroundSaveSD(colourImageView.getmBitmap(), getApplicationContext(),
                        nameImages, nameImages);
                String[] selectionArgs = {imgUrls};
                ContentResolver contentResolver = getContentResolver();
                ContentValues cv = new ContentValues();
                cv.put(MCDataContract.NewImages.MC_NEW_IMAGE_STATE, pathFromSD);
                contentResolver.update(contentUri,
                        cv, MCDataContract.NewImages.MC_NEW_IMAGE_URL,
                        selectionArgs);
                path = pathFromSD;
            } else {
                String pathFromLocal = saveAsyncBitmap.inBackgroundSaveLocal(colourImageView.getmBitmap(), getApplicationContext(),
                        nameImages, nameImages);
                ContentResolver contentResolver = getContentResolver();
                ContentValues cv = new ContentValues();
                cv.put(MCDataContract.NewImages.MC_NEW_IMAGE_STATE, pathFromLocal);
                contentResolver.update(contentUri,
                        cv, null,
                        null);
                path = pathFromLocal;
            }
        } else {
            if (fromSdCard) {
                String pathFromSD = saveAsyncBitmap.inBackgroundSaveSD(colourImageView.getmBitmap(), getApplicationContext(),
                        nameImages, nameImages);
                path = pathFromSD;
            } else {
                String pathFromLocal = saveAsyncBitmap.inBackgroundSaveLocal(colourImageView.getmBitmap(), getApplicationContext(),
                        nameImages, nameImages);
                path = pathFromLocal;
            }

        }
        return path;
    }

    @Override
    public void onBackPressed() {
        setSavedBitmap();
        super.onBackPressed();
    }

    private Bitmap getSavedBitmap() {

        ContentResolver contentResolver = getContentResolver();

        checkSdCard();
        if (fromSdCard) {
            String[] projection = {MCDataContract.NewImages.MC_NEW_IMAGE_STATE};
            Cursor cursor = contentResolver.query(contentUri, projection,
                    null, null, null);
            int colunmIndexState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
            String stateFromDB = null;
            while (cursor.moveToNext()) {
                stateFromDB = cursor.getString(colunmIndexState);
            }
            if (stateFromDB != null) {
                cachedBitmap = BitmapFactory.decodeFile(stateFromDB);
                colourImageView.setImageBitmap(cachedBitmap);
                return cachedBitmap;
            } else {
                return null;
            }
//            cachedBitmap = BitmapFactory.decodeFile(SharedPreferencesFactory.getStringUrl(this, nameImages));
//            if (cachedBitmap != null) {
//                colourImageView.setImageBitmap(cachedBitmap);
//            }
        } else {

            String[] projection = {MCDataContract.NewImages.MC_NEW_IMAGE_STATE};
            Cursor cursor = contentResolver.query(contentUri, projection,
                    null, null, null);
            int colunmIndexState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
            String stateFromDB = cursor.getString(colunmIndexState);
            if (stateFromDB != null) {
                cachedBitmap = BitmapFactory.decodeFile(stateFromDB);
                colourImageView.setImageBitmap(cachedBitmap);
                return cachedBitmap;
            } else {
                return null;
            }

//            cachedBitmap = BitmapFactory.decodeFile(SharedPreferencesFactory.getStringUrl(this, nameImages));
//            if (cachedBitmap != null) {
//                colourImageView.setImageBitmap(cachedBitmap);
//            }
        }
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

    private boolean checkSdCard() {

        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            fromSdCard = true;
        } else {
            fromSdCard = false;
        }
        return fromSdCard;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent openSettings = new Intent(this, SettingsColoringActivity.class);
            startActivity(openSettings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}