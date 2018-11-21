package com.dgtz.db.api.domain;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/21/13
 * Time: 9:29 PM
 * To change this template use File | Settings | File Templates.
 */

public class DcBonusEntity {


    private long idBonus;


    public long getIdBonus() {
        return idBonus;
    }

    public void setIdBonus(long idBonus) {
        this.idBonus = idBonus;
    }

    private long idUser;


    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    private String title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String description;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private byte[] logo;


    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcBonusEntity that = (DcBonusEntity) o;

        if (idBonus != that.idBonus) return false;
        if (idUser != that.idUser) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (!Arrays.equals(logo, that.logo)) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idBonus ^ (idBonus >>> 32));
        result = 31 * result + (int) (idUser ^ (idUser >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (logo != null ? Arrays.hashCode(logo) : 0);
        return result;
    }
}
