package com.dgtz.db.api.domain;

import com.dgtz.db.api.factory.GsonInsta;
import com.dgtz.mcache.api.factory.Constants;

/**
 * Created by root on 1/20/14.
 */
public class Notification {

    private long idNote = 0;
    private long idUser = 0;
    private long idFrom = 0;
    private String idRoom = "";
    private String text = "";
    private String username ="";
    private Long idMedia = 0l;
    private Long idHoster = 0l;
    private Long idChannel = 0l;
    private int rotation = 0;
    private String url = "";
    private String chAvatar = "";
    private String contentType = "";
    private short duration = 0;
    private String dateadded = "";
    private int type;
    private int commentType;
    private int ratingType;
    private int colorType;
    private boolean valid;
    private String activation;

    private long liked;
    private long amount;

    private String ratio;
    private String email ="";

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getIdHoster() {
        return idHoster;
    }

    public void setIdHoster(Long idHoster) {
        this.idHoster = idHoster;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public int getCommentType() {
        return commentType;
    }

    public void setCommentType(int commentType) {
        this.commentType = commentType;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRatingType() {
        return ratingType;
    }

    public void setRatingType(int ratingType) {
        this.ratingType = ratingType;
    }

    public int getColorType() {
        return colorType;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public long getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(long idFrom) {
        this.idFrom = idFrom;
    }

    public String getUrl() {
        return url;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public void setUrl(String url, Long uId) {
        if (url != null && uId != null) {
            url = Constants.encryptAmazonURL(uId, idMedia, "jpg", "thumb", Constants.STATIC_URL);
                this.contentType = "video";
        }

        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrl(String url, Long uId, boolean feed) {
        if (url != null && uId != null && !feed) {
            url = Constants.encryptAmazonURL(uId, idMedia, "jpg", "thumb", Constants.STATIC_URL);
            this.contentType = "video";
        }

        this.url = url;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = (ratio == null ? "4:3" : ratio);
    }

    public long getLiked() {
        return liked;
    }

    public void setLiked(long liked) {
        this.liked = liked;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getChAvatar() {
        return chAvatar;
    }

    public void setChAvatar(String chAvatar) {
        this.chAvatar = chAvatar;
    }

    public String getContentType() {
        return contentType;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public Long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(Long idChannel) {
        this.idChannel = idChannel;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(Long idMedia) {
        this.idMedia = idMedia;
    }

    @Override
    public String toString() {
        return GsonInsta.getInstance().toJson(this);
    }
}
