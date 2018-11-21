package com.dgtz.db.api.builder;


import com.brocast.riak.api.beans.*;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakSaveFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.db.api.beans.DcDeviceType;
import com.dgtz.db.api.domain.*;
import com.dgtz.db.api.enums.EnumAggregations;
import com.dgtz.db.api.enums.EnumNotification;
import com.dgtz.db.api.enums.EnumSQLErrors;
import com.dgtz.db.api.factory.GsonInsta;
import com.dgtz.db.api.features.MediaTagTokenizer;
import com.dgtz.db.api.features.PushNotifier;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.GsonBuilder;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 6/19/14
 */
public abstract class RedisSaveTemplate {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RedisSaveTemplate.class);

    /**
     * Instantiates a new Redis save template.
     */
    protected RedisSaveTemplate() {
    }

    @Deprecated
    /**
     * Save media info.
     *
     * @param entity the entity
     * @return the enum sQL errors
     * @throws Exception the exception
     */
    protected EnumSQLErrors saveMediaInfo(DcMediaEntity entity, String lang) throws Exception {
        EnumSQLErrors errors = EnumSQLErrors.OK;

        log.debug("TAGS LIST: {} ", entity.getTags());


//        entity.setLive(false);
//        entity.setDateadded(String.valueOf(RMemoryAPI.getInstance().currentTimeMillis()));
//        entity.setLiked(0);
//        entity.setDisliked(0);
//        entity.setAmount(0);
//        entity.setCcount(0);
//        entity.setDuration(entity.getDuration());
//
//        entity.setContentType("video");

        String thumb = Constants.encryptAmazonURL(entity.getIdUser(), entity.getIdMedia(), "jpg", "thumb", Constants.STATIC_URL);
        entity.setThumb(thumb);
        String thumb_webp = Constants.encryptAmazonURL(entity.getIdUser(), entity.getIdMedia(), "webp", "thumb", Constants.STATIC_URL);
        entity.setThumb_webp(thumb_webp);

        try {
            String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.getIdUser(), "username");
            //entity.setUsername(username);

            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "id_media", entity.getIdMedia() + "");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "username", username);

            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "id_user", entity.getIdUser() + "");
//            RMemoryAPI.getInstance()
//                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "liked", entity.getLiked() + "");
//            RMemoryAPI.getInstance()
    //                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "rating", "0");
//            RMemoryAPI.getInstance()
//                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "disliked", entity.getDisliked() + "");
//            RMemoryAPI.getInstance()
//                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "vcount", entity.getAmount() + "");
//            RMemoryAPI.getInstance()
//                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "ccount", entity.getCcount() + "");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "lang", lang);
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "detail", entity.toString());

            //MediaTagTokenizer.storeTags(entity.getTags(), entity.getIdMedia(), entity.getIdCategory());

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }

        return errors;
    }


    /**
     * Update live info.
     *
     * @param idLive   the id live
     * @param progress the progress
     * @return the enum sQL errors
     * @throws SQLException the sQL exception
     */
    public EnumSQLErrors updateLiveInfo(long idLive, int progress) throws SQLException {

        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {

            DcMediaEntity listInfo = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + idLive, "detail", DcMediaEntity.class);

            if (listInfo != null) {
                listInfo.setProgress(progress);
                listInfo.setMethod("recorded");

                RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idLive, "detail", listInfo.toString());
                RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idLive, "method", "recorded");
            }
            delFormMediaLists(idLive);


        } catch (Exception e) {
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
            log.error("ERROR IN UPDATING - " + e.getMessage());

        }

        return errors;
    }

    /**
     * Publish live.
     *
     * @param idLive   the id live
     * @param progress the progress
     * @return the enum sQL errors
     * @throws SQLException the sQL exception
     */
    public EnumSQLErrors publishLive(long idLive, int progress) throws SQLException {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            if (idLive != 0) {
                DcMediaEntity media = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idLive, "detail", DcMediaEntity.class);

                if (media != null) {
                    media.setProgress(progress);
                    RMemoryAPI.getInstance()
                            .pushHashToMemory(Constants.MEDIA_KEY + idLive, "progress",
                                    new MediaStatus(progress).toString());

                    RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idLive, "detail", media.toString());
                }
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }

        return errors;
    }

    /**
     * Update real time live info.
     *
     * @param entity the entity
     * @return the enum sQL errors
     * @throws SQLException the sQL exception
     */
    public EnumSQLErrors updateRealTimeLiveInfo(DcMediaEntity entity) throws SQLException {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            DcMediaEntity listInfo = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "detail", DcMediaEntity.class);

            log.debug("EDIT LIVE DESCRIPTIONS {}", entity.toString());
            if (listInfo != null) {
                listInfo.setTitle(entity.getTitle());
                listInfo.setDescription(entity.getDescription());

                if (listInfo.getIdCategory() != entity.getIdCategory()) {
                    updateMediaCategory(listInfo.getIdCategory(), entity.getIdCategory(), entity.getIdMedia());
                }
                listInfo.setIdCategory(entity.getIdCategory());
                long oldChannel = listInfo.getIdChannel();
                listInfo.setIdChannel(entity.getIdChannel());
                listInfo.setTags(entity.getTags());
                //listInfo.setProps(entity.getProps());

                RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia()
                        , "detail", listInfo.toString());

               // MediaTagTokenizer.storeTags(entity.getTags(), entity.getIdMedia(), entity.getIdCategory());

                addVideoCountToChannel(entity.getIdChannel(), oldChannel, entity.getIdMedia());
//                changePrivacy(entity.getProps(), entity.getIdCategory(), entity.getIdMedia());
//                if (entity.getProps() == 0 && entity.isLive()) {
//                    RMemoryAPI.getInstance().pushSetElemToMemory(Constants.LIVES, entity.getIdMedia() + "");
//                }

            }

        } catch (Exception e) {
            errors = EnumSQLErrors.UNKNOWN_ERROR;
            log.error("ERROR IN DB API ", e);
        }

        return errors;
    }


    /**
     * Save live info.
     *
     * @param entity the entity
     * @throws SQLException the sQL exception
     */
    public void saveContentRedisInfo(DcMediaEntity entity, boolean update) throws Exception {

        if(!update) {
            if(entity.method.equals("live")) {
                RMemoryAPI.getInstance().pushHashToMemory(Constants.LIVE_KEY + entity.idMedia,
                        "id_media", entity.idMedia + "");
                RMemoryAPI.getInstance().pushHashToMemory(Constants.LIVE_KEY + entity.idMedia,
                        "id_user", entity.idUser + "");
                RMemoryAPI.getInstance().pushHashToMemory(Constants.LIVE_KEY + entity.idMedia,
                        "rtmp_url", entity.liveProps.rtmp_url);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.LIVE_KEY + entity.idMedia,
                        "http_url", entity.liveProps.hls_url);
            }

            if(entity.idChannel!=0){
                RMemoryAPI.getInstance()
                        .pushSetElemToMemory(Constants.CHANNEL_KEY + "videos:" + entity.idChannel, entity.idMedia + "");
                long mcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + entity.idChannel);
                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.CHANNEL_KEY + entity.idChannel, "mcount", mcount + "");
            }

            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "id_media", entity.idMedia + "");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "liked", "0");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "id_user", entity.idUser + "");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "vcount", "0");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "lvcount", "0");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "rating", "0");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "ccount", "0");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "lang", entity.lang);
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "location", entity.location);

        }

        RMemoryAPI.getInstance()
                .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "last_update", System.currentTimeMillis()+"");

        RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "method", entity.method);

        String thumb = Constants.encryptAmazonURL(entity.idUser, entity.idMedia, "jpg", "thumb", Constants.STATIC_URL);
        String thumb_webp = Constants.encryptAmazonURL(entity.idUser, entity.idMedia, "webp", "thumb", Constants.STATIC_URL);
        entity.thumb = thumb;
        entity.thumb_webp = thumb_webp;

        if (entity.liveProps.debate && entity.method.equals("live")) {
            RMemoryAPI.getInstance().pushHashToMemory(Constants.LIVE_KEY + entity.idMedia,
                    "position", entity.liveProps.position);

            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia,
                    "position", entity.liveProps.position);

            RMemoryAPI.getInstance().pushHashToMemory(Constants.LIVE_KEY + entity.idMedia, "detail", entity.toString());
        }

        RMemoryAPI.getInstance()
                .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "title", entity.title);
        RMemoryAPI.getInstance()
                .pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "progress", new MediaStatus(entity.progress).toString());

        RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + entity.idMedia, "detail", entity.toString());

        MediaTagTokenizer.storeTags(entity.tags, entity.idMedia);

    }

    /**
     * Update location.
     *
     * @param location   the location
     * @param idMedia    the id media
     * @throws SQLException the sQL exception
     */
    public void updateLocation(String location, Long idMedia) {

        try {

            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia
                    , "location", location);;

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    /**
     * Save fresh social principles.
     *
     * @param entity the entity
     * @param idUser the id user
     * @return the long
     * @throws SQLException the sQL exception
     */
    public long saveFreshSocialPrinciples(DcUsersEntity entity, Long idUser, String lang, String clangs) throws SQLException {

        try {

            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "id_user", idUser + "");
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "username", entity.username);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "hash", entity.hash);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "email", entity.email);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "avatar", entity.avatar);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "wallpic", entity.wallpic);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "password", entity.secword);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "id_fb", entity.idFBSocial + "");
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "id_go", entity.idGSocial);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "settings", new DcUserSettings().toString());
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "profile", entity.profile.toString());
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "detail", entity.toString());
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "verified", false+"");
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "language", (lang==null||lang.isEmpty())?"en":lang);
            //RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "extlang:" + idUser, (lang==null||lang.isEmpty())?"en":lang);

            if(clangs!=null && !clangs.isEmpty()){
                String[] contentLangs = clangs.split(",");
                for(String lg: contentLangs) {
                    String langCode = lg.trim();
                    boolean trueLangCode = RMemoryAPI.getInstance().pullIfSetElem(Constants.TRANSLATION + ":list", langCode);
                    if(trueLangCode){
                        RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "extlang:" + idUser, langCode);
                    }
                    else {
                        RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "extlang:" + idUser, (checkForLang(lang))?"en":lang);
                    }
                }
            }

            if(RMemoryAPI.getInstance().checkSetElemCount(Constants.USER_KEY + "extlang:" + idUser)==0){
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "extlang:" + idUser, (checkForLang(lang))?"en":lang);
            }

            RMemoryAPI.getInstance().pushElemToMemory(Constants.USER_HASH + entity.hash, -1, entity.idUser + "");


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return idUser;
    }

    /**
     * Activate user account.
     *
     * @param idUser the id user
     * @param value  the value
     * @throws SQLException the sQL exception
     */
    public void activateUserAccount(Long idUser, String value) throws SQLException {

        try {

            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "detail");
            DcUsersEntity user = GsonInsta.getInstance().fromJson(val, DcUsersEntity.class);

            if (user != null) {
                user.enabled  = (1);

                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "hash", value);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "detail", user.toString());
                RMemoryAPI.getInstance().pushElemIfMemory(Constants.USER_HASH + value, -1, idUser + "");
            }


        } catch (Exception e) {
            log.error("ERROR IN UPDATING - ", e);

        }

    }

    /**
     * Save formatted location.
     *
     * @param local   the local
     * @param idMedia the id media
     * @throws SQLException the sQL exception
     */
    protected void saveFormattedLocation(String local, Long idMedia) throws SQLException {
        try {
            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia
                    , "full_location", local);
            //RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia
            //        , "location", local);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    /**
     * Update r media info.
     *
     * @param entity the entity
     * @throws SQLException the sQL exception
     */
    public void updateRMediaInfo(DcMediaEntity entity) throws SQLException {

        try {
            DcMediaEntity media = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "detail", DcMediaEntity.class);

            if (media != null) {
                media.setTitle(entity.getTitle());
                media.setDescription(entity.getDescription());
                if (media.getIdCategory() != entity.getIdCategory()) {
                    updateMediaCategory(media.getIdCategory(), entity.getIdCategory(), entity.getIdMedia());
                }
                media.setIdCategory(entity.getIdCategory());
                media.setTags(entity.getTags());
                long oldChannel = media.getIdChannel();
                media.setIdChannel(entity.getIdChannel());
                //media.setProps(entity.getProps());

                //MediaTagTokenizer.storeTags(entity.getTags(), entity.getIdMedia(), media.getIdCategory());

                log.debug("OLD one: {}, NEW one: {}", entity.getIdChannel(), oldChannel);
                addVideoCountToChannel(entity.getIdChannel(), oldChannel, entity.getIdMedia());

              //  changePrivacy(entity.getProps(), entity.getIdCategory(), entity.getIdMedia());
                RMemoryAPI.getInstance().delFromListElem(Constants.ARCHIVE + media.getIdUser()
                        , entity.getIdMedia() + "");

                RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia()
                        , "detail", media.toString());

            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

    }

    /**
     * Logout r user.
     *
     * @param idUser the id user
     */
    public void logoutRUser(Long idUser, String device) {

        try {
            RMemoryAPI.getInstance().delFromMemory(Constants.DEVICE_KEY + device);
            RMemoryAPI.getInstance().delFromSetElem(Constants.USER_KEY + "device:android:" + idUser, device);
            RMemoryAPI.getInstance().delFromSetElem(Constants.USER_KEY + "device:ios:" + idUser, device);
            RMemoryAPI.getInstance().delFromSetElem(Constants.USER_KEY + "device:" + idUser, device);
            PushNotifier pushNotifier = new PushNotifier();
            pushNotifier.sendTokenRemoveQueue(device);

        } catch (Exception e) {
            log.error("ERROR IN DELETING DEVICE REDIS", e);
        }

    }

    /**
     * Update r media tech info.
     *
     * @param entity the entity
     * @throws SQLException the sQL exception
     */
    public void updateRMediaTechInfo(DcMediaEntity entity) {
        try {
            log.debug("UPDATING TECH PROPS {}", entity);
            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "detail");
            DcMediaEntity media = GsonInsta.getInstance().fromJson(val, DcMediaEntity.class);

            if (media != null) {
                media.setDuration(entity.getDuration());

                RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "detail", media.toString());
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    /**
     * Update r media privacy.
     *
     * @param entity the entity
     */
    public void updateRMediaPrivacy(DcMediaEntity entity) {

        try {
            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "detail");
            DcMediaEntity media = GsonInsta.getInstance().fromJson(val, DcMediaEntity.class);

//            if (media != null) {
//                int oldPrivacy = media.getProps();
//
//                media.setProps(entity.getProps());
//                if (entity.getProps() != 1) {
//                    changePrivacy(entity.getProps(), media.getIdCategory(), media.getIdMedia());
//                }
//
//                RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "detail", media.toString());
//            }

            RMemoryAPI.getInstance().delFromListElem(Constants.ARCHIVE + entity.getIdUser(), entity.getIdMedia() + "");
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }


    }

    /**
     * Update r user info.
     *
     * @param entity the entity
     * @throws SQLException the sQL exception
     */
    public void updateRUserInfo(DcUsersEntity entity, String lang) throws SQLException {
        try {

            String idUser = entity.idUser+"";
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "username", entity.username);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "hash", entity.hash);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "email", entity.email);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "password", entity.secword);
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.USER_KEY + idUser, "profile", new GsonBuilder().create().toJson(entity.profile));
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "verified", entity.verified + "");
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "language", entity.lang);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "detail", entity.toString());

            if(!entity.avatar.contains("http")) {
                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "avatar", entity.avatar);
            }
            if(!entity.wallpic.contains("http")) {
                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "wallpic", entity.wallpic);
            }


            RMemoryAPI.getInstance().pushElemToMemory(Constants.USER_HASH + entity.hash, -1, idUser);


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

    }

    /**
     * Update r user ava.
     *
     * @param entity the entity
     * @throws SQLException the sQL exception
     */
    public void updateRUserAva(DcUsersEntity entity) throws SQLException {
        try {

            Long idUser = entity.idUser;

            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "detail");
            DcUsersEntity user = GsonInsta.getInstance().fromJson(val, DcUsersEntity.class);

            if (user != null) {
                user.avatar = (entity.avatar);

                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "avatar", entity.avatar);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "detail", user.toString());
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

    }

    /**
     * Update r notification params.
     *
     * @param json   the json
     * @param idUser the id user
     * @throws SQLException the sQL exception
     */
    //TODO
    public void updateRNotificationParams(DcUserSettings json, long idUser) throws SQLException {

        try {
            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "detail");
            DcUsersEntity user = GsonInsta.getInstance().fromJson(val, DcUsersEntity.class);

            if (user != null) {
                //user.setSettings(json);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "settings", json.toString());
                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "detail", user.toString());
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    /**
     * Change r account profile.
     *
     * @param idUser the id user
     * @param newP   the new p
     * @throws SQLException the sQL exception
     */
    public void changeRAccountProfile(long idUser, String newP) throws SQLException {

        try {
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "password", newP);
            DcUsersEntity user = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + idUser, "detail", DcUsersEntity.class);

            if (user != null) {
                user.secword = (newP);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "detail", user.toString());
            }

        } catch (Exception e) {
            log.error("ERROR IN CHNG_PASS - ", e);

        }
    }

    /**
     * Update r user device.
     *
     * @param idUser the id user
     * @throws SQLException the sQL exception
     */
    public void updateUserDevice(String devid, long idUser, String devType) {

        try {
            RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "device:"+devType+":" + idUser, devid);
            RMemoryAPI.getInstance().pushElemToMemory(Constants.DEVICE_KEY + devid, 1, devid);
            RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "device:" + idUser, devid);
        } catch (Exception e) {
            log.error("ERROR IN DELETING ", e);
        }

    }

    /**
     * Add r comments.
     *
     * @param entity the entity
     * @throws SQLException the sQL exception
     */
    public void addRComments(DcCommentsEntity entity, long timeFrac) throws SQLException {
        try {
            log.debug("Comment before timefrac: {}", timeFrac);
            String method = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY+entity.idMedia, "method");
            if (method.equals("live")) {
                String liveStopped = RMemoryAPI.getInstance()
                        .pullElemFromMemory(Constants.LIVE_KEY + "system:stop:" + entity.idMedia);
                String idTime = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + entity.idMedia, "start-time");
                if(liveStopped!=null) {
                    String stopTime = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.MEDIA_KEY + entity.idMedia, "stop-time");
                    if(stopTime!=null){
                        timeFrac = (Long.valueOf(stopTime) - Long.valueOf(idTime))-1000;
                    }
                } else {
                    if (idTime != null) {
                        timeFrac = (RMemoryAPI.getInstance().currentTime() - Long.valueOf(idTime));
                    }
                }
            } else if(method.equals("recorded") && timeFrac == 0) {
                String startTime = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + entity.idMedia, "start-time");

                String stopTime = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + entity.idMedia, "stop-time");
                if (stopTime != null) {
                    timeFrac = (Long.valueOf(stopTime) - Long.valueOf(startTime)) - 1000;
                }

            }
            log.debug("Comment after timefrac: {}", timeFrac);
            RMemoryAPI.getInstance()
                    .pushLSetElemToMemory(Constants.MEDIA_KEY + "comment:" + entity.idMedia, timeFrac, entity.idComment+"");
            RMemoryAPI.getInstance()
                    .pushSetElemToMemory(Constants.MEDIA_KEY + "comment:user:" + entity.idUser+":"+entity.idMedia, entity.idComment+"");


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

    }

    /**
     * Like dislike r activity.
     *
     * @param idUser    the id user
     * @param idMedia   the id media
     * @param idActType the id act type
     * @param tp        the tp
     * @throws SQLException the sQL exception
     */
    public void likeDislikeRActivity(long idUser, long idMedia, short idActType, int tp) throws SQLException {
        try {

            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "detail");
            DcMediaEntity media = GsonInsta.getInstance().fromJson(val, DcMediaEntity.class);

            String ld = "0";
            if (media != null) {
//                if (idActType == 0 && tp != 0) {
//                    if (tp == 1 && media.getLiked() != 0)
//                        media.setLiked(media.getLiked() - 1);
//                    else if (tp == -1 && media.getDisliked() != 0)
//                        media.setDisliked(media.getDisliked() - 1);
//                } else if (idActType != 0 && tp != 0) {
//                    if (idActType == -1 && tp == 1 && media.getLiked() != 0) {
//                        media.setLiked(media.getLiked() - 1);
//                        media.setDisliked(media.getDisliked() + 1);
//                        ld = "-1";
//                    } else if (idActType == 1 && tp == -1 && media.getDisliked() != 0) {
//                        media.setLiked(media.getLiked() + 1);
//                        media.setDisliked(media.getDisliked() - 1);
//                        ld = "1";
//                    }
//                } else if (idActType == 1) {
//                    media.setLiked(media.getLiked() + 1);
//                    ld = "1";
//                } else if (idActType == -1) {
//                    media.setDisliked(media.getDisliked() + 1);
//                    ld = "-1";
//                }


                RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia, "detail", media.toString());
//                RMemoryAPI.getInstance()
//                        .pushHashToMemory(Constants.MEDIA_KEY + idMedia, "liked", media.getLiked() + "");
//                RMemoryAPI.getInstance()
//                        .pushHashToMemory(Constants.MEDIA_KEY + idMedia, "disliked", media.getDisliked() + "");
//                RMemoryAPI.getInstance()
//                        .pushElemToMemory(Constants.ACTIVITY + idMedia + ":" + idUser, -1, ld);

            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

    }

    /**
     * Add r follower.
     *
     * @param source the source
     * @param dest   the dest
     * @throws SQLException the sQL exception
     */
    public void addRFollower(long source, long dest) {

        try {
            RMemoryAPI.getInstance().pushSetElemToMemory(Constants.FOLLOWERS + dest, source + "");
            RMemoryAPI.getInstance().pushSetElemToMemory(Constants.FOLLOWS + source, dest + "");
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

    }

    /**
     * Un r follow.
     *
     * @param source the source
     * @param dest   the dest
     * @throws SQLException the sQL exception
     */
    public void unRFollow(long source, long dest) throws SQLException {

        try {
            RMemoryAPI.getInstance().delFromSetElem(Constants.FOLLOWERS + dest, source + "");
            RMemoryAPI.getInstance().delFromSetElem(Constants.FOLLOWS + source, dest + "");

            /*Remove Feeds*/
            RMemoryAPI.getInstance().delFromMemory(Constants.USER_KEY + "feed:comment:" + source);
            RMemoryAPI.getInstance().delFromMemory(Constants.USER_KEY + "feed:like:" + source);
            RMemoryAPI.getInstance().delFromMemory(Constants.USER_KEY + "feed:new:" + source);

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    /**
     * Remove r media by owner.
     *
     * @throws SQLException the sQL exception
     */
    public void removeRMediaByOwner(DcMediaEntity media) {

        try {
            if (media != null) {
                RMemoryAPI.getInstance()
                        .delFromSetElem(Constants.CHANNEL_KEY + "videos:" + media.getIdChannel(), media.idMedia + "");

                delFormMediaLists(media.idMedia);
                updateChannelVideoAMount(media.getIdChannel(), media.idMedia);

                MediaTagTokenizer.removeIdMediaFromTag(media.getTags(), media.idMedia);
                RMemoryAPI.getInstance().delFromMemory(Constants.MEDIA_KEY + media.idMedia);
                RMemoryAPI.getInstance().delFromMemory(Constants.MEDIA_KEY + "hls:" + media.idMedia);
                RMemoryAPI.getInstance().delFromMemory(Constants.COMMENT_KEY + media.idMedia);

                Set<String> idPlst = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.MEDIA_PLAYLIST+"id:"+media.idMedia);
                if(idPlst!=null && !idPlst.isEmpty()) {
                    idPlst.forEach(ID -> {
                        RMemoryAPI.getInstance().delFromSetElem(Constants.MEDIA_PLAYLIST+"media:"+ID, media.idMedia+"");
                    });
                }
            }


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }


    /**
     * Save r fresh principles.
     *
     * @param entity the entity
     * @param profile the user profile info
     * @throws SQLException the sQL exception
     */
    public void saveRFreshPrinciples(DcUsersEntity entity, String profile) throws SQLException {

        try {
            String idUser = entity.idUser + "";
            String lang = entity.lang;
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "id_user", idUser);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "username", entity.username);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "hash", entity.hash);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "email", entity.email);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "password", entity.secword);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "profile", profile);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "verified", false + "");
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "stars", entity.stars + "");

            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "detail", entity.toString());
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "language", (lang == null || lang.isEmpty()) ? "en" : lang);

            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "avatar", entity.avatar);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "wallpic", entity.wallpic);
            RMemoryAPI.getInstance().pushElemToMemory(Constants.USER_HASH + entity.hash, -1, idUser);

            /*if(clangs!=null && !clangs.isEmpty()){
                String[] contentLangs = clangs.split(",");
                for(String lg: contentLangs) {
                    String langCode = lg.trim();
                    boolean trueLangCode = RMemoryAPI.getInstance().pullIfSetElem(Constants.TRANSLATION + ":list", langCode);
                    if(trueLangCode){
                        RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "extlang:" + idUser, langCode);
                    } else {
                        RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "extlang:" + idUser, "en");
                    }
                }
            } else {
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "extlang:" + idUser, (checkForLang(lang))?"en":lang);
            }

            if(RMemoryAPI.getInstance().checkSetElemCount(Constants.USER_KEY + "extlang:" + idUser)==0){
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "extlang:" + idUser, (checkForLang(lang))?"en":lang);
            }*/


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    private boolean checkForLang(String lang){
        return lang==null||lang.isEmpty()||!RMemoryAPI.getInstance().pullIfSetElem(Constants.TRANSLATION + ":list", lang);
    }

    /**
     * Del form media lists.
     *
     * @param idMedia the id media
     */
    protected void delFormMediaLists(Long idMedia) {
        //RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_KEY + "ids", idMedia + "");
        HomePageFactory.geoDelFromMediaIds(String.valueOf(idMedia));

        RMemoryAPI.getInstance().delFromSetElem(Constants.LIVES, idMedia + "");
        RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_KEY + "recommended", idMedia + "");

        RMemoryAPI.getInstance().delFromMemory("type:" + EnumNotification.LIKED.value + ":" + idMedia);
        RMemoryAPI.getInstance().delFromMemory("type:" + EnumNotification.COMMENTED.value + ":" + idMedia);
    }

    /**
     * Del form media lists.
     *
     * @param idMedia the id media
     */
    protected void delFormMediaLists(Long idMedia, Integer idCategory) throws Exception {
        //RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_KEY + "ids", idMedia + "");
        HomePageFactory.geoDelFromMediaIds(String.valueOf(idMedia));

        RMemoryAPI.getInstance().delFromSetElem(Constants.LIVES, idMedia + "");
        RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_KEY + "recommended", idMedia + "");

        RMemoryAPI.getInstance().delFromMemory("type:" + EnumNotification.LIKED.value + ":" + idMedia);
        RMemoryAPI.getInstance().delFromMemory("type:" + EnumNotification.COMMENTED.value + ":" + idMedia);

        String categories = (String) RMemoryAPI.getInstance().pullElemFromMemory("cid:" + idCategory, String.class);
        RMemoryAPI.getInstance().delFromListElem(Constants.CETEGORIES + categories, idMedia + "");
    }

    /**
     * Del form media lists.
     *
     * @param idMedia the id media
     */
    public static void delFormMediaLists(String idMedia) {
        //RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_KEY + "ids", idMedia + "");
        HomePageFactory.geoDelFromMediaIds(idMedia);
        RMemoryAPI.getInstance().delFromSetElem(Constants.LIVES, idMedia + "");
        RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_KEY + "recommended", idMedia + "");
        RMemoryAPI.getInstance().delFromMemory("type:" + EnumNotification.LIKED.value + ":" + idMedia);
        RMemoryAPI.getInstance().delFromMemory("type:" + EnumNotification.COMMENTED.value + ":" + idMedia);
    }

    /**
     * Change privacy.
     *
     * @param props      the props
     * @param idCategory the id category
     * @param idMedia    the id media
     * @throws InterruptedException the interrupted exception
     * @throws ExecutionException   the execution exception
     * @throws TimeoutException     the timeout exception
     * @throws IOException          the iO exception
     */
    protected void changePrivacy(Integer props, Integer idCategory, Long idMedia) throws InterruptedException, ExecutionException, TimeoutException, IOException {
        String categories = (String) RMemoryAPI.getInstance().pullElemFromMemory("cid:" + idCategory, String.class);
        if (props == 0) {
            RMemoryAPI.getInstance().pushListElemToMemory(Constants.CETEGORIES + categories, idMedia + "");
            //RMemoryAPI.getInstance().pushListElemToMemory(Constants.MEDIA_KEY + "ids", idMedia + "");

            HomePageFactory.geoAddToMediaIds(String.valueOf(idMedia));

        } else {
            delFormMediaLists(idMedia);
            RMemoryAPI.getInstance().delFromListElem(Constants.CETEGORIES + categories, idMedia + "");

        }

    }

    /**
     * Add video count to channel.
     *
     * @param idChannel the id channel
     * @param idMedia   the id media
     */
    protected void addVideoCountToChannel(Long idChannel, long oldChannel, Long idMedia) {


        if (idChannel > 0) {
            DcChannelsEntity channel = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.CHANNEL_KEY + idChannel, "detail", DcChannelsEntity.class);

            if (channel != null) {

                /*NEW CHANNEL*/
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.CHANNEL_KEY + "videos:" + idChannel, idMedia + "");
                long vcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + idChannel);
                channel.setMcount(vcount);

                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.CHANNEL_KEY + idChannel, "mcount", vcount + "");
                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.CHANNEL_KEY + idChannel, "detail", channel.toString());

            }

        }

        if(idChannel!=oldChannel) {
            updateOldChannel(oldChannel, idMedia);
        }

    }

    protected void updateOldChannel(Long oldChannel, long idMedia) {

        if (oldChannel != 0) {
            String chn = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.CHANNEL_KEY + oldChannel, "detail");


            if (chn != null && !chn.isEmpty()) {

                DcChannelsEntity channel = GsonInsta.getInstance().fromJson(chn, DcChannelsEntity.class);
                RMemoryAPI.getInstance().delFromSetElem(Constants.CHANNEL_KEY + "videos:" + oldChannel,
                        idMedia + "");

                long vcount = RMemoryAPI.getInstance()
                        .checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + oldChannel);
                channel.setMcount(vcount);

                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.CHANNEL_KEY + oldChannel, "mcount", vcount + "");

                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.CHANNEL_KEY + oldChannel, "detail", channel.toString());
            }
        }
    }

    /**
     * Update channel video a mount.
     *
     * @param idChannel the id channel
     */
    protected void updateChannelVideoAMount(Long idChannel, Long idMedia) {


        try {
            if (idChannel != 0) {

                DcChannelsEntity channel = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.CHANNEL_KEY + idChannel, "detail", DcChannelsEntity.class);

                if (channel != null) {

                    RiakTP transport = RiakAPI.getInstance();
                    IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                    RMemoryAPI.getInstance().delFromSetElem(Constants.CHANNEL_KEY + "videos:" + idChannel, idMedia + "");


                    long vcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + idChannel);

                    channel.mcount = (vcount);
                    saveFactory.updChannelContent(channel);

                    RMemoryAPI.getInstance()
                            .pushHashToMemory(Constants.CHANNEL_KEY + idChannel, "mcount", vcount + "");
                    RMemoryAPI.getInstance()
                            .pushHashToMemory(Constants.CHANNEL_KEY + idChannel, "detail", channel.toString());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMediaCategory(int oldCateg, int newCateg, Long idMedia) throws InterruptedException, ExecutionException, TimeoutException, IOException {

        String oldCategoryName = (String) RMemoryAPI.getInstance().pullElemFromMemory("cid:" + oldCateg, String.class);
        String newCategoryName = (String) RMemoryAPI.getInstance().pullElemFromMemory("cid:" + newCateg, String.class);

        log.debug("UPDATE MEDIA CATEGORY: {}, {}", oldCategoryName, newCategoryName);
        RMemoryAPI.getInstance().delFromListElem(Constants.CETEGORIES + oldCategoryName, idMedia + "");

        RMemoryAPI.getInstance().pushListElemToMemory(Constants.CETEGORIES + newCategoryName, idMedia + "");
    }
}
