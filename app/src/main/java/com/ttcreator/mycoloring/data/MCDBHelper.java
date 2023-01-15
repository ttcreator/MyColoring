package com.ttcreator.mycoloring.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ttcreator.mycoloring.model.CacheImageModel;

import java.util.ArrayList;
import java.util.List;

public class MCDBHelper extends SQLiteOpenHelper {

    public MCDBHelper(Context context) {
        super(context, MCDataContract.DB_NAME, null, MCDataContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME + "( " +
                MCDataContract.NewImages._ID + " INTEGER PRIMARY KEY," +
                MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY + " TEXT," +
                MCDataContract.NewImages.MC_NEW_IMAGE_STATUS + " TEXT," +
                MCDataContract.NewImages.MC_NEW_IMAGE_NAME + " TEXT," +
                MCDataContract.NewImages.MC_NEW_IMAGE_KEY + " TEXT," +
                MCDataContract.NewImages.MC_NEW_IMAGE_STATE + " TEXT," +
                MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS + " INTEGER," +
                MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS + " INTEGER," +
                MCDataContract.NewImages.MC_NEW_IMAGE_PREMIUM_STATUS + " INTEGER," +
                MCDataContract.NewImages.MC_NEW_IMAGE_URL + " TEXT" + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + MCDataContract.CacheImages.MC_CACHE_IMAGE_TABLE_NAME + "( " +
                MCDataContract.CacheImages._ID + " INTEGER PRIMARY KEY," +
                MCDataContract.CacheImages.MC_CACHE_IMAGE_NAME + " TEXT," +
                MCDataContract.CacheImages.MC_CACHE_IMAGE_URL + " TEXT," +
                MCDataContract.CacheImages.MC_CACHE_IMAGE_CATEGORY + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME);
        onCreate(db);
    }


    public void addCacheImage(CacheImageModel cacheImageModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_URL, cacheImageModel.getImageCacheUrl());
        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY, cacheImageModel.getCategory());
        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_NAME, cacheImageModel.getName());
        db.insert(MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME, null, contentValues);
        db.close();
    }

    public List<CacheImageModel> getAllCacheImage() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CacheImageModel> allCacheImages = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                CacheImageModel cacheImageModel = new CacheImageModel();
                cacheImageModel.setId(Integer.parseInt(cursor.getString(0)));
                cacheImageModel.setCategory(cursor.getString(1));
                cacheImageModel.setName(cursor.getString(2));
                cacheImageModel.setImageCacheUrl(cursor.getString(3));
                allCacheImages.add(cacheImageModel);
            } while (cursor.moveToNext());
        }
        return allCacheImages;
    }

    public ArrayList<CacheImageModel> getCacheImageByCategory(String[] category, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        ArrayList<CacheImageModel> cacheImagesByCategory = new ArrayList<>();
        String selection = MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY + "=?";
        String[] columns = {MCDataContract.NewImages._ID, MCDataContract.NewImages.MC_NEW_IMAGE_URL, MCDataContract.NewImages.MC_NEW_IMAGE_NAME,
                MCDataContract.NewImages.MC_NEW_IMAGE_KEY, MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS,
                MCDataContract.NewImages.MC_NEW_IMAGE_STATE, MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS,
                MCDataContract.NewImages.MC_NEW_IMAGE_PREMIUM_STATUS};
        Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI, columns,
                selection, category, null);
        int columnIndexImageUrl = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
        int columnIndexImageState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
        int columnIndexImageName = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NAME);
        int columnIndexImageKey = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_KEY);
        int columnIndexImageNewStatus = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS);
        int columnIndexImageNewHaveAds = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS);
        int columnIndexImageNewPremiumStatus = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_PREMIUM_STATUS);
        int columnIndexImageNewId = cursor.getColumnIndex(MCDataContract.NewImages._ID);

        if (cursor.moveToFirst()) {
            do {
                CacheImageModel cacheImageModel = new CacheImageModel();
                cacheImageModel.setName(cursor.getString(columnIndexImageName));
                cacheImageModel.setState(cursor.getString(columnIndexImageState));
                cacheImageModel.setImageCacheUrl(cursor.getString(columnIndexImageUrl));
                cacheImageModel.setCategory(category[0]);
                cacheImageModel.setKey(cursor.getInt(columnIndexImageKey));
                cacheImageModel.setNewStatus(cursor.getInt(columnIndexImageNewStatus));
                cacheImageModel.setHaveAds(cursor.getInt(columnIndexImageNewHaveAds));
                cacheImageModel.setPremiumStatus(cursor.getInt(columnIndexImageNewPremiumStatus));
                cacheImageModel.setId(cursor.getInt(columnIndexImageNewId));
                cacheImagesByCategory.add(cacheImageModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cacheImagesByCategory;
    }

    public List<CacheImageModel> getCacheImageByStatus(String[] status) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CacheImageModel> cacheImagesByStatus = new ArrayList<>();
        String selection = MCDataContract.NewImages.MC_NEW_IMAGE_STATUS + "=?";
        String[] columns = {MCDataContract.NewImages.MC_NEW_IMAGE_URL, MCDataContract.NewImages.MC_NEW_IMAGE_NAME,
                MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY};
        Cursor cursor = db.query(MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME, columns,
                selection, status, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CacheImageModel cacheImageModel = new CacheImageModel();
                cacheImageModel.setName(cursor.getString(1));
                cacheImageModel.setImageCacheUrl(cursor.getString(0));
                cacheImageModel.setCategory(status[0]);
                cacheImagesByStatus.add(cacheImageModel);
            } while (cursor.moveToNext());
        }
        return cacheImagesByStatus;
    }


    public List<CacheImageModel> getCacheImageByState() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CacheImageModel> cacheImagesByState = new ArrayList<>();
        String selection = MCDataContract.NewImages.MC_NEW_IMAGE_STATE + " IS NOT NULL";
        String[] projection = {MCDataContract.NewImages.MC_NEW_IMAGE_STATE};
        Cursor cursor = db.query(MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME, projection,
                selection, null, null, null, null);
        int columnIndexImageState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
        int columnIndexImageUrl = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
        if (cursor.moveToFirst()) {
            do {
                CacheImageModel cacheImageModel = new CacheImageModel();
                cacheImageModel.setImageCacheUrl(cursor.getString(columnIndexImageState));
                cacheImagesByState.add(cacheImageModel);
            } while (cursor.moveToNext());
        }
        return cacheImagesByState;
    }

    public void getCountTableName() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Log.d("Table Name", "Table Name=> " + c.getString(0));
                c.moveToNext();
            }
        }
    }

    public void dropTable(String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + tablename);
    }

    public void deleteAllRowsTable(String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + tablename);
    }

    public void getAllRows(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        if (cursor.getCount() != 0) {
            int columnIndexImageId = cursor.getColumnIndex(MCDataContract.NewImages._ID);
            int columnIndexImageState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
            int columnIndexImageStatus = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATUS);
            int columnIndexImageAds = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(columnIndexImageId);
                    String stateUrl = cursor.getString(columnIndexImageState);
                    String status = cursor.getString(columnIndexImageStatus);
                    String haveAds = cursor.getString(columnIndexImageAds);
                    Log.d("getAllRows", "_ID: " + id + ", haveAds: " + haveAds + ", status: " + status + ", item state url: " + stateUrl);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } else {
            Log.d("getAllRows", "Cursor is null");
            cursor.close();
        }
    }

}
