package com.dgtz.db.api.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 1/20/14.
 */
public class NotificationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Notification> notifications;
    private int count;

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
