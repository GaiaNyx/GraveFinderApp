package com.example.gravefinder;

public class PDFmemo {

    private String id;
    private String name;
    private String storageFilePath;

    public PDFmemo(String id, String name, String storageFilePath) {
        this.id = id;
        this.name = name;
        this.storageFilePath = storageFilePath;
    }

    public PDFmemo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageFilePath() {
        return storageFilePath;
    }

    public void setStorageFilePath(String storageFilePath) {
        this.storageFilePath = storageFilePath;
    }
}
