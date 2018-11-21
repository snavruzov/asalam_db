package com.dgtz.db.api.domain;


import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;


/**
 * Created by sardor on 1/3/14.
 */

public class DcMediaEntityTMP implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idMedia;
    private String title;
    private String url;
    private Short duration;
    private String dateadded;
    private int idCategory = 1;
    private boolean isLive;
    private long idUser;
    private String description;
    private Integer progress;
    private String location;
    private long amount;
    private long ccount;
    private long idChannel;
    private boolean showLocation;
    private String channelTitle;
    private String formattedLocation;
    private String username;
    private String contentType;
    private String ctitle;
    private String avatar;
    private Boolean verified = false;
    private String city;
    private String thumb;
    private String thumb_webp;
    private String lang;

    private long liked;
    private long disliked;
    private int props;
    private String currenTime;
    private String tags;
    private String ratio = "4:3";

    private String method = "upload";

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getCurrenTime() {
        return currenTime;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = (ratio == null ? "4:3" : ratio);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAvatar() {
        return avatar;
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

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCurrenTime(String currenTime) {
        this.currenTime = currenTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(long idMedia) {
        this.idMedia = idMedia;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public String getFormattedLocation() {
        return formattedLocation;
    }

    public void setFormattedLocation(String formattedLocation) {
        this.formattedLocation = formattedLocation;
    }

    public int getProps() {
        return props;
    }

    public void setProps(int props) {
        this.props = props;
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


    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
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

    public long getLiked() {
        return liked;
    }

    public void setLiked(long liked) {
        this.liked = liked;
    }

    public long getDisliked() {
        return disliked;
    }

    public void setDisliked(long disliked) {
        this.disliked = disliked;
    }


    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public boolean isShowLocation() {
        return showLocation;
    }

    public void setShowLocation(boolean showLocation) {
        this.showLocation = showLocation;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);

    }
}
