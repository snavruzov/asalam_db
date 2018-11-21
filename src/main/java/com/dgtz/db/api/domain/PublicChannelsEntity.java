package com.dgtz.db.api.domain;


import com.dgtz.db.api.factory.GsonInsta;

import java.io.Serializable;

/**
 * Created by root on 1/15/14.
 */

public class PublicChannelsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idChannel;
    private String title;
    private String description;
    private String avatar;
    private String wall="empty";
    private Long ownerIdUser;
    private Long mcount = 0l;
    private Long ucount = 0l;
    private Boolean enabled;
    private String dateadded;
    private String username;
    private int privacy;
    private boolean followed;
    private Integer access;
    private short state;

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public String getWall() {
        return wall;
    }

    public void setWall(String wall) {
        this.wall = wall;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Long getUcount() {
        return ucount;
    }

    public void setUcount(Long ucount) {
        this.ucount = ucount;
    }

    public long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(long idChannel) {
        this.idChannel = idChannel;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public Long getMcount() {
        return mcount;
    }

    public void setMcount(Long mcount) {
        this.mcount = mcount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public Long getOwnerIdUser() {
        return ownerIdUser;
    }

    public void setOwnerIdUser(Long ownerIdUser) {
        this.ownerIdUser = ownerIdUser;
    }


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return GsonInsta.getInstance().toJson(this);
    }
}
