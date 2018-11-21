package com.dgtz.db.api.beans;

import com.dgtz.mcache.api.utils.GsonInsta;
import com.google.gson.Gson;

/**
 * BroCast.
 * Copyright: Sardor Navruzov
 * 2013-2016.
 */
public class ScreenRotation {
    private static final long serialVersionUID = 1L;

    private Long time;
    private int rotation;

    public ScreenRotation() {
    }

    public ScreenRotation(Long time, int rotation) {
        this.time = time;
        this.rotation = rotation;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
