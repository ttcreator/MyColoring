package com.ttcreator.mycoloring.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CacheImageModel implements Parcelable {

    private int id;
    private String imageCacheUrl;
    private String name;
    private String category;
    private int imageKey;
    private String state;
    private int new_status, haveAds, premiumStatus;


    public CacheImageModel() {
    }

    public CacheImageModel(int id, String imageCacheUrl, String name, String category, int imageKey,
                            String state, int new_status, int haveAds, int premiumStatus) {
        this.id = id;
        this.imageCacheUrl = imageCacheUrl;
        this.name = name;
        this.category = category;
        this.imageKey = imageKey;
        this.state = state;
        this.new_status = new_status;
        this.haveAds = haveAds;
        this.premiumStatus = premiumStatus;
    }

    public CacheImageModel (String imageCacheUrl, String name, String category) {
        this.imageCacheUrl = imageCacheUrl;
        this.name = name;
        this.category = category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageCacheUrl(String imageCacheUrl) {
        this.imageCacheUrl = imageCacheUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setKey(int imageKey) {
        this.imageKey = imageKey;
    }

    public int getId() {
        return id;
    }

    public String getImageCacheUrl() {
        return imageCacheUrl;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getImageKey() {
        return imageKey;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setNewStatus(int new_status) {
        this.new_status = new_status;
    }

    public int getNewStatus() {
        return new_status;
    }

    public void setHaveAds(int haveAds) {
        this.haveAds = haveAds;
    }

    public int getHaveAds() {
        return haveAds;
    }

    public void setPremiumStatus(int premiumStatus) {
        this.premiumStatus = premiumStatus;
    }

    public int getPremiumStatus () {
        return premiumStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
