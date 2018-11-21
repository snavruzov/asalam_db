package com.dgtz.db.api.domain;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 2/27/14
 */
public class Notificator implements Serializable {

    private static final long serialVersionUID = 1L;

    private String device;
    private Long toId;
    private Long fromId;
    private Long idChannel;
    private int type;
    private Long idMedia;
    private String username;
    private String recipient;
    private String title;
    private String descr;
    private String email;
    private boolean sendPush;
    private boolean valid;
    private boolean sendEmail;
    private String ratio = "0:0";
    private String thumb;
    private String activation;
    private String link;
    private String phone;
    private String country;

    public Notificator() {
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = (ratio == null ? "4:3" : ratio);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isSendPush() {
        return sendPush;
    }

    public void setSendPush(boolean sendPush) {
        this.sendPush = sendPush;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(Long idChannel) {
        this.idChannel = idChannel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(Long idMedia) {
        this.idMedia = idMedia;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);

    }
}
