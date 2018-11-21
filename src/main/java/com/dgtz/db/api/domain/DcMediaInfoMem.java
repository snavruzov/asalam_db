package com.dgtz.db.api.domain;

/**
 * Created by root on 1/27/14.
 */
public class DcMediaInfoMem {

    private String title;
    private String dateadded;
    private String url;
    private String isLive;
    private String username;
    private String idUser;
    private String vCount;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getvCount() {
        return vCount;
    }

    public void setvCount(String vCount) {
        this.vCount = vCount;
    }

    @Override
    public String toString() {
        return
                (title == null ? "NULL" : title) + "," + (dateadded == null ? "NULL" : dateadded) + "," + (url == null ? "NULL" : url) + "," + (isLive == null ? "FALSE" : isLive) + "," + (username == null ? "NULL" : username) + "," + (idUser == null ? "NULL" : idUser) + "," + (vCount == null ? "NULL" : vCount);
    }
}
