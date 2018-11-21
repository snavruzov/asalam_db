package com.dgtz.db.api.domain;


import com.dgtz.db.api.factory.GsonInsta;

import java.io.Serializable;

/**
 * Created by sardor on 1/4/14.
 */
public class MediaListInfo implements Serializable{

    private static final long serialVersionUID = 1L;

    private long idMedia;
    private String title;
    private String description;
    private String url;
    private String contentType;
    private Short duration;
    private Long ccount;
    private String dateadded;
    private int idCategory;
    private boolean isLive;
    private long idUser;
    private String username;
    private String ctitle;
    private String location;
    private long amount;
    private long idChannel;
    private String tags;
    private String city;
    private String avatar;

    private Integer progress;
    private Integer props;
    private String alt_user;

    private long liked;
    private long disliked;
    private String currenTime;


    public MediaListInfo() {
    }

    public String getCurrenTime() {
        return currenTime;
    }

    public void setCurrenTime(String currenTime) {
        this.currenTime = currenTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCity() {
        return city;
    }

    public Integer getProps() {
        return props;
    }

    public void setProps(Integer props) {
        this.props = props;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getAlt_user() {
        return alt_user;
    }

    public void setAlt_user(String alt_user) {
        this.alt_user = alt_user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(long idChannel) {
        this.idChannel = idChannel;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Long getCcount() {
        return ccount;
    }

    public void setCcount(Long ccount) {
        this.ccount = ccount;
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

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return GsonInsta.getInstance().toJson(this);
    }

}
