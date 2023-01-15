package com.ttcreator.mycoloring.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MCDataContract {

    private MCDataContract() {
    }

    public static final String DB_NAME = "mcDB";
    public static final int DB_VERSION = 11;

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.ttcreator.mycoloring";
    public static final String PATH_NEW_IMAGES = "new_images";
    public static final String PATH_CACHE_IMAGES = "cache_images";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEW_IMAGES);
    public static final Uri CONTENT_URI_STATE = Uri.withAppendedPath(CONTENT_URI, NewImages.MC_NEW_IMAGE_STATE);
    public static final Uri CONTENT_URI_CACHE = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CACHE_IMAGES);


    public static final class NewImages implements BaseColumns {

        public static final String MC_NEW_IMAGE_TABLE_NAME = "new_images";
        public static final String MC_NEW_IMAGE_URL = "new_image_url";
        public static final String MC_NEW_IMAGE_STATUS = "new_image_status";
        public static final String _ID = BaseColumns._ID;
        public static final String MC_NEW_IMAGE_CATEGORY = "new_category";
        public static final String MC_NEW_IMAGE_NAME = "new_name";
        public static final String MC_NEW_IMAGE_KEY = "new_key";
        public static final String MC_NEW_IMAGE_STATE = "new_state";
        public static final String MC_NEW_IMAGE_NEW_STATUS = "new_status_new";
        public static final String MC_NEW_IMAGE_NEW_ADS = "new_status_ads";
        public static final String MC_NEW_IMAGE_PREMIUM_STATUS = "new_premium_status";

    }

    public static final class CacheImages {

        public static final String MC_CACHE_IMAGE_TABLE_NAME = "cache_images";
        public static final String MC_CACHE_IMAGE_URL = "cache_images_url";
        public static final String _ID = BaseColumns._ID;
        public static final String MC_CACHE_IMAGE_CATEGORY = "cache_images_category";
        public static final String MC_CACHE_IMAGE_NAME = "cache_images_name";
    }
}
