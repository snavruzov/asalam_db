package com.dgtz.db.api.domain;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/15/13
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediaInfo {
    private Long duration;
    private Long size;
    private Long abitrate;
    private Long vbitrate;
    private Integer width = 0;
    private Integer height = 0;
    private String type;
    private Integer channels;
    private int diff;

    public MediaInfo() {
    }

    public MediaInfo(Long duration, Long size, Long abitrate, Long vbitrate, Integer width, Integer height, String type, Integer channels, int diff) {
        this.duration = duration;
        this.size = size;
        this.abitrate = abitrate;
        this.vbitrate = vbitrate;
        this.width = width;
        this.height = height;
        this.type = type;
        this.channels = channels;
        this.diff = diff;
    }

    public Integer getChannels() {
        return channels;
    }

    public void setChannels(Integer channels) {
        this.channels = channels;
    }

    public void setAbitrate(Long abitrate) {
        this.abitrate = abitrate;
    }

    public Long getAbitrate() {
        return abitrate;
    }

    public Long getVbitrate() {
        return vbitrate;
    }

    public void setVbitrate(Long vbitrate) {
        this.vbitrate = vbitrate;
    }

    public int getDiff() {
        return width - height;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "MediaInfo{" +
                "duration=" + duration +
                ", size=" + size +
                ", abitrate=" + abitrate +
                ", width=" + width +
                ", height=" + height +
                ", type='" + type + '\'' +
                ", diff='" + diff + '\'' +
                '}';
    }
}
