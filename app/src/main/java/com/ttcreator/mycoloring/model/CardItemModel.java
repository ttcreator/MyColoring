package com.ttcreator.mycoloring.model;

import android.media.Image;

public class CardItemModel {

    private String name;
    private String imgUrl;
    private String category;

    public CardItemModel () {
    }

    public CardItemModel(String name, String imgUrl, String category) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
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

    public String getCategory() {
        return category;
    }
}
