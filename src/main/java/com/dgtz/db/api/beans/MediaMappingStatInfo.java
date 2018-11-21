package com.dgtz.db.api.beans;

import com.brocast.riak.api.beans.DcMediaEntity;
import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by sardor on 6/1/16.
 */
public class MediaMappingStatInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idMedia;
    private String title;
    private Integer duration;
    private String dateadded;
    private long idUser;
    private String username;
    private String method;
    private long amount;
    private String avatar;
    private String thumb_webp;
    private String thumb;
    private long liked;
    private String currentTime;
    private String location;
    private String video_url;
    private String video_hls;
    private String latLng;
    private String ratio;
    private String start_time;


    public MediaMappingStatInfo() {
    }

    public MediaMappingStatInfo(DcMediaEntity entity, String latLng, String video_url, String video_hls) {
        this.idMedia = entity.getIdMedia();
        this.title = entity.getTitle();
        this.dateadded = entity.getDateadded();
        this.idUser = entity.getIdUser();

        GeoCoordination coordination = new GeoCoordination();
        if(latLng!=null && !latLng.isEmpty())
        {
            String[] latLngParam = latLng.split(",");
            coordination.setLat(Double.valueOf(latLngParam[0]));
            coordination.setLng(Double.valueOf(latLngParam[1]));
        }

        this.video_url = video_url;
        this.video_hls = video_hls;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVideo_hls() {
        return video_hls;
    }

    public void setVideo_hls(String video_hls) {
        this.video_hls = video_hls;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(long idMedia) {
        this.idMedia = idMedia;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public long getLiked() {
        return liked;
    }

    public void setLiked(long liked) {
        this.liked = liked;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getThumb_webp() {
        return thumb_webp;
    }

    public void setThumb_webp(String thumb_webp) {
        this.thumb_webp = thumb_webp;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
