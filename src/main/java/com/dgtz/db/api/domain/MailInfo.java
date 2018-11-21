package com.dgtz.db.api.domain;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 3/29/14
 */
public class MailInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String info0;
    private String info1;
    private String info2;
    private String[] info3;
    private String email;
    private String hash;
    private String description;
    private int type;
    private String url;
    private String error;

    public MailInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String[] getInfo3() {
        return info3;
    }

    public void setInfo3(String[] info3) {
        this.info3 = info3;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getInfo0() {
        return info0;
    }

    public void setInfo0(String info0) {
        this.info0 = info0;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
