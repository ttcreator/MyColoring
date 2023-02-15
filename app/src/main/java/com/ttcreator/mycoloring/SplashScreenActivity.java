package com.ttcreator.mycoloring;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.menuItemFragment.Search;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class SplashScreenActivity extends AppCompatActivity {


    private int keyImage, new_status, haveAds, premiumStatus;
    private String imageUrl, name, imageCategory, status;
    public static AdsManager adsManager;
    public static InterstitialAd mInterstitialAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        checkSubscription();

        adsManager = new AdsManager(this);
        adsManager.initializateAds();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//        Cursor cursorAllItem = getContentResolver().query(MCDataContract.CONTENT_URI,
//                null, null, null, null, null);
        dbRef.child("images").addChildEventListener(firebaseChildEvenListener());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(5000);
                } catch (Exception e) {

                } finally {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }
            }
        };
        thread.start();

//        NotificationScheduler notificationScheduler = new NotificationScheduler(this);
//        notificationScheduler.createNotificationChannel();
//        notificationScheduler.main();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    public ChildEventListener firebaseChildEvenListener() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursorAllItem = getContentResolver().query(MCDataContract.CONTENT_URI,
                null, null, null, null, null);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("childEventListener", "onChildAdded is call");
                if (cursorAllItem.getCount() == 0) {
                    cursorAllItem.close();
                    keyImage = Integer.parseInt(snapshot.getKey());
                    if (snapshot.child("imageUrl").exists()) {
                        imageUrl = snapshot.child("imageUrl").getValue().toString().trim();
                    }
                    if (snapshot.child("name").exists()) {
                        name = snapshot.child("name").getValue().toString().trim();
                    }
                    if (snapshot.child("category").exists()) {
                        imageCategory = snapshot.child("category").getValue().toString().trim();
                    }
                    if (snapshot.child("ads").exists()) {
                        haveAds = Integer.parseInt(snapshot.child("ads").getValue().toString().trim());
                    } else {
                        haveAds = 0;
                    }
                    if (snapshot.child("prem").exists()) {
                        premiumStatus = Integer.parseInt(snapshot.child("prem").getValue().toString().trim());
                    } else {
                        premiumStatus = 0;
                    }
                    new_status = 1;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_URL, imageUrl);
                    contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_NAME, name);
                    contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY, imageCategory);
                    contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_STATUS, status);
                    contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_KEY, keyImage);
                    contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS, new_status);
                    contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS, haveAds);
                    contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_PREMIUM_STATUS, premiumStatus);
                    ContentResolver contentResolver = getContentResolver();
                    Uri uri = contentResolver.insert(MCDataContract.CONTENT_URI, contentValues);
                    if (uri == null) {
                        Toast.makeText(getApplicationContext(),
                                "Insertion of data in the table failed for " + uri, Toast.LENGTH_LONG);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Data saved" + uri, Toast.LENGTH_LONG);
                    }
                } else {
                    if (snapshot.child("imageUrl").exists()) {
                        cursorAllItem.close();
                        imageUrl = snapshot.child("imageUrl").getValue().toString().trim();
                    }
                    keyImage = Integer.parseInt(snapshot.getKey());
                    if (snapshot.child("name").exists()) {
                        name = snapshot.child("name").getValue().toString().trim();
                    }
                    if (snapshot.child("category").exists()) {
                        imageCategory = snapshot.child("category").getValue().toString().trim();
                    }
                    if (snapshot.child("ads").exists()) {
                        haveAds = Integer.parseInt(snapshot.child("ads").getValue().toString().trim());
                    } else {
                        haveAds = 0;
                    }
                    if (snapshot.child("prem").exists()) {
                        premiumStatus = Integer.parseInt(snapshot.child("prem").getValue().toString().trim());
                    } else {
                        premiumStatus = 0;
                    }
                    new_status = 1;
                    String[] projection = {MCDataContract.NewImages.MC_NEW_IMAGE_URL};
                    Cursor cursor = getContentResolver().query(
                            MCDataContract.CONTENT_URI, projection, null, null,
                            null);
                    int imgUrlColumnIndex = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
                    int countDuplicate = 0;
                    while (cursor.moveToNext() && countDuplicate < 1) {
                        String currentUrl = cursor.getString(imgUrlColumnIndex);
                        if (currentUrl.equals(imageUrl)) {
                            countDuplicate++;
                            Log.d("itemExist", "item " + name + " already exist");
                        }
                    }
                    cursor.close();
                    if (countDuplicate == 0) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_URL, imageUrl);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_NAME, name);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY, imageCategory);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_STATUS, status);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_KEY, keyImage);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS, new_status);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS, haveAds);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_PREMIUM_STATUS, premiumStatus);
                        ContentResolver contentResolver = getContentResolver();
                        Uri uri = contentResolver.insert(MCDataContract.CONTENT_URI, contentValues);
                        if (uri == null) {
                            Toast.makeText(getApplicationContext(),
                                    "Insertion of data in the table failed for " + uri, Toast.LENGTH_LONG);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Data saved" + uri, Toast.LENGTH_LONG);
                        }

                    } else {
                        Log.d("allDuplicates", "All item now have in sqlite");
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("childEventListener", "onChildChanged is call. previousChildName: " + previousChildName);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("childEventListener", "onChildRemoved is call");
                String deleteUrl = snapshot.child("imageUrl").getValue().toString().trim();
                String[] selectionArgs = {deleteUrl};
                String selection = MCDataContract.NewImages.MC_NEW_IMAGE_URL + "=?";
                String[] projection = {MCDataContract.NewImages._ID};
                Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection,
                        selection, selectionArgs, null);
                int columnIndexId = cursor.getColumnIndex(MCDataContract.NewImages._ID);
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(columnIndexId);
                    Uri uri = ContentUris.withAppendedId(MCDataContract.CONTENT_URI, id);
                    contentResolver.delete(uri, null, null);
                    cursor.close();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("childEventListener", "onChildMoved is call");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("childEventListener", "onCancelled is call");
            }
        };
        return childEventListener;
    }


    void checkSubscription() {

        BillingClient billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {
        }).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    Log.d("testOffer", list.size() + " size");
                                    if (list.size() > 0) {
                                        SharedPreferencesFactory.saveBoolean(getApplicationContext(),
                                                "isPurchase", true);
                                        int i = 0;
                                        for (Purchase purchase : list) {
                                            //Here you can manage each product, if you have multiple subscription
                                            Log.d("testOffer", purchase.getOriginalJson()); // Get to see the order information
                                            Log.d("testOffer", " index" + i);
                                            i++;
                                        }
                                    } else {
                                        SharedPreferencesFactory.saveBoolean(getApplicationContext(),
                                                "isPurchase", false);
                                    }
                                }
                            });
                }

            }
        });
    }

}