package com.ttcreator.mycoloring;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.DialogFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ttcreator.mycoloring.data.MCDataContract;


public class MenuDialogFragment extends DialogFragment {

    private ImageView imageView;
    private String uriToSting;
    private String urlImagePosition, namePosition, categoryPosition, stateImagePosition;
    private Integer posit, imageKeyPosition;
    private Button continueButton, retyButton, deleteButton;
    private View root;
    private Uri contentUri;
    private RelativeLayout parentDialogFragmentLayout;
    private LinearLayoutCompat parentThreeButton;
    private FloatingActionButton closeButton;
    private boolean isUserHavePrem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.menu_dialog_fragment_popup, container, false);


        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            getDialog().getWindow().setGravity(Gravity.BOTTOM);
        }

        isUserHavePrem = SharedPreferencesFactory.getBoolean(requireContext(), "isPurchase");
        closeButton = root.findViewById(R.id.closeButton);
        parentThreeButton = root.findViewById(R.id.parentThreeButton);
        imageView = root.findViewById(R.id.imageView);
        continueButton = root.findViewById(R.id.buttonContinue);
        retyButton = root.findViewById(R.id.buttonRetry);
        deleteButton = root.findViewById(R.id.buttonDelete);
        parentDialogFragmentLayout = (RelativeLayout) root.findViewById(R.id.parentDialogFragmentLayout);
        uriToSting = getArguments().getString("uriToSting");
        urlImagePosition = getArguments().getString("urlImagePosition");
        namePosition = getArguments().getString("nameImage");
        categoryPosition = getArguments().getString("categoryPosition");
        stateImagePosition = getArguments().getString("statePosition");
        posit = getArguments().getInt("position");
        imageKeyPosition = getArguments().getInt("keyPosition");
        contentUri = Uri.parse(uriToSting);

        imageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        imageView.getLayoutParams().width = MyApp.getScreenWidth(requireContext()) * 3/4;
        Glide.with(getContext())
                .load(stateImagePosition)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dismiss();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isUserHavePrem) {
                    SplashScreenActivity.adsManager.showInterstitialAds(requireActivity());
                    InterstitialAd mInterstitialAds = AdsManager.mInterstitialAd;
                    mInterstitialAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Intent intent = new Intent(v.getContext(),
                                    ColoringActivity.class);
                            intent.putExtra("urlImagePosition", urlImagePosition);
                            intent.putExtra("nameImage", namePosition);
                            intent.putExtra("keyPosition", imageKeyPosition);
                            intent.putExtra("categoryPosition", categoryPosition);
                            intent.putExtra("statePosition", stateImagePosition);
                            intent.putExtra("position", posit);
                            intent.setData(contentUri);
                            v.getContext().startActivity(intent);
                            dismiss();
                        }
                    });
                }
                else{
                    Intent intent = new Intent(v.getContext(),
                            ColoringActivity.class);
                    intent.putExtra("urlImagePosition", urlImagePosition);
                    intent.putExtra("nameImage", namePosition);
                    intent.putExtra("keyPosition", imageKeyPosition);
                    intent.putExtra("categoryPosition", categoryPosition);
                    intent.putExtra("statePosition", stateImagePosition);
                    intent.putExtra("position", posit);
                    intent.setData(contentUri);
                    v.getContext().startActivity(intent);
                    dismiss();
                }
            }
        });

        retyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isUserHavePrem) {
                    SplashScreenActivity.adsManager.showInterstitialAds(requireActivity());
                    InterstitialAd mInterstitialAds = AdsManager.mInterstitialAd;
                    mInterstitialAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setMessage("This image was delete, are you sure? All progress will be lost")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ContentResolver contentResolver = requireContext().getContentResolver();
                                            ContentValues cv = new ContentValues();
                                            cv.putNull(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
                                            int rowsUpdate = contentResolver.update(contentUri, cv, null, null);
                                            if (rowsUpdate != 0) {
                                                stateImagePosition = null;
                                            }
                                            Intent intent = new Intent(v.getContext(),
                                                    ColoringActivity.class);
                                            intent.putExtra("urlImagePosition", urlImagePosition);
                                            intent.putExtra("nameImage", namePosition);
                                            intent.putExtra("keyPosition", imageKeyPosition);
                                            intent.putExtra("categoryPosition", categoryPosition);
                                            intent.putExtra("statePosition", stateImagePosition);
                                            intent.putExtra("position", posit);
                                            intent.setData(contentUri);
                                            v.getContext().startActivity(intent);
                                            dismiss();
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
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("This image was delete, are you sure? All progress will be lost")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ContentResolver contentResolver = requireContext().getContentResolver();
                                    ContentValues cv = new ContentValues();
                                    cv.putNull(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
                                    int rowsUpdate = contentResolver.update(contentUri, cv, null, null);
                                    if (rowsUpdate != 0) {
                                        stateImagePosition = null;
                                    }
                                    Intent intent = new Intent(v.getContext(),
                                            ColoringActivity.class);
                                    intent.putExtra("urlImagePosition", urlImagePosition);
                                    intent.putExtra("nameImage", namePosition);
                                    intent.putExtra("keyPosition", imageKeyPosition);
                                    intent.putExtra("categoryPosition", categoryPosition);
                                    intent.putExtra("statePosition", stateImagePosition);
                                    intent.putExtra("position", posit);
                                    intent.setData(contentUri);
                                    v.getContext().startActivity(intent);
                                    dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                }

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isUserHavePrem) {
                    SplashScreenActivity.adsManager.showInterstitialAds(requireActivity());
                    InterstitialAd mInterstitialAds = AdsManager.mInterstitialAd;;
                    mInterstitialAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setMessage("This image was delete, are you sure? All progress will be lost")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ContentResolver contentResolver = requireContext().getContentResolver();
                                            ContentValues cv = new ContentValues();
                                            cv.putNull(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
                                            int rowsUpdate = contentResolver.update(contentUri, cv, null, null);
                                            if (rowsUpdate != 0) {
                                                stateImagePosition = null;
                                            }
                                            dismiss();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            builder.show();
                        }
                    });
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("This image was delete, are you sure? All progress will be lost")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ContentResolver contentResolver = requireContext().getContentResolver();
                                    ContentValues cv = new ContentValues();
                                    cv.putNull(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
                                    int rowsUpdate = contentResolver.update(contentUri, cv, null, null);
                                    if (rowsUpdate != 0) {
                                        stateImagePosition = null;
                                    }
                                    dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                }

            }
        });

        return root;
    }



    @Override
    public void onStop() {
        super.onStop();
        dismissAllowingStateLoss();
    }
}