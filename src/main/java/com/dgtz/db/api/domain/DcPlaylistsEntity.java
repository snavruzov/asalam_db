package com.dgtz.db.api.domain;


/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/21/13
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */

public class DcPlaylistsEntity {
    private long idPlaylist;


    public long getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(long idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcPlaylistsEntity that = (DcPlaylistsEntity) o;

        if (idPlaylist != that.idPlaylist) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idPlaylist ^ (idPlaylist >>> 32));
    }
}
