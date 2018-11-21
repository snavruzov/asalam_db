package com.dgtz.db.api.domain;


/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/21/13
 * Time: 9:29 PM
 * To change this template use File | Settings | File Templates.
 */

public class DcActivityEntity {
    private long idActivity;


    public long getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(long idActivity) {
        this.idActivity = idActivity;
    }

    private long idUser;


    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcActivityEntity that = (DcActivityEntity) o;

        if (idActivity != that.idActivity) return false;
        if (idUser != that.idUser) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idActivity ^ (idActivity >>> 32));
        result = 31 * result + (int) (idUser ^ (idUser >>> 32));
        return result;
    }
}
