package com.ttcreator.mycoloring;

import com.ttcreator.mycoloring.model.CacheImageModel;

import java.util.List;

public interface OnLoadUserPaintListener {
    void loadUserPaintFinished(List<CacheImageModel> list);
}

