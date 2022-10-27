package com.ttcreator.mycoloring;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;

import uk.co.senab.photoview.PhotoViewAttacher;

public class MyPhotoViewAttacher extends androidx.appcompat.widget.AppCompatImageView {

    PhotoViewAttacher mAttacher;

    public MyPhotoViewAttacher(Context context) {
        super(context);
    }

    public MyPhotoViewAttacher(@NonNull Context context, PhotoViewAttacher mAttacher) {
        super(context);
        this.mAttacher = mAttacher;
    }

    public MyPhotoViewAttacher(@NonNull Context context, @Nullable AttributeSet attrs, PhotoViewAttacher mAttacher) {
        super(context, attrs);
        this.mAttacher = mAttacher;
    }

    public MyPhotoViewAttacher(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, PhotoViewAttacher mAttacher) {
        super(context, attrs, defStyleAttr);
        this.mAttacher = mAttacher;
    }

    @Override
    // setImageBitmap calls through to this method
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (null != mAttacher) {
            mAttacher.update();
        }
    }
}
