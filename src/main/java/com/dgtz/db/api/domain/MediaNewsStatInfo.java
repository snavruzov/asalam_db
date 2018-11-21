package com.dgtz.db.api.domain;

import com.brocast.riak.api.beans.DcMediaEntity;
import com.dgtz.db.api.factory.GsonInsta;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 2/26/15
 */
public class MediaNewsStatInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private long idMedia;
    private String title;
    private String contentType;
    private Integer duration;
    private String dateadded;
    private boolean isLive;
    private long idUser;
    private String username;
    private long amount;
    private String avatar;
    private String thumb_webp;
    private String thumb;
    private long liked;
    private String currentTime;
    private String ratio;
    private String location;
    private Boolean verified = false;
    private boolean reFollow;
    private String method;
    private Set<String> tags = Collections.emptySet();

    public MediaNewsStatInfo() {
    }


    public MediaNewsStatInfo(DcMediaEntity media) {

        String username = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "username");

        String thumb = Constants
                .encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "jpg", "thumb", Constants.STATIC_URL);
        String thumb_webp = Constants
                .encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "webp", "thumb", Constants.STATIC_URL);
        media.setThumb(thumb);
        media.setThumb_webp(thumb_webp);

        String ava = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "avatar");
        String verified = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "verified");

        String ratio = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + media.getIdMedia(), "ratio");
        String location = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.MEDIA_KEY + media.getIdMedia(), "location", "en");
        String amount = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.MEDIA_KEY + media.getIdMedia(), "vcount");
        String liked = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.MEDIA_KEY + media.getIdMedia(), "liked");

        this.idMedia = media.getIdMedia();
        this.title = media.getTitle();
        this.contentType = "video";
        this.duration = media.getDuration();
        this.dateadded = media.getDateadded();
        this.isLive = media.method.equals("live");
        this.idUser = media.getIdUser();
        this.username = username;
        this.amount = Long.valueOf(amount);
        this.avatar = Constants.STATIC_URL + media.idUser + "/image" + ava + ".jpg";
        this.thumb = thumb;
        this.thumb_webp = thumb_webp;
        this.liked = Long.valueOf(liked);
        this.currentTime = RMemoryAPI.getInstance().currentTimeMillis()+"";
        this.ratio = ratio;
        this.location = location;
        this.verified = verified == null ? false : Boolean.valueOf(verified);
        this.method = media.method;
        this.tags = media.tags;
    }

    public boolean isReFollow() {
        return reFollow;
    }

    public void setReFollow(boolean reFollow) {
        this.reFollow = reFollow;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setIsLive(boolean isLive) {
        this.isLive = isLive;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getThumb_webp() {
        return thumb_webp;
    }

    public void setThumb_webp(String thumb_webp) {
        this.thumb_webp = thumb_webp;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public long getLiked() {
        return liked;
    }

    public void setLiked(long liked) {
        this.liked = liked;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
