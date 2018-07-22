package com.example.kowshick.travelmate;

import java.io.Serializable;

public class Moments implements Serializable{
    private String momentsId;
    private String photourl;
    private String captions;

    public Moments() {
    }

    public Moments(String momentsId, String photourl, String captions) {
        this.momentsId = momentsId;
        this.photourl = photourl;
        this.captions = captions;
    }

    public String getMomentsId() {
        return momentsId;
    }

    public String getPhotourl() {
        return photourl;
    }

    public String getCaptions() {
        return captions;
    }
}
