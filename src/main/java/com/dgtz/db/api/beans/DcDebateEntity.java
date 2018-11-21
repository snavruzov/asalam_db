package com.dgtz.db.api.beans;

import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * BroCast.
 * Copyright: Sardor Navruzov
 * 2013-2017.
 */
public class DcDebateEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer startID;
    private Long idMedia;
    private String avatar;
    private String username;
    private Long idUser;
    private String rtmp_url;
    private String duration;
    private String mp4_url;
    private String hls_url;
    private String position;//sbs, pop
    private List<ScreenRotation> rotations;

    public DcDebateEntity() {
    }

    public DcDebateEntity(Integer startID, Long idMedia, Long idUser, String position) {
        this.startID = startID;
        this.idMedia = idMedia;
        this.idUser = idUser;
        this.position = position;
        this.hls_url = Constants.encryptAmazonURL(idUser, idMedia, "", "hls", "");
        this.mp4_url = Constants.encryptAmazonURL(idUser, idMedia, "_hi.mp4", "v", "");
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<ScreenRotation> getRotations() {
        return rotations;
    }

    public void setRotations(List<ScreenRotation> rotations) {
        this.rotations = rotations;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRtmp_url() {
        return rtmp_url;
    }

    public void setRtmp_url(String rtmp_url) {
        this.rtmp_url = rtmp_url;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Integer getStartID() {
        return startID;
    }

    public void setStartID(Integer startID) {
        this.startID = startID;
    }

    public Long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(Long idMedia) {
        this.idMedia = idMedia;
    }

    public String getMp4_url() {
        return mp4_url;
    }

    public void setMp4_url(String mp4_url) {
        this.mp4_url = mp4_url;
    }

    public String getHls_url() {
        return hls_url;
    }

    public void setHls_url(String hls_url) {
        this.hls_url = hls_url;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
