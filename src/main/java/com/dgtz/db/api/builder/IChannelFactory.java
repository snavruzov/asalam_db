package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.DcChannelsEntity;
import com.dgtz.db.api.beans.EmailRecipients;
import com.dgtz.db.api.domain.*;
import com.dgtz.db.api.enums.EnumAggregations;
import com.dgtz.db.api.enums.EnumOperationSystem;
import com.dgtz.db.api.enums.EnumSQLErrors;
import org.json.JSONObject;

import java.util.Deque;
import java.util.List;
import java.util.Set;

/**
 * Created by sardor on 12/1/15.
 */
public interface IChannelFactory {

    /**
     * Implements a query to get users' email and ID values to notify within channel community
     *
     * @param idOwner   the ID of channels owner
     * @param idChannel the ID of channel
     * @return Set of array string with two indexes, <code>[0]=email, [1]=id_user</code>
     */
    Set<EmailRecipients> getUserMultiChannelEmails(long idOwner, long idChannel);

    /**
     * Implements a query to get mobile device ID list to notify within a channel community.
     *
     * @param idOwner   the ID of channel owner
     * @param idChannel the ID of channel
     * @return the SET of String with devices list
     */
    Set<String> getUserMultiChannelDevice(long idOwner, long idChannel, EnumOperationSystem os);

    /**
     * Implements a query to check if user is already joined to the channel or not..
     *
     * @param idUser    the ID user
     * @param idChannel the ID channel
     * @return the short value 1- channel's member, 0- not joined
     */
    boolean ifUserJoinedToChannel(long idUser, long idChannel);

    /**
     * Implements a query to get a full channel's information by ID of channel
     *
     * @param idChannel the id channel
     * @return the object of channels params DcChannelsEntity
     * @see DcChannelsEntity
     */
    PublicChannelsEntity extractChannelInfoByIdChannel(long idChannel, long idUser);

    /**
     * Implements a query to get(search) a channel by key name.
     *
     * @param chName the channel name
     * @param off    the offset of collection
     * @param limit  the limit of collection
     * @return the list of channels DcChannelsEntity
     * @see DcChannelsEntity
     */
    //TODO need to realize a fast text-search mechanism
    List<DcChannelsEntity> castMediaByChannelName(String chName, int off, int limit);

    /**
     * Implements a query to get the all authors channels list .
     *
     * @param idOUser the ID of the channels' owner
     * @return the list of channels <code>DcChannelsEntity</code>
     * @see DcChannelsEntity
     */
    List<DcChannelsEntity> extractChannelsInfoByOwner(long idOUser);

    /**
     * Implements a query to get channels list by sorting type <code>EnumAggregations</code>.
     *
     * @param enumAgr the sorting type
     * @param off     the offset of collection
     * @param limit   the limit of collection
     * @return the list of channels <code>DcChannelsEntity</code>
     * @see EnumAggregations
     * @see DcChannelsEntity
     */
    List<PublicChannelsEntity> extractChannelsInfo(EnumAggregations enumAgr, Long idUser, long off, long limit);

    /**
     * Implements a query to get the channels list of users, it can the owners channels or any channels where user is joined .
     *
     * @param idUser the ID user
     * @param off    the offset of collection
     * @param limit  the limit of collection
     * @return the list of channels <code>DcChannelsEntity</code>
     * @see DcChannelsEntity
     */
    List<PublicChannelsEntity> extractChannelsInfoByIdUser(long idUser, long off, long limit);
    List<PublicChannelsEntity> extractChannelsInfoByFollowing(long idUser, long off, long limit);

    List<PublicChannelsEntity> extractChannelsInfoBySearch(String name, long idUser, long off, long limit);
    List<PublicChannelsEntity> extractChannelsInfoByFollowsSearch(String name, long idUser, long off, long limit);

    /**
     * Implements a method to get content list belongs to the channels.
     *
     * @param idChannel the ID channel
     * @param off       the offset of collection
     * @param limit     the limit of collection
     * @return the list contents DcMediaEntity
     */
    List<MediaNewsStatInfo> extractChannelsContent(long idChannel, long off, long limit, String lang);

    /**
     * Implements a method to get all members of the channel.
     *
     * @param idChannel the ID channel
     * @param offset    the offset of collection
     * @param limit     the limit of collection
     * @return the list of users
     */
    List<UserShortInfo> extractChannelsUsers(long idChannel, long offset, long limit, String lang);


    /**
     * Implements a method to get all members of the channel.
     *
     * @param idChannel the ID channel
     * @param idUser    the ID of user who is opened the channel
     * @param offset    the offset of collection
     * @param limit     the limit of collection
     * @return the list of users
     */
    Deque<UserShortInfo> extractChannelsUsers(long idChannel, long idUser, long offset, long limit, String lang);

    /**
     * Implements a method to get channels invitation notification.
     *
     * @param idUser the ID user who got a notification
     * @param off    the offset of collection
     * @param limit  the limit of collection
     * @return the channel notification object
     * @see NotificationInfo
     */
    NotificationInfo getChannelNotification(long idUser, long off, long limit);



    /*Cahnnel Create/Savings*/

    /**
     * Create channel.
     *
     * @param entity the entity
     * @return the long
     */
    long createChannel(DcChannelsEntity entity);

    /**
     * Update channel info.
     *
     * @return the enum sQL errors
     */
    EnumSQLErrors updateChannelInfo(DcChannelsEntity entity);

    /**
     * Join to channel.
     *
     * @param id_user   the id _ user
     * @param idChannel the id channel
     * @param byOwner   the by owner
     * @param accept    the accept
     * @param refuse    the refuse
     * @return the jSON object
     */
    JSONObject joinToChannel(long id_user, long idChannel, boolean byOwner, boolean accept, boolean refuse);

    /**
     * Leave the channel.
     *
     * @param id_user   the id _ user
     * @param idChannel the id channel
     * @return the boolean
     */
    boolean leaveTheChannel(long id_user, long idChannel);//still thinking

    /**
     * Accept joiner by owner.
     *
     * @param id_user        the id _ user
     * @param id_act_channel the id _ act _ channel
     * @return the enum sQL errors
     */
    EnumSQLErrors acceptJoinerByOwner(long id_user, long id_act_channel); //deprecated

    /**
     * Remove channel by id.
     *
     * @param id_ouser  the id _ ouser
     * @param idChannel the id channel
     * @return the enum sQL errors
     */
    EnumSQLErrors removeChannelById(long id_ouser, long idChannel);

    /**
     * Remove media from channel.
     *
     * @param idMedia   the id media
     * @param idChannel the id channel
     * @return the enum sQL errors
     */
    EnumSQLErrors removeMediaFromChannel(long idMedia, long idChannel);

    /**
     * Subscribe to channel.
     *
     * @param idUser   the id user
     * @param idChannel the id channel
     * @return the enum sQL errors
     */
    EnumSQLErrors subscribeToChannel(long idUser, long idChannel);

    /**
     * UnSubscribe to channel.
     *
     * @param idUser   the id user
     * @param idChannel the id channel
     * @return the enum sQL errors
     */
    EnumSQLErrors unsubFromChannel(long idUser, long idChannel);

    /**
     * Subscriberd List.
     *
     * @param idChannel the id channel
     * @return the list users info
     */
    List<UserShortInfo> extractSubsOfChannel(long idChannel, long idUser, long offset, long limit, String lang);

}
