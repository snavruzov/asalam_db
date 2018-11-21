package com.dgtz.db.api.builder;

import com.dgtz.db.api.beans.NotificationMessageContainer;
import com.dgtz.db.api.enums.EnumOperationSystem;

import java.util.Set;


/**
 * Created by sardor on 4/7/16.
 */
public interface INotificationFactory {

    /**
     * Implements a query to get user's device list, that is, mobile device unique ID value.
     *
     * @param idUser the ID of user
     * @param type the ID of notification type
     * @return the set of String,devices list
     */
    @Deprecated
    Set<NotificationMessageContainer> getUserDevice(long idUser, int type, EnumOperationSystem os);

    /**
     * Implements a query to get a user's follower mobile device ID to "push notification"
     *
     * @param idUser      the ID of user
     * @param idChannel   the ID of channel, if channel is private we get only the channel memberships device list
     * @param isPrivately define should device list generated for private push notification
     * @param type        the type of notification activity
     * @return the set of String of devices
     */
    Set<NotificationMessageContainer> getUserMultiDevice(long idUser, long idChannel, boolean isPrivately, int type, EnumOperationSystem os);

    /**
     * Implements a query to get a user's follower mobile device ID to "push LIVE notification"
     *
     * @param idUserFrom      the ID of user
     * @param idChannel   the ID of channel, if channel is private we get only the channel memberships device list
     * @param key   key for redis store
     * @param isPrivately define should device list generated for private push notification
     */
    Long setLiveNotificationUsers(long idUserFrom, long idChannel, long key, boolean isPrivately);
}
