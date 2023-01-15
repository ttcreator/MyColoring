package com.ttcreator.mycoloring.model;

public class CategoryItemModel {

    private String name;
    private String imgUrl;
    private String category;
    private int id, imgKey, haveAds, premiumStatus, newStatus;

    public CategoryItemModel() {
    }

    public CategoryItemModel(String name, String imgUrl, String category, int id, int imgKey,
                             int haveAds, int premiumStatus, int newStatus) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.category = category;
        this.imgKey = imgKey;
        this.haveAds = haveAds;
        this.id = id;
        this.premiumStatus = premiumStatus;
        this.newStatus = newStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setimgKey(int imgKey) {
        this.imgKey = imgKey;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getCategory() {
        return category;
    }

    public int getimgKey() {
        return imgKey;
    }

    public void setHaveAds(int haveAds) {
        this.haveAds = haveAds;
    }

    public int getHaveAds() {
        return haveAds;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getPremiumStatus () {
        return premiumStatus;
    }

    public void setPremiumStatus (int premiumStatus) {
        this.premiumStatus = premiumStatus;
    }

    public int getNewStatus () {
        return newStatus;
    }

    public void setNewStatus (int newStatus) {
        this.newStatus = newStatus;
    }
}
