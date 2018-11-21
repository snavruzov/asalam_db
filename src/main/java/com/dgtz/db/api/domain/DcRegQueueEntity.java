package com.dgtz.db.api.domain;


import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by sardor on 1/6/14.
 */

public class DcRegQueueEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idRegQueue;
    private long idUser;
    private Integer status;
    private Timestamp expire;
    private Timestamp datereg;
    private String activationNum;


    public long getIdRegQueue() {
        return idRegQueue;
    }

    public void setIdRegQueue(long idRegQueue) {
        this.idRegQueue = idRegQueue;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public Timestamp getExpire() {
        return expire;
    }

    public void setExpire(Timestamp expire) {
        this.expire = expire;
    }

    public Timestamp getDatereg() {
        return datereg;
    }

    public void setDatereg(Timestamp datereg) {
        this.datereg = datereg;
    }

    public String getActivationNum() {
        return activationNum;
    }

    public void setActivationNum(String activationNum) {
        this.activationNum = activationNum;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
