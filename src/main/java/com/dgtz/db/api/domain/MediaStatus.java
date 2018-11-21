package com.dgtz.db.api.domain;

import com.dgtz.db.api.factory.GsonInsta;
import com.dgtz.db.api.features.ConverterUtils;

/**
 * Created by sardor on 11/30/15.
 */
public class MediaStatus {
    private static final long serialVersionUID = 1L;

    private int progress;
    private String status = "NOT FOUND";

    public MediaStatus(int progress) {
        this.setProgress(progress);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        String status = ConverterUtils.parseStatusToText(progress);
        this.status = status;
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return GsonInsta.getInstance().toJson(this);
    }
}
