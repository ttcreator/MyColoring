package com.ttcreator.mycoloring;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdsManager {

    private Context context;
    public static InterstitialAd mInterstitialAd;
    private static boolean adIsLoaded;

    public AdsManager(Context context) {
        this.context = context;
    }


    public void initializateAds () {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                loadInterstatialAd();
            }
        });
    }

    public void createAds(AdView adView) {
        AdRequest adRequestBanner = new AdRequest.Builder().build();
        adView.loadAd(adRequestBanner);
    }



    public boolean loadInterstatialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        adIsLoaded = true;
                        Log.i("loadInterstatialAd", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("loadInterstatialAd", loadAdError.toString());
                        mInterstitialAd = null;
                        adIsLoaded = false;
                    }
                });
        return adIsLoaded;
    }

    public void showInterstitialAds(Activity activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
            loadInterstatialAd();
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    public InterstitialAd getmInterstitialAd() {
        return mInterstitialAd;
    }
}

