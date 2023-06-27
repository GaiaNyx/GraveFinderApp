package com.example.gravefinder;

import android.view.View;

public class ImageMemo {
    private String id;
    private String ownerName;
    private String storageFilePath;

    public ImageMemo(String id, String ownerName, String storageFilePath) {
        this.id = id;
        this.ownerName = ownerName;
        this.storageFilePath = storageFilePath;
    }

    public ImageMemo() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStorageFilePath() {
        return storageFilePath;
    }

    public void setStorageFilePath(String storageFilePath) {
        this.storageFilePath = storageFilePath;
    }
}
