package com.bypassmobile.octo.image;


import android.graphics.Bitmap;

import com.squareup.picasso.Cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class ImageCache implements Cache{

    //BitmapStamp is a simple datastructure that lets us keep track of the time we downloaded an image
    //if when we go to retrieve an image it has been too long, we will just discard the result
    private static class BitmapStamp{
        Bitmap mBitmap;
        long mTimestamp;

        public BitmapStamp(Bitmap bitmap, long currentTime){
            mBitmap = bitmap;
            mTimestamp = currentTime;
        }
    }
    private long TIMEOUT_TIME = 3600000;
    private Map<String,BitmapStamp> cacheMap = new LinkedHashMap<>();

    @Override
    public Bitmap get(String stringResource) {
        BitmapStamp stamp = cacheMap.get(stringResource);
        if((stamp == null)|| ((System.currentTimeMillis() - stamp.mTimestamp))>TIMEOUT_TIME) {
            return null;
        }else{
            return stamp.mBitmap;
        }
    }

    @Override
    public void set(String stringResource, Bitmap bitmap) {
        cacheMap.put(stringResource,new BitmapStamp(bitmap, System.currentTimeMillis()));
    }

    @Override
    public int size() {
        return cacheMap.size();
    }

    @Override
    public int maxSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }
}
