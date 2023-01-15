package com.ttcreator.mycoloring.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.ttcreator.mycoloring.data.MCDataContract.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MCContentProvider extends android.content.ContentProvider {

    MCDBHelper mcdbHelper;

    private static final int IMAGES = 444;
    private static final int IMAGES_ID = 555;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(MCDataContract.AUTHORITY, MCDataContract.PATH_NEW_IMAGES, IMAGES);
        sURIMatcher.addURI(MCDataContract.AUTHORITY, MCDataContract.PATH_NEW_IMAGES + "/#", IMAGES_ID);

    }

    @Override
    public boolean onCreate() {
        mcdbHelper = new MCDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase db = mcdbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sURIMatcher.match(uri);
        switch (match) {
            case IMAGES:
                cursor = db.query(NewImages.MC_NEW_IMAGE_TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case IMAGES_ID:
                selection = NewImages._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(NewImages.MC_NEW_IMAGE_TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                Toast.makeText(getContext(), "Incorrect Uri", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't query incorrect Uri " + uri );
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mcdbHelper.getWritableDatabase();
        int match = sURIMatcher.match(uri);
        switch (match) {
            case IMAGES:
                long id = db.insert(NewImages.MC_NEW_IMAGE_TABLE_NAME, null, values);
                if (id == -1) {
                    Log.e ("insertMethod", "Insertion of data in the table failed for " + uri);
                    return null;
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion of data in the table failed for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase db = mcdbHelper.getWritableDatabase();
        int match = sURIMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case IMAGES:
                rowsUpdated = db.delete(NewImages.MC_NEW_IMAGE_TABLE_NAME, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
            case IMAGES_ID:
                selection = NewImages._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.delete(NewImages.MC_NEW_IMAGE_TABLE_NAME, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
            default:
                Toast.makeText(getContext(), "Incorrect Uri", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't delete this Uri " + uri );
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mcdbHelper.getWritableDatabase();
        int match = sURIMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case IMAGES:
                rowsUpdated = db.update(NewImages.MC_NEW_IMAGE_TABLE_NAME, values, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
            case IMAGES_ID:
                selection = NewImages._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(NewImages.MC_NEW_IMAGE_TABLE_NAME, values, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
            default:
                Toast.makeText(getContext(), "Incorrect Uri", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't update this Uri  " + uri );
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
