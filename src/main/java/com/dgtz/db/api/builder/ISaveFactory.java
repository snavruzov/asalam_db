package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.*;
import com.dgtz.db.api.domain.*;
import com.dgtz.db.api.enums.EnumAggregations;
import com.dgtz.db.api.enums.EnumSQLErrors;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/24/13
 * Time: 5:18 PM
 * <p/>
 * Implementing this interface allows to build DB updates
 */
public interface ISaveFactory {

    /**
     * Implements a method to insert Media content data  .
     *
     * @param entity the entity for Media content parameters.
     * @return the enum value of SQL errors.
     * @see DcMediaEntity
     * @see EnumSQLErrors
     */
    EnumSQLErrors saveMediaInfo(DcMediaEntity entity, String lang);

    /**
     * Inserts media data to memcached.
     *
     * @param entity the entity
     * @return the boolean
     * @deprecated no memcached is used anymore
     */
    boolean saveMediaInfoIntoMem(DcMediaEntity entity);

    /**
     * Saves live event initial parameters.
     *
     * @param entity the entity
     */
    EnumSQLErrors saveContentInfo(DcMediaEntity entity, String lang);
    EnumSQLErrors saveTextContentInfo(DcMediaEntity entity, String lang);


    /**
     * Saves users' social link vars.
     *
     * @param idSocial the id of social network
     */
    EnumSQLErrors linkSocialNetworkAcc(DcUsersEntity entity, String idSocial, String stp);

    /**
     * Updates location information.
     *
     * @param location the location
     * @param idMedia  the ID media
     */
    void updateLocation(String location, Long idMedia);

    /**
     * Update live event information.
     *
     * @param idLive   the ID of the event
     * @param progress the progress of the live event
     *                 <UL>
     *                 <LI>1- Live event has stopped and in encoding progress</LI>
     *                 <LI>2- Video is not processed correctly, video is broken</LI>
     *                 <LI>3- Video has been removed</LI>
     *                 <LI>0- Video is converted successfully and ready to watch or Live is still broadcasting</LI>
     *                 </UL>
     * @return the enum of SQL error values
     * @see EnumSQLErrors
     */
    EnumSQLErrors updateLiveInfo(long idLive, int progress);

    /**
     * Publishes Live event if all is OK.
     *
     * @param idLIve   the ID of Live
     * @param progress the progress status of the Live event
     *                 <UL>
     *                 <LI>1- Live event has stopped and in encoding progress</LI>
     *                 <LI>2- Video is not processed correctly, video is broken</LI>
     *                 <LI>3- Video has been removed</LI>
     *                 <LI>0- Video is converted successfully and ready to watch or Live is still broadcasting</LI>
     *                 </UL>
     * @return the enum of SQL error values
     * @see EnumSQLErrors
     */
    EnumSQLErrors publishLive(long idLIve, int progress);

    /**
     * Updating Live event data during Live broadcasting mode.
     *
     * @param entity the entity of Media content properties
     * @return the enum of SQL errors
     * @see DcMediaEntity
     * @see EnumSQLErrors
     */
    EnumSQLErrors updateRealTimeLiveInfo(DcMediaEntity entity);

    /**
     * Saves just a social registered user's data.
     *
     * @param entity the entity of User's params
     * @param clang user prefered languages to sort him content
     * @param type   the type of social network auth <code>Facebook/Google</code>
     * @return the value of idUser
     */
    long saveFreshSocialPrinciples(DcUsersEntity entity, Integer type, String lang, String clang);

    /**
     * Saves full location properties, city, country, streets and etc..
     *
     * @param local   the full location data
     * @param idMedia the ID of media
     */
    void saveFormattedLocation(String local, Long idMedia);

    /**
     * Updates the notification parameters.
     *
     * @param json   the entity of user's notification list
     * @param idUser the ID of user
     * @return the enum od SQL errors
     * @see DcUserSettings
     * @see EnumSQLErrors
     */
    EnumSQLErrors updateNotificationParams(DcNotificationTypes json, long idUser);

    /**
     * Activate fresh body.
     *
     * @param entity the user entity object
     * @return the enum sQL errors
     */
    EnumSQLErrors activateFreshBody(DcUsersEntity entity);

    /**
     * Update media info.
     *
     * @param entity the entity
     * @return the enum sQL errors
     */
    EnumSQLErrors updateContentInfo(DcMediaEntity entity);

    /**
     * Logout user.
     *
     * @param idUser the id user
     * @param device the id device(token) of user' mobile
     * @return the enum sQL errors
     */
    EnumSQLErrors logoutUser(Long idUser, String device);

    /**
     * Update media tech info.
     *
     * @param entity the entity
     * @return the enum sQL errors
     */
    EnumSQLErrors updateMediaTechInfo(DcMediaEntity entity);

    /**
     * Update media status.
     *
     * @param idMedia  the id media
     * @param progress the progress
     * @return the enum sQL errors
     */
    EnumSQLErrors updateMediaStatus(Long idMedia, Short progress);

    /**
     * Update user ava.
     *
     * @param entity the entity
     * @return the enum sQL errors
     */
    EnumSQLErrors updateUserAva(DcUsersEntity entity);

    /**
     * Update user info.
     *
     * @param entity the entity
     * @return the enum sQL errors
     */
    EnumSQLErrors updateUserInfo(DcUsersEntity entity, String lang);

    /**
     * Change account profile.
     *
     * @param idUser the id user
     * @param newP   the new p
     * @param hash   the hash
     * @return the string
     */
    String changeAccountProfile(long idUser, String newP, String hash);

    /**
     * Update user pass.
     *
     * @param entity the entity
     * @return the enum sQL errors
     */
    EnumSQLErrors updateUserPass(DcUsersEntity entity);

    /**
     * Update user device.
     *
     * @param idUser the id user
     * @param devType the type of device: 2- iOS, 1- Android
     * @return the enum sQL errors
     */
    void updateUserDevice(String devid, long idUser, String devType);

    /**
     * Remove device.
     *
     * @param devId the dev id
     * @return the enum sQL errors
     */
    EnumSQLErrors removeDevice(String devId);

    /**
     * Add comments.
     *
     * @param entity the entity
     * @return the long
     */
    long addComments(DcCommentsEntity entity, long timeFrac);

    /**
     * Add counter media stat.
     *
     * @param idMedia the id media
     * @param type    the type
     * @return the enum sQL errors
     */
    void addCounterMediaStat(long idMedia, EnumAggregations type);

    /**
     * Report media activity.
     *
     * @param idUser    the id user
     * @param idMedia   the id media
     * @param idRepType the id rep type
     * @return the enum sQL errors
     */
    EnumSQLErrors reportMediaActivity(long idUser, long idMedia, short idRepType);

    /**
     * Add follower.
     *
     * @param source the source
     * @param dest   the dest
     * @return the enum sQL errors
     */
    EnumSQLErrors addFollower(long source, long dest);

    /**
     * Video sharing count.
     *
     * @param idUser  the id user
     * @param idMedia the id media
     * @return the enum sQL errors
     */
    EnumSQLErrors videoSharingCount(long idUser, long idMedia);

    /**
     * Un follow.
     *
     * @param source the source
     * @param dest   the dest
     * @return the enum sQL errors
     */
    EnumSQLErrors unFollow(long source, long dest);

    /**
     * Remove media by owner.
     *
     * @param id_ouser the id _ ouser
     * @param idMedia  the id media
     * @return the enum sQL errors
     */
    EnumSQLErrors removeMediaByOwner(long id_ouser, long idMedia);

    /**
     * Save feedback.
     *
     * @param idUser the id user
     * @param text   the text
     * @return the enum sQL errors
     */
    EnumSQLErrors saveFeedback(long idUser, String text);

    /**
     * Save bug track.
     *
     * @param title the title
     * @param text  the text
     * @return the enum sQL errors
     */
    EnumSQLErrors saveBugTrack(String title, String text);

    /**
     * Sets personal localization.
     *
     * @param ids    the ids
     * @param idUser the id user
     * @return the personal localization
     */
    EnumSQLErrors setPersonalLocalization(String ids, long idUser);

    /**
     * Save info in memory.
     *
     * @param key   the key
     * @param value the value
     * @param exp   the exp
     */
    void saveInfoInMemory(String key, String value, String exp);


    /**
     * Update real time notification queue.
     *
     * @param id any unique ID, may be idMedia or idChannel
     * @param idUser id of the user
     * @param key Notification unique KEY
     * @return the enum sQL errors
     * @throws SQLException the sQL exception
     */
    default void pushToOrder(long id, long idUser, String key) {
        try {
            RMemoryAPI.getInstance().pushElemToMemory(Constants.NOTIFICATION_KEY + id + ":" + idUser, 3, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Upd media device param.
     *
     * @param idMedia the id media
     * @param dname   the dname
     * @return the enum sQL errors
     */
    EnumSQLErrors updMediaDeviceParam(long idMedia, String dname);

    /**
     * Implements an DB insert of logging users' activities.
     *
     * @param idMedia the id media
     * @param idUser  the id user
     * @param type    the type: --10 - watch live, 11 watch video, 20 - shoot live, 21 upload video
     * @param method  the method: --1 APP, 2 WEB
     * @param data    the data of app_version, device_name, IP
     * @return the enum sQL errors
     */
    EnumSQLErrors updMediaDeviceParam(long idMedia, long idUser, int type, int method, String data);

}
