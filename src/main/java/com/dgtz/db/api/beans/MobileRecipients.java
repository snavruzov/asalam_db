package com.dgtz.db.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

/**
 * Created by Sardor Navruzov on 8/17/15.
 * Copyrights Digitizen Co.
 */
public class MobileRecipients {

    private static final long serialVersionUID = 1L;

    private Long idUser;
    private String device;

    public MobileRecipients() {
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);

    }
}
