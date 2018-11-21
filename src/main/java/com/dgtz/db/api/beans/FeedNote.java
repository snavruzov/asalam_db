package com.dgtz.db.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Sardor Navruzov on 8/31/15.
 * Copyrights Digitizen Co.
 */
public class FeedNote implements Serializable {
    private static final long serialVersionUID = 1L;

    private long idNote;
    private int type;
    private long idMedia = 0;
    private long idChannel = 0;
    private boolean valid;
    private long idFromUser;
    private long idToUser;
    private String title;

    public FeedNote() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getIdToUser() {
        return idToUser;
    }

    public void setIdToUser(long idToUser) {
        this.idToUser = idToUser;
    }

    public long getIdFromUser() {
        return idFromUser;
    }

    public void setIdFromUser(long idFromUser) {
        this.idFromUser = idFromUser;
    }

    public long getIdNote() {
        return idNote;
    }

    public void setIdNote(long idNote) {
        this.idNote = idNote;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(long idMedia) {
        this.idMedia = idMedia;
    }

    public long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(long idChannel) {
        this.idChannel = idChannel;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);

    }
}
