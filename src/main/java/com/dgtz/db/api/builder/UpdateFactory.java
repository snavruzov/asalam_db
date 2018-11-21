package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.*;
import com.dgtz.db.api.enums.EnumAggregations;
import com.dgtz.db.api.enums.EnumSQLErrors;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/24/13
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */

public class UpdateFactory extends SQLSaveQuery implements ISaveFactory {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(UpdateFactory.class);

    public UpdateFactory() {
    }

    @Override
    public EnumSQLErrors saveMediaInfo(DcMediaEntity entity, String lang) {

        EnumSQLErrors out = EnumSQLErrors.UNKNOWN_ERROR;
        try {
            out = super.saveMediaInfo(entity, lang);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return out;
    }


    @Override
    public void saveInfoInMemory(String key, String value, String exp) {
        try {
            super.saveInfoInMemory(key, value, exp);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    @Override
    public boolean saveMediaInfoIntoMem(DcMediaEntity entity) {
        return true;
    }

    @Override
    public EnumSQLErrors updateLiveInfo(long idLIve, int progress) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_ERROR;
        try {
            errors = super.updateLiveInfo(idLIve, progress);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return errors;
    }

    public EnumSQLErrors updMediaDeviceParam(long idMedia, String dname) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_ERROR;
        try {
            errors = super.updMediaDeviceParam(idMedia, dname);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return errors;

    }

    @Override
    public EnumSQLErrors updMediaDeviceParam(long idMedia, long idUser, int type, int method, String data) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.updMediaDeviceParam(idMedia, idUser, type, method, data);
        } catch (SQLException e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }



    @Override
    public EnumSQLErrors publishLive(long idLIve, int progress) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.publishLive(idLIve, progress);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors updateRealTimeLiveInfo(DcMediaEntity entity) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.updateRealTimeLiveInfo(entity);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors linkSocialNetworkAcc(DcUsersEntity entity, String idSocial, String stp) {
        return super.linkSocialNetworkAcc(entity, idSocial, stp);
    }

    @Override
    public EnumSQLErrors saveContentInfo(DcMediaEntity entity, String lang) {
        return super.saveContentInfo(entity, lang);
    }

    @Override
    public EnumSQLErrors saveTextContentInfo(DcMediaEntity entity, String lang) {
        return super.saveTextContentInfo(entity, lang);
    }

    @Override
    public void updateLocation(String location, Long idMedia) {
        try {
            super.updateLocation(location, idMedia);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }


    @Override
    public long saveFreshSocialPrinciples(DcUsersEntity entity, Integer type, String lang, String clang) {
        long val = 0;
        try {
            val = super.saveFreshSocialPrinciples(entity, entity.idUser, lang, clang);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return val;
    }

    @Override
    public EnumSQLErrors activateFreshBody(DcUsersEntity entity) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_ERROR;
        try {
            errors = super.activateFreshBody(entity);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return errors;
    }

    @Override
    public void saveFormattedLocation(String local, Long idMedia) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_ERROR;
        try {
            super.saveFormattedLocation(local, idMedia);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }


    @Override
    public EnumSQLErrors updateContentInfo(DcMediaEntity entity) {
            return super.updateContentInfo(entity);
    }

    @Override
    public EnumSQLErrors logoutUser(Long idUser, String device) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            errors = super.logoutUser(idUser, device);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return errors;
    }

    @Override
    public EnumSQLErrors updateMediaTechInfo(DcMediaEntity entity) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.updateMediaTechInfo(entity);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors updateMediaStatus(Long idMedia, Short progress) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.updateMediaStatus(idMedia, progress);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors updateUserInfo(DcUsersEntity entity, String lang) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;

        try {
            errors = super.updateUserInfo(entity, lang);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return errors;
    }

    @Override
    public EnumSQLErrors updateUserAva(DcUsersEntity entity) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;

        try {
            errors = super.updateUserAva(entity);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return errors;
    }

    @Override
    public EnumSQLErrors updateNotificationParams(DcNotificationTypes json, long idUser) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;

        try {
            errors = super.updateNotificationParams(json, idUser);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return errors;
    }

    public String changeAccountProfile(long idUser, String newP, String hash) {
        String newHash = "";
        try {
            newHash = super.changeAccountProfile(idUser, newP, hash);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return newHash;
    }

    @Override
    public EnumSQLErrors updateUserPass(DcUsersEntity entity) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;

        try {
            errors = super.updateUserPass(entity);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return errors;
    }

    @Override
    public void updateUserDevice(String newId, long idUser, String devType) {
        super.updateUserDevice(newId, idUser, devType);
    }

    @Override
    public EnumSQLErrors removeDevice(String devId) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_ERROR;
        try {
            errors = super.removeDevice(devId);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return errors;
    }

    @Override
    public long addComments(DcCommentsEntity entity, long timeFrac) {
            return  super.addComments(entity, timeFrac);
    }

    @Override
    public void addCounterMediaStat(long idMedia, EnumAggregations type) {
            super.addCounterMediaStat(idMedia, type);
    }

    @Override
    public EnumSQLErrors reportMediaActivity(long idUser, long idMedia, short idRepType) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.reportMediaActivity(idUser, idMedia, idRepType);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors addFollower(long source, long dest) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.addFollower(source, dest);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors videoSharingCount(long idUser, long idMedia) {

        EnumSQLErrors sqlErrors = super.videoSharingCount(idUser, idMedia);

        return sqlErrors;
    }

    @Override
    public EnumSQLErrors unFollow(long source, long dest) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.unFollow(source, dest);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors removeMediaByOwner(long id_ouser, long idMedia) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.removeMediaByOwner(id_ouser, idMedia);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors saveFeedback(long idUser, String text) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.saveFeedback(idUser, text);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors saveBugTrack(String title, String text) {
        EnumSQLErrors sqlErrors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
        try {
            sqlErrors = super.saveBugTrack(title, text);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return sqlErrors;
    }

    @Override
    public EnumSQLErrors setPersonalLocalization(String ids, long idUser) {
        EnumSQLErrors errors = EnumSQLErrors.UNKNOWN_ERROR;
        try {
            errors = super.setPersonalLocalization(ids, idUser);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return errors;
    }

}
