package com.dgtz.db.api.domain;


import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by sardor on 1/2/14.
 */

public class DcLiveMediaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idLive;
    private long idUser;
    private String description;
    private String rtmpUrl;
    private String dateadded;
    private Long idCateg;
    private Long idChannel;
    private Long idMedia;
    private String httpUrl;
    private Boolean stop;
    private String title;
    private String location;
    private int progress;
    private int props;
    private String tags;
    private String keyVal;
    private String chName;

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public Long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(Long idChannel) {
        this.idChannel = idChannel;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getProps() {
        return props;
    }

    public void setProps(int props) {
        this.props = props;
    }

    public Boolean getStop() {
        return stop;
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }

    public String getKeyVal() {
        return keyVal;
    }

    public void setKeyVal(String keyVal) {
        this.keyVal = keyVal;
    }

    public long getIdLive() {
        return idLive;
    }

    public void setIdLive(long idLive) {
        this.idLive = idLive;
    }

    public long getIdUser() {
        return idUser;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }


    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }


    public Long getIdCateg() {
        return idCateg;
    }

    public void setIdCateg(Long idCateg) {
        this.idCateg = idCateg;
    }

    public Long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(Long idMedia) {
        this.idMedia = idMedia;
    }


    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
