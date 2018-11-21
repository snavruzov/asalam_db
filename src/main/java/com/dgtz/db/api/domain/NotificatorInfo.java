package com.dgtz.db.api.domain;

import java.util.Set;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 3/15/14
 */
public class NotificatorInfo {

    private static final long serialVersionUID = 1L;

    private Set<Notificator> notificators;

    public NotificatorInfo() {
    }

    public Set<Notificator> getNotificators() {
        return notificators;
    }

    public void setNotificators(Set<Notificator> notificators) {
        this.notificators = notificators;
    }
}
