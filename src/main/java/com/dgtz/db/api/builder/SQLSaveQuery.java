package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.DcCommentsEntity;
import com.brocast.riak.api.beans.DcMediaEntity;
import com.brocast.riak.api.beans.DcNotificationTypes;
import com.brocast.riak.api.beans.DcUsersEntity;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakQueryFactory;
import com.brocast.riak.api.factory.IRiakSaveFactory;
import com.brocast.riak.api.factory.RiakQueryFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.db.api.beans.DcDeviceType;
import com.dgtz.db.api.beans.FeedNote;
import com.dgtz.db.api.dao.db.DBUtils;
import com.dgtz.db.api.dao.db.JDBCUtil;
import com.dgtz.db.api.domain.*;
import com.dgtz.db.api.enums.EnumAggregations;
import com.dgtz.db.api.enums.EnumSQLErrors;
import com.dgtz.db.api.factory.DataBaseAPI;
import com.dgtz.db.api.factory.GsonInsta;
import com.dgtz.db.api.features.MD5;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.GsonBuilder;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 6/19/14
 */
public abstract class SQLSaveQuery extends RedisSaveTemplate {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SQLSaveQuery.class);
    private static final long LIMIT = 12;

    public void saveInfoInMemory(String key, String value, String exp) {

        try {
            RMemoryAPI.getInstance().pushElemToMemory(key, Integer.valueOf(exp), value);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    public EnumSQLErrors updateLiveInfo(long idLive, int progress)  {

        EnumSQLErrors errors = EnumSQLErrors.OK;

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcMediaEntity entity = queryFactory.queryMediaDataByID(idLive);

            entity.progress = progress;
            entity.method = "recorded";
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.updMediaContent(entity);
            super.updateLiveInfo(idLive, progress);

        } catch (Exception e) {
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
            log.error("ERROR IN UPDATING - " + e.getMessage());
        }

        return errors;
    }

    public EnumSQLErrors publishLive(long idLive, int progress) {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcMediaEntity e = queryFactory.queryMediaDataByID(idLive);

            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            e.progress = progress;
            saveFactory.updMediaContent(e);
            super.publishLive(idLive, progress);

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }
        return errors;
    }

    public EnumSQLErrors updMediaDeviceParam(long idMedia, long idUser, int type, int method, String data) throws SQLException {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            String[] params = data.split("@");
            dbConnection = JDBCUtil.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(
                    "INSERT INTO dc_user_stats (id_user,id_media, action_type, ip, app_version, device_name, method)" +
                            " VALUES (?,?,?,?,?,?,?)");

            preparedStatement.setLong(1, idUser);
            preparedStatement.setLong(2, idMedia);
            preparedStatement.setInt(3, type);
            preparedStatement.setString(4, params[0]);
            preparedStatement.setString(5, params[1]);
            preparedStatement.setString(6, params[2]);
            preparedStatement.setInt(7, method);

            preparedStatement.executeUpdate();

        } catch (Exception e) {
            DBUtils.dbRollback(dbConnection);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
            log.error("ERROR IN DB API ", e);
        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, null);
        }
        return errors;
    }

    public EnumSQLErrors updMediaDeviceParam(long idMedia, String dname)  {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia, "device", dname);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + "properties:" + idMedia, "device", dname);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }
        return errors;
    }

    public EnumSQLErrors saveContentInfo(DcMediaEntity entity, String lang) {
        EnumSQLErrors errors = EnumSQLErrors.OK;

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.buildMediaContent(entity);
            super.saveContentRedisInfo(entity, false);
        } catch (Exception e) {
            errors = EnumSQLErrors.UPDATE_SQL_ERROR;
            log.error("ERROR IN INSERTING - ", e);
        }
        return errors;
    }

    public EnumSQLErrors saveTextContentInfo(DcMediaEntity entity, String lang) {
        
        EnumSQLErrors errors = EnumSQLErrors.OK;

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.buildMediaContent(entity);

            RMemoryAPI.getInstance()
                    .pushSetElemToMemory(Constants.CHANNEL_KEY + "videos:" + entity.idChannel, entity.idMedia + "");
            long mcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + entity.idChannel);
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.CHANNEL_KEY + entity.idChannel, "mcount", mcount+"");

        } catch (Exception e) {
            errors = EnumSQLErrors.UPDATE_SQL_ERROR;
            log.error("ERROR IN INSERTING - ", e);
        } 

        return errors;
    }

    public EnumSQLErrors linkSocialNetworkAcc(DcUsersEntity entity, String idSocial, String stp) {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            RiakTP transport = RiakAPI.getInstance();
            RiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcUsersEntity ent = queryFactory.queryUserDataByIDSocial(idSocial, stp);
            if(ent!=null){
                errors = EnumSQLErrors.ALREADE_CONNECTED_SOCIAL;
            }
            else {
                RiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                saveFactory.updUserData(entity);
            }

        } catch (Exception e) {
            log.error("ERROR IN INSERTING - ", e);

        }

        return errors;
    }

    public EnumSQLErrors activateFreshBody(DcUsersEntity entity)  {

        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            String jsonProfile = new GsonBuilder().create().toJson(entity.profile);
            super.saveRFreshPrinciples(entity, jsonProfile);
        } catch (Exception e) {
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
            log.error("ERROR IN DB API ", e);
        }

        return errors;
    }

    protected void saveFormattedLocation(String local, Long idMedia)  {
        PreparedStatement preparedStatement = null;
        try {
            super.saveFormattedLocation(local, idMedia);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    public EnumSQLErrors updateContentInfo(DcMediaEntity entity) {

        EnumSQLErrors errors = EnumSQLErrors.OK;

        try {
            RiakTP transport = RiakAPI.getInstance();

            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcMediaEntity e = queryFactory.queryMediaDataByID(entity.idMedia);

            if(!Objects.equals(e.idChannel, entity.idChannel)){
                if(entity.idChannel!=0) {
                    RMemoryAPI.getInstance()
                            .pushSetElemToMemory(Constants.CHANNEL_KEY + "videos:" + entity.idChannel, entity.idMedia + "");
                    long mcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + entity.idChannel);
                    RMemoryAPI.getInstance()
                            .pushHashToMemory(Constants.CHANNEL_KEY + entity.idChannel, "mcount", mcount + "");
                }

                if(e.idChannel!=0){
                    RMemoryAPI.getInstance()
                            .delFromSetElem(Constants.CHANNEL_KEY + "videos:" + e.idChannel, e.idMedia + "");
                    long mcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + e.idChannel);
                    RMemoryAPI.getInstance()
                            .pushHashToMemory(Constants.CHANNEL_KEY + e.idChannel, "mcount", mcount + "");
                }
            }
            log.debug("Edititng video: original: {}", e.toString() );
            e.title = entity.title;
            e.description = entity.description;
            e.idChannel = entity.idChannel;
            e.tags = entity.tags;
            e.liveProps.rtmp_url = entity.liveProps.rtmp_url;
            e.liveProps.hls_url = entity.liveProps.hls_url;
            e.liveProps.mp4_url = entity.liveProps.mp4_url;
            e.liveProps.debate = entity.liveProps.debate;
            e.liveProps.position = entity.liveProps.position;
            e.method = entity.method;

            log.debug("Edititng video: after: {}", e.toString() );

            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.updMediaContent(e);

            super.saveContentRedisInfo(e, true);

        } catch (Exception e) {
            log.error("ERROR IN UPDATING - " + e.getMessage());
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }
        return errors;
    }


    public EnumSQLErrors logoutUser(Long idUser, String device)  {

        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            super.logoutRUser(idUser, device);
        } catch (Exception e) {
            log.error("ERROR IN DELETING ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }

        return errors;
    }

    public EnumSQLErrors updateMediaTechInfo(DcMediaEntity entity)  {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcMediaEntity e = queryFactory.queryMediaDataByID(entity.idMedia);

            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            e.duration = entity.duration;
            saveFactory.updMediaContent(e);

            super.updateRMediaTechInfo(entity);

        } catch (RuntimeException e) {
            errors = EnumSQLErrors.UNKNOWN_ERROR;
            log.error("ERROR IN DB API ", e);
        }

        return errors;
    }

    public EnumSQLErrors updateMediaStatus(Long idMedia, Short progress)  {

        EnumSQLErrors errors = EnumSQLErrors.OK;
        log.debug("UPDATED MEDIA STATUS: {} {}", idMedia, progress);
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcMediaEntity e = queryFactory.queryMediaDataByID(idMedia);

            e.progress = progress.intValue();
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.updMediaContent(e);

            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia, "progress", new MediaStatus(progress).toString());
            log.debug("UPDATE MEDIA PROGRESS {}", idMedia);

            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "detail");
            DcMediaEntity entity = GsonInsta.getInstance().fromJson(val, DcMediaEntity.class);

            if (progress == 0 && entity != null) {
                entity.setProgress(0);

                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "detail", entity.toString());

                if (entity.getIdChannel() != 0) {
                    super.addVideoCountToChannel(entity.getIdChannel(), 0, idMedia);
                }

            } else if (progress == 2 && entity != null) {
                super.removeRMediaByOwner(entity);
            } else if (entity != null) {
                delFormMediaLists(idMedia, entity.getIdCategory());
            }

            RMemoryAPI.getInstance().delFromMemory(Constants.LIVE_KEY + idMedia);
            RMemoryAPI.getInstance().delFromSetElem(Constants.LIVES, idMedia + "");
            RMemoryAPI.getInstance().delFromSetElem(Constants.MEDIA_KEY + "queue", idMedia + "");
        } catch (Exception e) {
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
            log.error("ERROR IN UPDATING {}", e);
        }

        return errors;
    }

    public EnumSQLErrors updateUserInfo(DcUsersEntity u, String lang)  {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcUsersEntity usersEntity = queryFactory.queryUserDataByIDUser(u.idUser);
            if(usersEntity!=null) {
                usersEntity.username = u.username;
                usersEntity.secword = u.secword;
                usersEntity.fullname = u.fullname;
                usersEntity.email = u.email;
                usersEntity.profile = u.profile;
                if(!u.avatar.contains("http")) {
                    usersEntity.avatar = u.avatar;
                }
                if(!u.wallpic.contains("http")) {
                    usersEntity.wallpic = u.wallpic;
                }
                usersEntity.city = u.city;
                usersEntity.about = u.about;
                usersEntity.country = u.country;
                usersEntity.enabled = 1;
                usersEntity.hash = u.hash;
                usersEntity.idFBSocial = u.idFBSocial;
                usersEntity.idGSocial = u.idGSocial;
                usersEntity.idTWTRSocial = u.idTWTRSocial;
                usersEntity.idVKSocial = u.idVKSocial;
                usersEntity.social_links = u.social_links;
                usersEntity.lang = u.lang;
                usersEntity.verified = u.verified;
                usersEntity.type = u.type; //basic streamer

                IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                saveFactory.updUserData(usersEntity);

                super.updateRUserInfo(u, u.lang);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }

        return errors;
    }

    public EnumSQLErrors updateUserAva(DcUsersEntity entity)  {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            super.updateRUserAva(entity);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }

        return errors;
    }

    public EnumSQLErrors updateNotificationParams(DcNotificationTypes settings, long idUser) {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            RiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.updUserNotificationSettings(idUser + "", settings.push, "push");
            saveFactory.updUserNotificationSettings(idUser+"", settings.email, "email");
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        } 

        return errors;

    }

    public String changeAccountProfile(long idUser, String newP, String hash) {

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcUsersEntity usersEntity = queryFactory.queryUserDataByIDUser(idUser);

            usersEntity.secword = newP;

            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.updUserData(usersEntity);


                log.debug("newP: {} idUser: {} ", newP, idUser);

                String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "hash");

                if (val != null && !val.isEmpty()) {
                    super.changeRAccountProfile(idUser, newP);
                }
        } catch (Exception e) {
            log.error("ERROR IN CH_PASS - ", e);
        }

        return hash;
    }

    public EnumSQLErrors updateUserPass(DcUsersEntity entity) {

        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            changeAccountProfile(entity.idUser, entity.secword, entity.hash);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }
        return errors;
    }

    public EnumSQLErrors removeDevice(String devId) {

        EnumSQLErrors errors = EnumSQLErrors.OK;

        try {

                DcDeviceType dev = (DcDeviceType) RMemoryAPI.getInstance().pullElemFromMemory(Constants.DEVICE_KEY + devId, DcDeviceType.class);

                if(dev!=null) {
                    RMemoryAPI.getInstance().delFromSetElem(Constants.USER_KEY + "device:" + dev.getIdUser(), devId);
                }
                RMemoryAPI.getInstance().delFromMemory(Constants.DEVICE_KEY + devId);

        } catch (Exception e) {
            log.error("ERROR IN DELETING - " + e.getMessage());
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }
        return errors;
    }

    public long addComments(DcCommentsEntity entity, long timeFrac) {
        try {
            String username = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + entity.idUser, "username");
            entity.setUsername(username);
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
            }

            log.debug("Comment after timefrac: {}", timeFrac);

            entity.setTkey(timeFrac);
            RiakTP transport = RiakAPI.getInstance();
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.buildMediaComments(entity);
            RMemoryAPI.getInstance()
                    .pushSetElemToMemory(Constants.MEDIA_KEY + "comment:user:" + entity.idUser+":"+entity.idMedia, entity.idComment+"");

            //super.addRComments(entity, timeFrac);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return entity.idComment;
    }

    public void addCounterMediaStat(long idMedia, EnumAggregations type) {

        try {

            if (type == EnumAggregations.REC_VIEWED) {

                RiakTP transport = RiakAPI.getInstance();
                IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                saveFactory.storeMediaStatistics(idMedia, false, "vnumber");
                RMemoryAPI.getInstance()
                        .pushHashIncrToMemory(Constants.MEDIA_KEY + idMedia, "vcount", 1l);
            } else if (type == EnumAggregations.LIVE_VIEWED) {
                RiakTP transport = RiakAPI.getInstance();
                IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                saveFactory.storeMediaStatistics(idMedia, false, "lvnumber");
                RMemoryAPI.getInstance()
                        .pushHashIncrToMemory(Constants.MEDIA_KEY + idMedia, "vcount", 1l);
                RMemoryAPI.getInstance()
                        .pushHashIncrToMemory(Constants.MEDIA_KEY + idMedia, "lvcount", 1l);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

    }

    public EnumSQLErrors reportMediaActivity(long idUser, long idMedia, short idRepType) throws SQLException {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "id_media");
        if (val != null) {
            try {
                dbConnection = JDBCUtil.getDBConnection();


                    preparedStatement = dbConnection.prepareStatement(
                            "INSERT INTO dc_reported (id_media,id_user,id_rep_title) VALUES (?,?,?)");

                    preparedStatement.setLong(1, idMedia);
                    preparedStatement.setLong(2, idUser);
                    preparedStatement.setLong(3, idRepType);

                    preparedStatement.executeUpdate();

            } catch (Exception e) {
                log.error("ERROR IN DB API ", e);
                DBUtils.dbRollback(dbConnection);
            } finally {
                DBUtils.closeConnections(preparedStatement, dbConnection, null);
            }
        } else {
            errors = EnumSQLErrors.NOTHING_UPDATED;
        }

        return errors;
    }

    public EnumSQLErrors addFollower(long source, long dest) {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        super.addRFollower(source, dest);
        return errors;
    }

    public EnumSQLErrors videoSharingCount(long idUser, long idMedia) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.OK;
        try {

            String media = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "id_media");
            if (media != null && !media.isEmpty() && !media.equals("0")) {
                RMemoryAPI.getInstance().pushIncrElemToMemory(Constants.MEDIA_KEY + "share:" + idMedia, 0);
            }
        } catch (IOException e) {
            log.error("ERROR IN DB API ", e);
            sqlErrors = EnumSQLErrors.UNKNOWN_ERROR;
        }
        return sqlErrors;
    }

    public EnumSQLErrors unFollow(long source, long dest) {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            super.unRFollow(source, dest);
        } catch (Exception e) {
            errors = EnumSQLErrors.UNKNOWN_ERROR;
            log.error("ERROR IN DB API ", e);
        }

        return errors;
    }

    public EnumSQLErrors removeMediaByOwner(long id_ouser, long idMedia) {
        EnumSQLErrors errors = EnumSQLErrors.OK;

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcMediaEntity entity = queryFactory.queryMediaDataByID(idMedia);

            log.debug("Deleting object {}: {}", idMedia, entity.idMedia);
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            entity.progress = 3;
            entity.idChannel = 0L;
            saveFactory.updMediaContent(entity);

            log.debug("VIDEO REMOVED: {}", entity.toString());
            super.removeRMediaByOwner(entity);

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        }
        return errors;
    }

    public EnumSQLErrors saveFeedback(long idUser, String text) throws SQLException {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            dbConnection = JDBCUtil.getDBConnection();
                preparedStatement = dbConnection.prepareStatement(
                        "INSERT INTO dc_feedback (id_user,feedtext) VALUES (?,?)");

                preparedStatement.setLong(1, idUser);
                preparedStatement.setString(2, text);

                preparedStatement.executeUpdate();
        } catch (Exception e) {
            DBUtils.dbRollback(dbConnection);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
            log.error("ERROR IN DB API ", e);
        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, null);
        }
        return errors;
    }

    public EnumSQLErrors saveBugTrack(String title, String text) throws SQLException {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            dbConnection = JDBCUtil.getDBConnection();
                preparedStatement = dbConnection.prepareStatement(
                        "INSERT INTO dc_bugtracker (title,message) VALUES (?,?)");

                preparedStatement.setString(1, title);
                preparedStatement.setString(2, text);

                preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
            DBUtils.dbRollback(dbConnection);
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, null);
        }
        return errors;
    }

    public EnumSQLErrors setPersonalLocalization(String local, long idUser) {

        
        EnumSQLErrors errors = EnumSQLErrors.OK;
        try {
            if(local==null){
                throw new Exception("Null local language");
            }

            RiakTP transport = RiakAPI.getInstance();

            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcUsersEntity entity = queryFactory.queryUserDataByIDUser(idUser);
            if(entity!=null) {
                entity.lang = local;

                IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                saveFactory.updUserData(entity);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "language", local);

            } else {
                errors = EnumSQLErrors.NOTHING_UPDATED;
            }

        } catch (Exception e){
            errors = EnumSQLErrors.NOTHING_UPDATED;
            log.error("Error in Riak DB {}", e);
        } 

        return errors;

    }

}
