package com.dgtz.db.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

/**
 * Created by Sardor Navruzov on 8/17/15.
 * Copyrights Digitizen Co.
 */
public class EmailRecipients {

    private static final long serialVersionUID = 1L;

    private Long idUser;
    private String email;

    public EmailRecipients() {
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);

    }
}
