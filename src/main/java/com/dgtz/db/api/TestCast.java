package com.dgtz.db.api;


/**
 * Created by sardor on 1/3/14.
 */
public class TestCast {

    private long idMedia;
    private String title;
    private String url;
    private Short duration;
    private Long rootComment;
    private String dateadded;
    private int idCategory;
    private boolean isLive;


    public TestCast(long idMedia, String title, String url, Short duration, Long rootComment, String dateadded, int idCategory, boolean isLive) {
        this.idMedia = idMedia;
        this.title = title;
        this.url = url;
        this.duration = duration;
        this.rootComment = rootComment;
        this.dateadded = dateadded;
        this.idCategory = idCategory;
        this.isLive = isLive;
    }

    public long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(long idMedia) {
        this.idMedia = idMedia;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Short getDuration() {
        return duration;
    }

    public void setDuration(Short duration) {
        this.duration = duration;
    }

    public Long getRootComment() {
        return rootComment;
    }

    public void setRootComment(Long rootComment) {
        this.rootComment = rootComment;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCast)) return false;

        TestCast testCast = (TestCast) o;

        if (idCategory != testCast.idCategory) return false;
        if (idMedia != testCast.idMedia) return false;
        if (isLive != testCast.isLive) return false;
        if (dateadded != null ? !dateadded.equals(testCast.dateadded) : testCast.dateadded != null) return false;
        if (duration != null ? !duration.equals(testCast.duration) : testCast.duration != null) return false;
        if (rootComment != null ? !rootComment.equals(testCast.rootComment) : testCast.rootComment != null)
            return false;
        if (title != null ? !title.equals(testCast.title) : testCast.title != null) return false;
        if (url != null ? !url.equals(testCast.url) : testCast.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idMedia ^ (idMedia >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (rootComment != null ? rootComment.hashCode() : 0);
        result = 31 * result + (dateadded != null ? dateadded.hashCode() : 0);
        result = 31 * result + idCategory;
        result = 31 * result + (isLive ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestCast{" +
                "idMedia=" + idMedia +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", duration=" + duration +
                ", rootComment=" + rootComment +
                ", dateadded=" + dateadded +
                ", idCategory=" + idCategory +
                ", isLive=" + isLive +
                '}';
    }
}

