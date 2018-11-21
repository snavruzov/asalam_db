package com.dgtz.db.api.domain;


/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/22/13
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */

public class DcTagsEntity {
    private long idTag;
    private String title;


    public long getIdTag() {
        return idTag;
    }

    public void setIdTag(long idTag) {
        this.idTag = idTag;
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

        DcTagsEntity that = (DcTagsEntity) o;

        if (idTag != that.idTag) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idTag ^ (idTag >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
