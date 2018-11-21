package com.dgtz.db.api.domain;


import com.dgtz.db.api.factory.GsonInsta;

import java.io.Serializable;

/**
 * Created by sardor on 1/3/14.
 */

public class DcCategoriesEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private long idCateg;
    private String title;
    private String description;


    public long getIdCateg() {
        return idCateg;
    }

    public void setIdCateg(long idCateg) {
        this.idCateg = idCateg;
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

    @Override
    public String toString() {
        return GsonInsta.getInstance().toJson(this);
    }
}
