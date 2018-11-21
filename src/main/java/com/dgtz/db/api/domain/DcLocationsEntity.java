package com.dgtz.db.api.domain;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 3/21/14
 */
public class DcLocationsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id_location;
    private long id_country;
    private String ip;
    private String title;
    private String country;
    private String code;

    public DcLocationsEntity() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getId_location() {
        return id_location;
    }

    public void setId_location(Long id_location) {
        this.id_location = id_location;
    }


    public long getId_country() {
        return id_country;
    }

    public void setId_country(long id_country) {
        this.id_country = id_country;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
