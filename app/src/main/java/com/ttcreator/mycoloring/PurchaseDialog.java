package com.ttcreator.mycoloring;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.DialogFragment;

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
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.ttcreator.mycoloring.adapters.RecyclerViewAdapter;

import java.util.List;

public class PurchaseDialog extends DialogFragment {

    private View view;
    private AppCompatButton oneWeekPurchaseButton, oneMonthPurchaseButton, oneYearPurchaseButton;
    private BillingClient billingClient;
    private List<ProductDetails> productDetails;
    private Purchase purchase;

    private FloatingActionButton closeButton;

    static final String TAG = "InAppPurchaseTag";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.purchase_dialog_fragment, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            getDialog().getWindow().setGravity(Gravity.CENTER);
        }

        oneWeekPurchaseButton = view.findViewById(R.id.oneWeekPurchaseButton);
        oneMonthPurchaseButton = view.findViewById(R.id.oneMonthPurchaseButton);
        oneYearPurchaseButton = view.findViewById(R.id.oneYearPurchaseButton);
        closeButton = view.findViewById(R.id.closeButton);

        billingSetup();

        oneWeekPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPurchaseFlow(v, productDetails.get(2));
            }
        });

        oneMonthPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPurchaseFlow(v, productDetails.get(0));
            }
        });

        oneYearPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPurchaseFlow(v, productDetails.get(1));
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
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

        billingClient = BillingClient.newBuilder(getContext())
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


    private void completePurchase(Purchase item) {

        purchase = item;

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    SharedPreferencesFactory.saveBoolean(getContext(), "isPurchase", true);
                    Bundle result = new Bundle();
                    result.putString("isPurchaseOK", "OK");
                    getParentFragmentManager().setFragmentResult("resultPurchasedToFragment", result);
                    getDialog().dismiss();
                });
            }
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
                        getActivity().runOnUiThread(() -> {
                            oneWeekPurchaseButton.setText("Weekly: " + productDetails.get(2).getSubscriptionOfferDetails()
                                    .get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice());
                            oneMonthPurchaseButton.setText("Monthly: " + productDetails.get(0).getSubscriptionOfferDetails()
                                    .get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice());
                            oneYearPurchaseButton.setText("Yearly: " + productDetails.get(1).getSubscriptionOfferDetails()
                                    .get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice());
                        });

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

        BillingResult billingResult = billingClient.launchBillingFlow(getActivity(), billingFlowParams);
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
                    getActivity().runOnUiThread(() -> {
                    });
                }
            }
        };
        billingClient.consumeAsync(consumeParams, listener);
    }

    void checkSubscription(){

        billingClient = BillingClient.newBuilder(getContext()).enablePendingPurchases().setListener((billingResult, list) -> {}).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){
                                    Log.d("testOffer",list.size() +" size");
                                    if(list.size()>0){
                                      //  prefs.setPremium(1); // set 1 to activate premium feature
                                        int i = 0;
                                        for (Purchase purchase: list){
                                            //Here you can manage each product, if you have multiple subscription
                                            Log.d("testOffer",purchase.getOriginalJson()); // Get to see the order information
                                            Log.d("testOffer", " index" + i);
                                            i++;
                                        }
                                    } else {
                                        // prefs.setPremium(0); // set 0 to de-activate premium feature
                                    }
                                }
                            });

                }

            }
        });
    }

}
