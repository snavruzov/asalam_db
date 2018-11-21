package com.dgtz.db.api.domain;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 10/23/14
 */
public class NotifyMethod {

    public boolean email;
    public boolean notification;

    public NotifyMethod() {
        this.email = false;
        this.notification = true;
    }

    public NotifyMethod(boolean email) {
        this.email = email;
        this.notification = true;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public static NotifyMethod getDefault() {
        return new NotifyMethod();
    }

    public static NotifyMethod getDefault(boolean email) {
        return new NotifyMethod(email);
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);

    }
}
