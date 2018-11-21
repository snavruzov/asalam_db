package com.dgtz.db.api.domain;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 10/22/14
 */
public class DcUserSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotifyMethod liked = NotifyMethod.getDefault();
    private NotifyMethod want_to_join = NotifyMethod.getDefault(true);
    private NotifyMethod new_media = NotifyMethod.getDefault();
    private NotifyMethod suggest_to_join = NotifyMethod.getDefault(true);
    private NotifyMethod live_started = NotifyMethod.getDefault();
    private NotifyMethod commented = NotifyMethod.getDefault();
    private NotifyMethod newsfeed = NotifyMethod.getDefault(true);
    private NotifyMethod video_status = NotifyMethod.getDefault();

    private NotifyMethod user_channel_joined = NotifyMethod.getDefault();
    private NotifyMethod you_channel_joined = NotifyMethod.getDefault();
    private NotifyMethod promo_push = NotifyMethod.getDefault(true);
    private NotifyMethod channel_upd = NotifyMethod.getDefault();
    private NotifyMethod schd_event = NotifyMethod.getDefault();

    private NotifyMethod flw_comment = NotifyMethod.getDefault();
    private NotifyMethod flw_liked = NotifyMethod.getDefault();
    private NotifyMethod flw_added = NotifyMethod.getDefault();

    private NotifyMethod sub_channel = NotifyMethod.getDefault();

    private String location = "0";

    public DcUserSettings() {
    }

    public NotifyMethod getSub_channel() {
        return sub_channel;
    }

    public void setSub_channel(NotifyMethod sub_channel) {
        this.sub_channel = sub_channel;
    }

    public NotifyMethod getChannel_upd() {
        return channel_upd;
    }

    public void setChannel_upd(NotifyMethod channel_upd) {
        this.channel_upd = channel_upd;
    }

    public NotifyMethod getSchd_event() {
        return schd_event;
    }

    public void setSchd_event(NotifyMethod schd_event) {
        this.schd_event = schd_event;
    }

    public NotifyMethod getPromo_push() {
        return promo_push;
    }

    public void setPromo_push(NotifyMethod promo_push) {
        this.promo_push = promo_push;
    }

    public NotifyMethod getUser_channel_joined() {
        return user_channel_joined;
    }

    public void setUser_channel_joined(NotifyMethod user_channel_joined) {
        this.user_channel_joined = user_channel_joined;
    }

    public NotifyMethod getYou_channel_joined() {
        return you_channel_joined;
    }

    public void setYou_channel_joined(NotifyMethod you_channel_joined) {
        this.you_channel_joined = you_channel_joined;
    }

    public NotifyMethod getLiked() {
        return liked;
    }

    public void setLiked(NotifyMethod liked) {
        this.liked = liked;
    }

    public NotifyMethod getWant_to_join() {
        return want_to_join;
    }

    public void setWant_to_join(NotifyMethod want_to_join) {
        this.want_to_join = want_to_join;
    }

    public NotifyMethod getNew_media() {
        return new_media;
    }

    public void setNew_media(NotifyMethod new_media) {
        this.new_media = new_media;
    }

    public NotifyMethod getSuggest_to_join() {
        return suggest_to_join;
    }

    public void setSuggest_to_join(NotifyMethod suggest_to_join) {
        this.suggest_to_join = suggest_to_join;
    }

    public NotifyMethod getLive_started() {
        return live_started;
    }

    public void setLive_started(NotifyMethod live_started) {
        this.live_started = live_started;
    }

    public NotifyMethod getCommented() {
        return commented;
    }

    public void setCommented(NotifyMethod commented) {
        this.commented = commented;
    }

    public NotifyMethod getNewsfeed() {
        return newsfeed;
    }

    public void setNewsfeed(NotifyMethod newsfeed) {
        this.newsfeed = newsfeed;
    }

    public NotifyMethod getVideo_status() {
        return video_status;
    }

    public void setVideo_status(NotifyMethod video_status) {
        this.video_status = video_status;
    }

    public NotifyMethod getFlw_comment() {
        return flw_comment;
    }

    public void setFlw_comment(NotifyMethod flw_comment) {
        this.flw_comment = flw_comment;
    }

    public NotifyMethod getFlw_liked() {
        return flw_liked;
    }

    public void setFlw_liked(NotifyMethod flw_liked) {
        this.flw_liked = flw_liked;
    }

    public NotifyMethod getFlw_added() {
        return flw_added;
    }

    public void setFlw_added(NotifyMethod flw_added) {
        this.flw_added = flw_added;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);

    }
}
