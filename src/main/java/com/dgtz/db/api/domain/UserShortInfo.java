package com.dgtz.db.api.domain;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

/**
 * Created by sardor on 12/22/15.
 */
public class UserShortInfo {
    private static final long serialVersionUID = 1L;

    public long idUser;
    public String username;
    public String fullname;
    public String avatar;
    public Boolean reFollow;
    public String location;
    public String about;
    public Long flwnum = 0l;

    public UserShortInfo(){}

    public Long getFlwnum() {
        return flwnum;
    }

    public void setFlwnum(Long flwnum) {
        this.flwnum = flwnum;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getReFollow() {
        return reFollow;
    }

    public void setReFollow(Boolean reFollow) {
        this.reFollow = reFollow;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);

    }
}
