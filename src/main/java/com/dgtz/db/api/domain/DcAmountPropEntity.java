package com.dgtz.db.api.domain;


/**
 * Created by sardor on 1/9/14.
 */

public class DcAmountPropEntity {
    private long idAmount;
    private String title;


    public long getIdAmount() {
        return idAmount;
    }

    public void setIdAmount(long idAmount) {
        this.idAmount = idAmount;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcAmountPropEntity that = (DcAmountPropEntity) o;

        if (idAmount != that.idAmount) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idAmount ^ (idAmount >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
