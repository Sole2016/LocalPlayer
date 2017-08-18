package com.zy.vplayer.utils;

import android.content.Context;

public class LocalCache {
    private String cacheFileName = "Queue.cv";

    private static class Instance {
        private static LocalCache cache = new LocalCache();
    }

    private LocalCache() {
    }

    public static LocalCache getInstance() {
        return Instance.cache;
    }

    public boolean hasLocalCache(Context context) {
        String innerPath = FileUtils.getStoragePath(context, false);
        return FileUtils.hasFile(innerPath + "/Cache/" + cacheFileName);
    }

    public Object getLocalCache(Context context) {
        String innerPath = FileUtils.getStoragePath(context, false);
        String filePath = innerPath + "/Cache/" + cacheFileName;
        return FileUtils.readDataFromFile(filePath);
    }

    public void saveLocalCache(Context context,Object obj) {
        String innerPath = FileUtils.getStoragePath(context, false);
        FileUtils.writeDataToFile(obj,innerPath+"/Cache",cacheFileName);
    }
}
