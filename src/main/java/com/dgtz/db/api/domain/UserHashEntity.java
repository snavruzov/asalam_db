package com.dgtz.db.api.domain;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 6/2/14
 */
public class UserHashEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUser;
    private String hash;

    public UserHashEntity() {
    }

    public UserHashEntity(Long idUser, String hash) {
        this.idUser = idUser;
        this.hash = hash;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }


    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
