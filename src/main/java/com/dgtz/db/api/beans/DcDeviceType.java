package com.dgtz.db.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

/**
 * Created by sardor on 12/8/15.
 */
public class DcDeviceType {

    private static final long serialVersionUID = 1L;

    public DcDeviceType() {
    }

    private long idUser;
    private int devType; //Android or iOS

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);

    }
}
