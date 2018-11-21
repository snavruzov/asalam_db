package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.DcCommentsEntity;
import com.brocast.riak.api.beans.DcMediaEntity;
import com.brocast.riak.api.beans.DcNotificationTypes;
import com.brocast.riak.api.beans.DcUsersEntity;
import com.dgtz.db.api.beans.DcDebateEntity;
import com.dgtz.db.api.beans.EmailRecipients;
import com.dgtz.db.api.beans.MediaMappingStatInfo;
import com.dgtz.db.api.domain.*;
import com.dgtz.db.api.enums.EnumAggregations;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/22/13
 * Time: 11:51 AM
 * <p/>
 * Implementing this interface allows to build DB queries.
 */
public interface IQueryFactory {

    /**
     * Implements a query to get sorted <code>sortType</code> data collection
     * by content category ID <code>idCategory</code>
     *
     * @param idCategory ID number of the content category.
     * @param sortType   method of data sorting.
     * @param off        collection offset
     * @param limit      collection limit
     * @return list of <code>MediaNewsStatInfo</code> generated content
     */
    List<MediaNewsStatInfo> castMediaByIdCategory(int idCategory, int sortType, long off, long limit, String lang);

    /**
     * Implements a query to get Live stream viewers number.
     *
     * @param idLive ID number of the Live event
     * @return a number of viewers
     */
    long getLiveViewCount(long idLive);

    /**
     * Implements a query to get a media by its ID.
     *
     * @param idMedia unique ID of the media content.
     * @return the media data in <code>DcMediaEntity</code>
     */
    DcMediaEntity getMediaById(long idMedia);


    /**
     * Implements a query to get the list of active Live stream contents..
     *
     * @param off   the offset value of the collection
     * @param limit the limit of the collection
     * @return the list of media media contents in <code>DcMediaEntity</code>
     */
    List<MediaNewsStatInfo> castMediaByLive(long off, long limit);

    /**
     * Implements a query to get the content list by popularity type <code>EnumAggregations</code>
     *
     * @param enumAgr the enum of sorting types
     * @param geoIds  the geo location where the collection should belong
     * @param off     the offset of collection
     * @param limit   the limit of collection
     * @return the list of <code>DcMediaEntity</code>
     * @see EnumAggregations
     */
    List<MediaNewsStatInfo> castMediaByStats(EnumAggregations enumAgr, String geoIds, Set<String> langs, long off, long limit);

    /**
     * Implements a query to get the content list with geo-coordination values
     * @return the list of <code>DcMediaEntity</code>
     * @see EnumAggregations
     */
    List<MediaMappingStatInfo> castMediaByLatLng();
    List<DcMediaEntity> castMediaByFeatured();

    /**
     * Implements a query to get a user information by Google ID value, if user is registered via Google API.
     *
     * @param email the email of user
     * @param idGoo Google ID number
     * @return the object of DcUsersEntity data
     */
    DcUsersEntity getUserInfoByGooID(String email, String idGoo);

    /**
     * Implements a query to get a Live event info by its ID number.
     *
     * @param idLive the ID of the Live event
     * @return the object DcLiveMediaEntity serialized to keep Live event info
     * @see DcLiveMediaEntity
     */
    DcLiveMediaEntity castLiveById(long idLive);

    /**
     * Implements a query to get a  modified media info by its ID value.
     *
     * @param idMedia the ID of media
     * @return the object of serialized DcMediaEntity
     * @see DcMediaEntity
     */
    DcMediaEntity castMediaById(long idMedia);

    /**
     * Implements a query to get an basic instance of media info.
     *
     * @param idMedia the ID of media
     * @return the object of serialized DcMediaEntity
     * @see DcMediaEntity
     */
    @Deprecated
    DcMediaEntity castSimpleMediaById(long idMedia);

    /**
     * * Implements a query to get rating hearts by r-time.
     *
     * @param idLive the ID of Live.
     * @return the object of Map
     */
    Map<Long, List<String>> castRatingsByLive(String idLive);

    /**
     * * Implements a query to get comments by r-time.
     *
     * @param idMedia the ID of Media.
     * @return the object of Map
     */
    Map<Long, List<DcCommentsEntity>> castCommentsByIdLive(long idMedia,int duration, long off, long limit, boolean reverse, boolean sort);

    /**
     * Implements a query to get a user's account info by ID.
     *
     * @param idUser the ID of user
     * @return the object of user's profile info DcUsersEntity
     * @DcUsersEntity
     */
    DcUsersEntity getUserProfileInfoById(long idUser);

    /**
     * Implements a query to get a users profile info by email address.
     *
     * @param email the email address of user
     * @return the object of user's profile DcUsersEntity
     * @see DcUsersEntity
     */
    DcUsersEntity getUserInfoByEmail(String email);

    /**
     * Implements a query to get a users profile info by id.
     *
     * @param idUser the ID of user
     * @return the object of user's profile DcUsersEntity
     * @see DcUsersEntity
     */
    DcUsersEntity getUserInfoByID(Long idUser);

    /**
     * Implements a query to get user's email address.
     *
     * @param idUser the ID of user
     * @return the set of array String with email and idUser
     */
    Set<EmailRecipients> getUserEmail(long idUser, int type);

    /**
     * Implements a query to get a user information by HASH value.
     * All registered and one "Anonymous" users have unique public HASH IDs, to securely get private data.
     *
     * @param hash the hash value
     * @return the user info object of DcUsersEntity
     * @see DcUsersEntity
     */
    //TODO: HASH values should be refreshable with some expiration period to prevent
    //TODO: HASH "robbing" by third persons. Currently HASHs are constant.
    DcUsersEntity getUserInfoByHash(String hash);

    /**
     * Implements a query to get user's info by Facebook token.
     *
     * @param hash the Facebook ID value
     * @return the user's object DcUsersEntity
     * @see DcUsersEntity
     */
    DcUsersEntity getUserInfoByFB(String hash);

    /**
     * Implements a query to get country's language by its code.
     *
     * @param code country short code
     * @return language code
     */
    String getCountryLanguages(String code);

    /**
     * Implements a query to get user's info by Facebook ID and email.
     *
     * @param email the email address of user
     * @param idFb  the ID value of Facebook
     * @return the user info object DcUsersEntity
     * @see DcUsersEntity
     */
    DcUsersEntity getUserInfoByFbID(String email, String idFb);

    /**
     * Implements a query to get user's info by Twitter ID .
     *
     * @param idVk  the ID value of Twitter
     * @return the user info object DcUsersEntity
     * @see DcUsersEntity
     */
    DcUsersEntity getUserInfoByTwitterID(String idVk);

    /**
     * Implements a query to get user's info by Vkontakte ID .
     *
     * @param idVk  the ID value of Vkontakte
     * @return the user info object DcUsersEntity
     * @see DcUsersEntity
     */
    DcUsersEntity getUserInfoByVKID(String idVk);

    /**
     * Implements a query to get user's notification setting
     *
     * @param idUser the ID value of user
     * @return the user notification settings' DcUserSettings
     * @see DcUserSettings
     */
    DcNotificationTypes getUserNotificationSettings(long idUser);

    /**
     * Implements a query to get a media content commentaries by media ID value.
     *
     * @param idMedia the id media
     * @param off     the offset of collection
     * @param limit   the limit of collection
     * @return the list of DcCommentsEntity
     * @see DcCommentsEntity
     */
    List<DcCommentsEntity> getCommentsByIdMedia(long idMedia, long off, long limit);

    /**
     * Implements a query to get media collection located in Archive, Archive contains privatly removed media elements
     *
     * @param idUser the ID user
     * @param offset the offset of collection
     * @param limit  the limit
     * @return the list of DcMediaEntity
     */
    List<MediaNewsStatInfo> castMediaInArchive(long idUser, long offset, long limit, String lang);
    List<DcDebateEntity> castMediaInDebate(long idMedia);

    /**
     * Implements a query to get the full list of all Countries name in the world
     *
     * @return the list of DcLocationsEntity
     * @see DcLocationsEntity
     */
    List<DcLocationsEntity> extractAllCountries();

    /**
     * Implements a query to generate unique ID value for a new Live event.
     *
     * @return the ID value
     */
    long extractUniqueIdForLive();

    /**
     * Implements a query to generate unique ID value for a new user..
     *
     * @return the ID value
     */
    long extractUniqueIdForUser();

    /**
     * Implements a query to generate unique ID value for a new media content.
     *
     * @return the ID value
     */
    long extractUniqueIdForMedia();

    /**
     * Implements a query to get a video rating(Likes, Dislike) rated by a user.
     * 0 - not yet rated, 1- user liked it, -1- disliked, .
     *
     * @param idUser  the ID user
     * @param idMedia the ID media
     * @return the short value of rating type
     */
    short extractVideoRateByUserAVideo(long idUser, long idMedia);

    /**
     * Implements a query to get report type about flagged video.
     *
     * @param idUser  the ID user
     * @param idMedia the id media
     * @return the Short value of rating types
     */
    short extractVideoReportByUserAct(long idUser, long idMedia);

    /**
     * Implements a query to identify if the viewer is already followed the author of video.
     *
     * @param source the source ID the user who is should follow ht author
     * @param dest   the destination ID, the author who is should be followed
     * @return the long value 1- followed, 0-not yet followed
     */
    long extractVideoIsFollowed(long source, long dest);

    /**
     * Implements a method to get user's media contents
     *
     * @param idUser the ID user
     * @param offset the offset of collection
     * @param limit  the limit of collection
     * @param isMine defines whether the user is going to open own content list or not
     * @return the list content DcMediaEntity
     * @see DcMediaEntity
     */
    List<MediaPublicInfo> extractUsersContentByIdUser(long idUser, int offset, int limit, boolean isMine);
    List<MediaPublicInfo> extractUsersEventContentByIdUser(long idUser, int offset, int limit, boolean isMine);
    List<MediaPublicInfo> extractUsersContentByTOP(long idUser, int offset, int limit);

    /**
     * Implement a method to check if KEY value is exist.
     *
     * @param key the key value
     * @return the long result of key
     */
    @Deprecated
    long validateKeyPreUpdate(String key);


    List<UserShortInfo> extractPublicIntersectUserFollowers(long idUser, long off, long limit, String lang);

    List<UserShortInfo> extractPublicViewersList(long idUser, long off, long limit);

    List<UserShortInfo> extractPublicUserFollowers(long idUser, long idSource, long off, long limit, String lang);


    List<UserShortInfo> searchMyUserFollowsByName(String name, Long idUser, int off, int limit);
    List<UserShortInfo> searchUserFollowsByName(String name, Long source, Long dest, String direct, int off, int limit);
    List<UserShortInfo> searchMyUserInterFollowsByName(String name, Long idUser, int off, int limit);
    List<UserShortInfo> searchMyUserFollowersByName(String name, Long idUser, int off, int limit);
    List<UserShortInfo> searchAllUsersByName(String name, Long idUser, int off, int limit);


    List<UserShortInfo> extractPublicUserFollowing(long idUser, long idSource, long off, long limit, String lang);

    /**
     * Implements a method to get a media transcodding state.
     * <UL>
     * <LI>COMPLETED</LI>
     * <LI>INPROCESS</LI>
     * <LI>BROKEN</LI>
     * <LI>TOOLONG</LI>
     * </UL>
     *
     * @param idMedia the ID of media
     * @return the string value if status
     * @see com.dgtz.db.api.enums.EnumCompressingState
     */
    String checkMediaCompressStatus(long idMedia);

    /**
     * Implements a method to get a countries list by code.
     *
     * @param code the code
     * @return the string
     */
    String extractCountriesByCode(String code);

    /**
     * Implements a method to get user's followings/followers number.
     *
     * @param idUser the ID of user
     * @param type   the type of the list followers or followings
     * @return the long value of amounts
     */
    Long getUserFollowsCount(long idUser, int type);

    /**
     * Implements a method to get uploaded media contents number of user.
     *
     * @param idUser the ID user
     * @return the Long value of amount of uploaded contents
     */
    Long getUserVideosCount(long idUser, boolean mine);

    /**
     * Implements a method to get channels number of user.
     *
     * @param idUser the ID user
     * @param mine whether user explored publicly or authorized
     * @return the Long value of amount of channels
     */
    Long getUserChannelsCount(long idUser, boolean mine);

    /**
     * Test memcached stat.
     *
     * @param idUser the id user
     * @return the string
     * @deprecated the memcached is no longer used
     */
    String testMemcached(long idUser);


}
