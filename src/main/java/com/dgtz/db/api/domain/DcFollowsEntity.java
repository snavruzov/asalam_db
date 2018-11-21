package com.dgtz.db.api.domain;


/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/21/13
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */

public class DcFollowsEntity {
    private long idFollow;
    private String username;
    private String avatar;
    private Long source;
    private Long dest;
    private Boolean reFollow;
    private String location;

    public Boolean getReFollow() {
        return reFollow;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setReFollow(Boolean reFollow) {
        this.reFollow = reFollow;
    }


    public long getIdFollow() {
        return idFollow;
    }

    public void setIdFollow(long idFollow) {
        this.idFollow = idFollow;
    }


    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public Long getDest() {
        return dest;
    }

    public void setDest(Long dest) {
        this.dest = dest;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "DcFollowsEntity{" +
                "idFollow=" + idFollow +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", source=" + source +
                ", dest=" + dest +
                '}';
    }
}
