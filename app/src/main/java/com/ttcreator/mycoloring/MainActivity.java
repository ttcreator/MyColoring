package com.ttcreator.mycoloring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.deeplink.DeepLink;
import com.appsflyer.deeplink.DeepLinkListener;
import com.appsflyer.deeplink.DeepLinkResult;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.menuItemFragment.MyDrawing;
import com.ttcreator.mycoloring.menuItemFragment.MyGalery;
import com.ttcreator.mycoloring.menuItemFragment.Search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private MyGalery myGalery;
    private MyDrawing myDrawingFragment;
    private Search searchFragment;
    private MCDBHelper mcdbHelper;
    private BillingClient billingClient;
    private List<ProductDetails> productDetails;
    private Purchase purchase;

    static final String TAG = "InAppPurchaseTag";

    public static final String LOG_TAG = "AppsFlyerOneLinkSimApp";
    public static final String DL_ATTRS = "dl_attrs";
    Map<String, Object> conversionData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String afDevKey = "V9qaHeRnWJAuc3Mf9b3u7X";
        AppsFlyerLib appsflyer = AppsFlyerLib.getInstance();
        // Make sure you remove the following line when building to production
        appsflyer.setDebugLog(true);
        appsflyer.setMinTimeBetweenSessions(0);
        //set the OneLink template id for share invite links
        AppsFlyerLib.getInstance().setAppInviteOneLink("H5hv");

        appsflyer.subscribeForDeepLink(new DeepLinkListener(){
            @Override
            public void onDeepLinking(@NonNull DeepLinkResult deepLinkResult) {
                DeepLinkResult.Status dlStatus = deepLinkResult.getStatus();
                if (dlStatus == DeepLinkResult.Status.FOUND) {
                    Log.d(LOG_TAG, "Deep link found");
                } else if (dlStatus == DeepLinkResult.Status.NOT_FOUND) {
                    Log.d(LOG_TAG, "Deep link not found");
                    return;
                } else {
                    // dlStatus == DeepLinkResult.Status.ERROR
                    DeepLinkResult.Error dlError = deepLinkResult.getError();
                    Log.d(LOG_TAG, "There was an error getting Deep Link data: " + dlError.toString());
                    return;
                }
                DeepLink deepLinkObj = deepLinkResult.getDeepLink();
                try {
                    Log.d(LOG_TAG, "The DeepLink data is: " + deepLinkObj.toString());
                } catch (Exception e) {
                    Log.d(LOG_TAG, "DeepLink data came back null");
                    return;
                }
                // An example for using is_deferred
                if (deepLinkObj.isDeferred()) {
                    Log.d(LOG_TAG, "This is a deferred deep link");
                } else {
                    Log.d(LOG_TAG, "This is a direct deep link");
                }
                // An example for getting deep_link_value
                String fruitName = "";
                try {
                    fruitName = deepLinkObj.getDeepLinkValue();

                    String referrerId = "";
                    JSONObject dlData = deepLinkObj.getClickEvent();

                    // ** Next if statement is optional **
                    // Our sample app's user-invite carries the referrerID in deep_link_sub2
                    // See the user-invite section in FruitActivity.java
                    if (dlData.has("deep_link_sub2")){
                        referrerId = deepLinkObj.getStringValue("deep_link_sub2");
                        Log.d(LOG_TAG, "The referrerID is: " + referrerId);
                    }  else {
                        Log.d(LOG_TAG, "deep_link_sub2/Referrer ID not found");
                    }

                    if (fruitName == null || fruitName.equals("")){
                        Log.d(LOG_TAG, "deep_link_value returned null");
                        fruitName = deepLinkObj.getStringValue("fruit_name");
                        if (fruitName == null || fruitName.equals("")) {
                            Log.d(LOG_TAG, "could not find fruit name");
                            return;
                        }
                        Log.d(LOG_TAG, "fruit_name is " + fruitName + ". This is an old link");
                    }
                    Log.d(LOG_TAG, "The DeepLink will route to: " + fruitName);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "There's been an error: " + e.toString());
                    return;
                }
                goToFruit(fruitName, deepLinkObj);
            }
        });

        AppsFlyerConversionListener conversionListener =  new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionDataMap) {
                for (String attrName : conversionDataMap.keySet())
                    Log.d(LOG_TAG, "Conversion attribute: " + attrName + " = " + conversionDataMap.get(attrName));
                String status = Objects.requireNonNull(conversionDataMap.get("af_status")).toString();
                if(status.equals("Non-organic")){
                    if( Objects.requireNonNull(conversionDataMap.get("is_first_launch")).toString().equals("true")){
                        Log.d(LOG_TAG,"Conversion: First Launch");
                        //Deferred deep link in case of a legacy link
                        if(conversionDataMap.containsKey("fruit_name")){
                            if (conversionDataMap.containsKey("deep_link_value")) { //Not legacy link
                                Log.d(LOG_TAG,"onConversionDataSuccess: Link contains deep_link_value, deep linking with UDL");
                            }
                            else{ //Legacy link
                                conversionDataMap.put("deep_link_value", conversionDataMap.get("fruit_name"));
                                String fruitNameStr = (String) conversionDataMap.get("fruit_name");
                                DeepLink deepLinkData = mapToDeepLinkObject(conversionDataMap);
                                goToFruit(fruitNameStr, deepLinkData);
                            }
                        }
                    } else {
                        Log.d(LOG_TAG,"Conversion: Not First Launch");
                    }
                } else {
                    Log.d(LOG_TAG, "Conversion: This is an organic install.");
                }
                conversionData = conversionDataMap;
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d(LOG_TAG, "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                Log.d(LOG_TAG, "onAppOpenAttribution: This is fake call.");
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d(LOG_TAG, "error onAttributionFailure : " + errorMessage);
            }
        };

        appsflyer.init(afDevKey, conversionListener, this);
        appsflyer.start(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.myGalery);

        mcdbHelper = new MCDBHelper(this);
        mcdbHelper.getAllRows(MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME);
    }

    private void goToFruit(String fruitName, DeepLink dlData) {
        String fruitClassName = (fruitName.substring(0, 1).toUpperCase() + fruitName.substring(1)).concat("Activity");
        try {
            Class fruitClass = Class.forName(this.getPackageName().concat(".").concat(fruitClassName));
            Log.d(LOG_TAG, "Looking for class " + fruitClass);
            Intent intent = new Intent(getApplicationContext(), fruitClass);
            if (dlData != null) {
                // TODO - make DeepLink Parcelable
                String objToStr = new Gson().toJson(dlData);
                intent.putExtra(DL_ATTRS, objToStr);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Log.d(LOG_TAG, "Deep linking failed looking for " + fruitName);
            e.printStackTrace();
        }
    }

    public DeepLink mapToDeepLinkObject(Map <String, Object> conversionDataMap){
        try {
            String objToStr = new Gson().toJson(conversionDataMap);
            DeepLink deepLink = DeepLink.AFInAppEventType(new JSONObject(objToStr));
            return deepLink;
        }
        catch (org.json.JSONException e ){
            Log.d(LOG_TAG, "Error when converting map to DeepLink object: " + e.toString());
        }
        return null;
    }

    private void billingSetup() {

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        completePurchase(purchase);
                    }
                } else if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.USER_CANCELED) {
                    Log.i(TAG, "onPurchasesUpdated: Purchase Canceled");
                } else {
                    Log.i(TAG, "onPurchasesUpdated: Error");
                }
            }
        };

        billingClient = BillingClient.newBuilder(this)
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
                        .setProductId("premium_subs_test")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("one_month_premium_test")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("one_year_premuim_test")
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
                        productDetails = productDetailsList;
                        Log.i(TAG, "onProductDetailsResponse: No products");
                    }
                }
        );
    }

    void launchPurchaseFlow(View view, ProductDetails productDetails) {
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

        BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void completePurchase(Purchase item) {

        purchase = item;

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            runOnUiThread(() -> {
            });
    }

    public void consumePurchase(View view) {
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
                    runOnUiThread(() -> {
                    });
                }
            }
        };
        billingClient.consumeAsync(consumeParams, listener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myGalery:
                myGalery = new MyGalery();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, myGalery).commit();
                return true;

            case R.id.search:
                searchFragment = new Search();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, searchFragment).commit();
//                launchPurchaseFlow(item.getActionView(), productDetails.get(2));
                return true;

            case R.id.myDrawing:
                myDrawingFragment = new MyDrawing();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, myDrawingFragment).commit();
                return true;
        }
        return false;
    }


}
