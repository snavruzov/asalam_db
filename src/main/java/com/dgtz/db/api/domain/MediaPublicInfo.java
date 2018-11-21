package com.dgtz.db.api.domain;

import com.google.gson.GsonBuilder;

import java.util.Set;


/**
 * BroCast.
 * Copyright: Sardor Navruzov
 * 2013-2016.
 */
public class MediaPublicInfo {
    private long idMedia;
    private String title;
    private String url;
    private Short duration;
    private String dateadded;
    private int idCategory = 1;
    private long idUser;
    private String description;
    private Integer progress;
    private String location;
    private long amount;
    private long ccount;
    private long idChannel;
    private String channelTitle;
    private String username;
    private String avatar;
    private Boolean verified = false;
    private String thumb;
    private String thumb_webp;
    private String lang;
    private String sharedby;

    private long liked;
    private int props;
    private String currenTime;
    private Set<String> tags;
    private String ratio = "4:3";
    private Boolean followed;
    private String start_time;
    private String method = "upload"; //live, event, upload, recorded

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getSharedby() {
        return sharedby;
    }

    public void setSharedby(String sharedby) {
        this.sharedby = sharedby;
    }

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Short getDuration() {
        return duration;
    }

    public void setDuration(Short duration) {
        this.duration = duration;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public long getIdUser() {
        return idUser;
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

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getCcount() {
        return ccount;
    }

    public void setCcount(long ccount) {
        this.ccount = ccount;
    }

    public long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(long idChannel) {
        this.idChannel = idChannel;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getThumb_webp() {
        return thumb_webp;
    }

    public void setThumb_webp(String thumb_webp) {
        this.thumb_webp = thumb_webp;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public long getLiked() {
        return liked;
    }

    public void setLiked(long liked) {
        this.liked = liked;
    }

    public int getProps() {
        return props;
    }

    public void setProps(int props) {
        this.props = props;
    }

    public String getCurrenTime() {
        return currenTime;
    }

    public void setCurrenTime(String currenTime) {
        this.currenTime = currenTime;
    }


    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
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

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
