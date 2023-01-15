package com.ttcreator.mycoloring;

import android.app.Activity;
import android.content.Context;

import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class BillingManager {

    private ArrayList<String> subsList;
    private String TAG = "BillingManagerLog";
    private BillingClient billingClient;
    private Purchase purchase;
    private List <ProductDetails> productDetailsList;


    public BillingManager() {

        productDetailsList = new ArrayList<>();
        subsList = new ArrayList<>();
        subsList.add("premium_subs_test");
        subsList.add("one_month_premium_test");
        subsList.add("one_year_premuim_test");
    }

    public void billingSetup(Context context, Activity activity) {

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        completePurchase(purchase, activity);
                    }
                } else if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.USER_CANCELED) {
                    Log.i(TAG, "onPurchasesUpdated: Purchase Canceled");
                } else {
                    Log.i(TAG, "onPurchasesUpdated: Error");
                }
            }
        };

        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(
                    @NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "OnBillingSetupFinish connected");
                    queryProduct();
                } else {
                    Log.i(TAG, "OnBillingSetupFinish failed");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.i(TAG, "OnBillingSetupFinish connection lost");
            }
        });
    }

    private void queryProduct() {
        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(subsList.get(0))
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(subsList.get(1))
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(subsList.get(2))
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );


        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(
                            @NonNull BillingResult billingResult,
                            @NonNull List<ProductDetails> productDetailsList) {
                        BillingManager.this.productDetailsList = productDetailsList;
                        Log.i(TAG, "onProductDetailsResponse: No products");
                    }
                }
        );
    }


    void launchPurchaseFlow(ProductDetails productDetails, Activity activity) {
        assert productDetails.getSubscriptionOfferDetails() != null;
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    public void consumePurchase(View view, Activity activity) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult,
                                          @NonNull String purchaseToken) {
                if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.OK) {
                    activity.runOnUiThread(() -> {
                    });
                }
            }
        };
        billingClient.consumeAsync(consumeParams, listener);
    }

    private void completePurchase(Purchase item, Activity activity) {

        purchase = item;

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            activity.runOnUiThread(() -> {
                SharedPreferencesFactory.saveBoolean(activity.getApplicationContext(),
                        "isPurchase", true);
            });
    }

}