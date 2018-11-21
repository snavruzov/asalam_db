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
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/22/13
 * Time: 10:20 AM
 * <p/>
 * Inherits <code>SQLTemplate</code> class to overrides <code>IQueryFactory</code> interface
 */

public class QueryFactory extends SQLTemplates implements IQueryFactory {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(QueryFactory.class);

    public QueryFactory() {

    }

    @Override
    public List<MediaNewsStatInfo> castMediaByIdCategory(int idCategory, int sortType, long off, long limit, String lang) {

        List<MediaNewsStatInfo> list = null;
        try {
            list = super.castMediaByIdCategory(idCategory, sortType, off, limit, lang);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return list;

    }

    @Override
    public DcMediaEntity getMediaById(long idMedia) {
        return super.getMediaById(idMedia);
    }

    @Override
    public long getLiveViewCount(long idLive) {
        long count = 0;
        try {
            count = super.getLiveViewCount(idLive);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return count;
    }

    @Override
    public List<MediaNewsStatInfo> castMediaByLive(long off, long limit) {

        List<MediaNewsStatInfo> result = null;
        try {
            result = super.castMediaByLive(off, limit);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return result;
    }

    @Override
    public List<MediaNewsStatInfo> castMediaByStats(EnumAggregations enumAgr, String user_lang, Set<String> langs, long off, long limit) {
        List<MediaNewsStatInfo> objects = null;
        try {
            objects = super.castMediaByStats(enumAgr, user_lang, langs, off, limit);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return objects;

    }

    @Override
    public Map<Long, List<String>> castRatingsByLive(String idLive) {
        return super.castRatingsByLive(idLive);
    }

    @Override
    public Map<Long, List<DcCommentsEntity>> castCommentsByIdLive(long idMedia,int duration, long off, long limit, boolean reverse, boolean sort){
        return super.castCommentsByIdLive(idMedia,duration, off, limit, reverse, sort);
    }


    @Override
    public List<MediaMappingStatInfo> castMediaByLatLng() {


        List<MediaMappingStatInfo> objects = null;
        try {
            objects = super.castMediaByLatLng();
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return objects;

    }

    @Override
    public List<DcMediaEntity> castMediaByFeatured() {


        List<DcMediaEntity> objects = null;
        try {
            objects = super.castMediaByFeatured();
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return objects;

    }

    public DcUsersEntity getUserInfoByGooID(String email, String idGoo) {
        DcUsersEntity usersEntity = null;
        try {
            usersEntity = super.getUserInfoByGooID(email, idGoo);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return usersEntity;
    }

    public Set<EmailRecipients> getUserEmail(long idUser, int type) {
        Set<EmailRecipients> list = new HashSet<>();
        try {
            list = super.getUserEmail(idUser, type);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return list;
    }

    @Override
    public DcLiveMediaEntity castLiveById(long idLive) {
        DcLiveMediaEntity dcLiveMediaEntity = new DcLiveMediaEntity();
        try {
            dcLiveMediaEntity = super.castLiveById(idLive);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return dcLiveMediaEntity;
    }


    @Override
    public DcMediaEntity castMediaById(long idMedia) {
        return super.castMediaById(idMedia);
    }

    @Override
    public String getCountryLanguages(String code) {
        try {
            code = super.getCountryLanguages(code);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return code;
    }

    public String extractCountriesByCode(String code) {
        String country = "";
        try {
            country = super.extractCountriesByCode(code);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return country;
    }

    public Long getUserFollowsCount(long idUser, int type) {
        Long count = 0L;
        try {
            count = super.getUserFollowsCount(idUser, type);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return count;
    }

    public Long getUserVideosCount(long idUser, boolean mine) {
        Long count = 0L;
        try {
            count = super.getUserVideosCount(idUser, mine);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return count;
    }

    public Long getUserChannelsCount(long idUser, boolean mine) {
        Long count = 0L;
        try {
            count = super.getUserChannelsCount(idUser, mine);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return count;
    }

    @Override
    @Deprecated
    public DcMediaEntity castSimpleMediaById(long idMedia) {
        DcMediaEntity result = null;
        try {
            result = super.castSimpleMediaById(idMedia);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return result;
    }

    @Override
    public DcUsersEntity getUserInfoByEmail(String email) {
        DcUsersEntity usersEntity = new DcUsersEntity();
        try {
            usersEntity = super.getUserInfoByEmail(email);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return usersEntity;
    }

    @Override
    public DcUsersEntity getUserInfoByID(Long idUser) {
           return super.getUserInfoByID(idUser);
    }

    @Override
    public DcUsersEntity getUserInfoByHash(String hash) {
        DcUsersEntity usersEntity = null;
        try {
            usersEntity = super.getUserInfoByHash(hash);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return usersEntity;
    }

    @Override
    public DcUsersEntity getUserInfoByFB(String hash) {
        DcUsersEntity usersEntity = null;
        try {
            usersEntity = super.getUserInfoByFB(hash);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return usersEntity;
    }

    @Override
    public DcUsersEntity getUserInfoByVKID(String idVk) {
        DcUsersEntity usersEntity = null;
        try {
            usersEntity = super.getUserInfoByVKID(idVk);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return usersEntity;
    }

    @Override
    public DcUsersEntity getUserInfoByTwitterID(String idTw) {
        DcUsersEntity usersEntity = null;
        try {
            usersEntity = super.getUserInfoByTwitterID(idTw);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return usersEntity;
    }

    @Override
    public DcUsersEntity getUserInfoByFbID(String email, String idFb) {
        DcUsersEntity usersEntity = null;
        try {
            usersEntity = super.getUserInfoByFbID(email, idFb);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return usersEntity;
    }

    public DcNotificationTypes getUserNotificationSettings(long idUser) {
        DcNotificationTypes usersEntity = null;
        try {
            usersEntity = super.getUserNotificationSettings(idUser);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return usersEntity;
    }

    @Override
    public List<DcCommentsEntity> getCommentsByIdMedia(long idMedia, long off, long limit) {

        List<DcCommentsEntity> result = null;
        try {
            result = super.getCommentsByIdMedia(idMedia, off, limit);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return result;
    }

    @Override
    public List<MediaNewsStatInfo> castMediaInArchive(long idUser, long offset, long limit, String lang) {
        List<MediaNewsStatInfo> result = null;
        try {
            result = super.castMediaInArchive(idUser, offset, limit, lang);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return result;
    }

    @Override
    public List<DcDebateEntity> castMediaInDebate(long idMedia) {
            return super.castMediaInDebate(idMedia);
    }

    @Override
    public List<DcLocationsEntity> extractAllCountries() {
        List<DcLocationsEntity> list = null;
        try {
            list = super.extractAllCountries();
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return list;
    }

    @Override
    public long extractUniqueIdForLive() {
        long value = 0;
        try {
            value = super.extractUniqueIdForLive();
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return value;
    }

    @Override
    public long extractUniqueIdForUser() {
        long value = 0;
        try {
            value = super.extractUniqueIdForUser();
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return value;
    }

    @Override
    public long extractUniqueIdForMedia() {
        long value = 0;
        try {
            value = super.extractUniqueIdForMedia();
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return value;
    }

    @Override
    public short extractVideoRateByUserAVideo(long idUser, long idMedia) {
        short value = 0;
        try {
            value = super.extractVideoRateByUserAVideo(idUser, idMedia);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return value;
    }

    @Override
    public short extractVideoReportByUserAct(long idUser, long idMedia) {
        short val = 0;
        try {
            val = super.extractVideoReportByUserAct(idUser, idMedia);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return val;
    }

    @Override
    public List<MediaPublicInfo> extractUsersContentByIdUser(long idUser, int offset, int limit, boolean isMine) {
            return super.extractUsersContentByIdUser(idUser, offset, limit, isMine);
    }

    @Override
    public List<MediaPublicInfo> extractUsersEventContentByIdUser(long idUser, int offset, int limit, boolean isMine) {
        return super.extractUsersEventContentByIdUser(idUser, offset, limit, isMine);
    }

    @Override
    public List<MediaPublicInfo> extractUsersContentByTOP(long idUser, int offset, int limit) {
        return super.extractUsersContentByTOP(idUser, offset, limit);
    }

    @Override
    public DcUsersEntity getUserProfileInfoById(long idUser) {
        DcUsersEntity usersEntity = null;

        try {
            usersEntity = super.getUserProfileInfoById(idUser);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return usersEntity;
    }

    @Override
    public long validateKeyPreUpdate(String key) {
        long val = 0;
        try {
            val = super.validateKeyPreUpdate(key);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return val;
    }


    public long extractVideoIsFollowed(long source, long dest) {
        long val = 0;
        try {
            val = super.extractVideoIsFollowed(source, dest);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return val;

    }

    @Override
    public List<UserShortInfo> extractPublicUserFollowers(long idUser, long idSource, long off, long limit, String lang) {
        List<UserShortInfo> dcFollowsEntities = null;

        try {
            dcFollowsEntities = super.extractPublicUserFollowers(idUser, idSource, off, limit, lang);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return dcFollowsEntities;
    }

    @Override
    public List<UserShortInfo> extractPublicIntersectUserFollowers(long idUser, long off, long limit, String lang) {
        List<UserShortInfo> dcFollowsEntities = null;

        try {
            dcFollowsEntities = super.extractPublicIntersectUserFollowers(idUser, off, limit, lang);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return dcFollowsEntities;
    }

    @Override
    public List<UserShortInfo> extractPublicViewersList(long idUser, long off, long limit) {
            return  super.extractPublicViewersList(idUser, off, limit);
    }

    @Override
    public List<UserShortInfo> searchMyUserFollowsByName(String name, Long idUser, int off, int limit) {
            return  super.searchMyUserFollowsByName(name, idUser, off, limit);
    }

    @Override
    public List<UserShortInfo> searchUserFollowsByName(String name, Long source, Long dest, String direct, int off, int limit) {
        return  super.searchUserFollowsByName(name, source, dest, direct, off, limit);
    }

    @Override
    public List<UserShortInfo> searchMyUserInterFollowsByName(String name, Long idUser, int off, int limit) {
        return  super.searchMyUserInterFollowsByName(name, idUser, off, limit);
    }

    @Override
    public List<UserShortInfo> searchMyUserFollowersByName(String name, Long idUser, int off, int limit) {
        return  super.searchMyUserFollowersByName(name, idUser, off, limit);
    }

    @Override
    public List<UserShortInfo> searchAllUsersByName(String name, Long idUser, int off, int limit) {
        return  super.searchAllUsersByName(name, idUser, off, limit);
    }

    @Override
    public List<UserShortInfo> extractPublicUserFollowing(long idUser, long idSource, long off, long limit, String lang) {
        List<UserShortInfo> list = null;
        try {
            list = super.extractPublicUserFollowing(idUser, idSource, off, limit, lang);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return list;
    }

    @Override
    public String checkMediaCompressStatus(long idMedia) {
        String str = null;
        try {
            str = super.checkMediaCompressStatus(idMedia);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return str;
    }

    @Override
    public String testMemcached(long idUser) {
        return null;
    }

}
