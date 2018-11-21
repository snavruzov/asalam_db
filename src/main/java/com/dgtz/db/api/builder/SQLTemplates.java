package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.DcCommentsEntity;
import com.brocast.riak.api.beans.DcMediaEntity;
import com.brocast.riak.api.beans.DcNotificationTypes;
import com.brocast.riak.api.beans.DcUsersEntity;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakQueryFactory;
import com.brocast.riak.api.factory.RiakQueryFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.db.api.beans.*;
import com.dgtz.db.api.dao.db.JDBCUtil;
import com.dgtz.db.api.domain.*;
import com.dgtz.db.api.enums.EnumAggregations;
import com.dgtz.db.api.enums.EnumPlatform;
import com.dgtz.db.api.factory.GsonInsta;
import com.dgtz.db.api.features.GenSettings;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sardor Navruzov on 1/4/14.
 * <p/>
 * Abstract class realizes the encapsulated logics of <code>IQueryFactory</code> inteface
 * <p/>
 * Implements SQL base of implementaion, inherits <code>RedisQueryTemplate</code> of redis NoSQL realization
 */
public abstract class SQLTemplates extends RedisQueryTemplate {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SQLTemplates.class);
    private static final long LIMIT = 12;

    public SQLTemplates() {
    }


    protected List<MediaNewsStatInfo> castMediaByIdCategory(int idCategory, int sortType, long off, long limit, String lang) {
        return super.castMediaByIdCategory(idCategory, sortType, off, limit, lang);
    }


    protected String getCountryLanguages(String code) throws SQLException {

        String lcode = "en";

                Connection dbConnection = null;

        try {
            dbConnection = JDBCUtil.getDBConnection();
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;

            try {
                preparedStatement = dbConnection.prepareStatement(
                        "SELECT languages FROM countries WHERE code=? LIMIT 1");

                preparedStatement.setString(1, code);

                //TODO migrate to Riak
                rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    String long_language = rs.getString("languages");

                    String short_langs = RMemoryAPI.getInstance().pullElemFromMemory(Constants.TRANSLATION+long_language);
                    if(short_langs!=null && !short_langs.isEmpty()) {
                        lcode = short_langs;
                    }
                }

            } catch (Exception e) {
                log.error("ERROR IN DB API ", e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }


        } catch (SQLException e) {
            log.error("ERROR IN DB API ", e);

        } finally {
            if (dbConnection != null) {
                dbConnection.commit();
                dbConnection.close();
            }
        }

        return lcode;
    }


    protected List<MediaNewsStatInfo> castMediaByStats(EnumAggregations enumAgr, String user_lang, Set<String> langs,
                                                       long off, long limit)  {
        return super.castMediaByStats(enumAgr, user_lang, langs, off, limit);
    }

    protected List<MediaMappingStatInfo> castMediaByLatLng() {
        return super.castMediaByLatLng();
    }

    protected List<DcMediaEntity> castMediaByFeatured() {
        return super.castMediaByFeatured();
    }


    protected DcLiveMediaEntity castLiveById(long idLive)  {

        DcLiveMediaEntity entity = null;
        try {
            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.LIVE_KEY + idLive, "detail");
            entity = GsonInsta.getInstance().fromJson(val, DcLiveMediaEntity.class);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entity;
    }

    /*0 - Followers, 1 - Follows*/
    protected Long getUserFollowsCount(long idUser, int type) {

        Long count = 0L;
        if (type == 0) {
            count = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + idUser);
        } else {
            count = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWS + idUser);
        }

        return count;
    }

    protected DcMediaEntity getMediaById(long idMedia) {

        DcMediaEntity entity = null;
        try {
            entity = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "detail", DcMediaEntity.class);

            if (entity != null) {
                String ratio = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + idMedia, "ratio");
                entity.setRatio(ratio);
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API IN REDIS GETTING", e);
        }

        return entity;

    }

    protected DcMediaEntity castMediaById(long idMedia) {
        DcMediaEntity entity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcMediaEntity e = queryFactory.queryMediaDataByID(idMedia);

            if (e != null) {
                entity = e;
                log.debug("DCMEDIAENT: {}", e.toString());
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entity;
    }

    protected List<MediaPublicInfo> extractUsersContentByIdUser(long idUser, int offset, int limit, boolean isMine) {
        List<MediaPublicInfo> entity = new ArrayList<>();

        
        try {
            RiakTP transport  = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcMediaEntity> mlist = queryFactory.queryUserVideoDataByIduser(idUser,offset,limit);

            String currentTime = String.valueOf(RMemoryAPI.getInstance().currentTimeMillis());
            String username = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + idUser, "username");
            String verified = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + idUser, "verified");

            mlist.forEach(m -> {
                MediaPublicInfo obj = new MediaPublicInfo();
                String thumb = Constants.encryptAmazonURL(idUser, m.idMedia, "jpg", "thumb", Constants.STATIC_URL);
                String thumb_webp = Constants.encryptAmazonURL(idUser, m.idMedia, "webp", "thumb", Constants.STATIC_URL);
                String lcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "liked");
                String vcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "vcount");
                String avatar = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + m.idUser, "avatar");
                String evnt_time = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "evnt_time");

                obj.setIdMedia(m.idMedia);
                obj.setTitle(m.title);
                obj.setDateadded(m.dateadded);
                obj.setIdChannel(m.idChannel);
                obj.setMethod(m.method);
                obj.setStart_time(evnt_time);
                obj.setIdUser(m.idUser);
                obj.setUsername(username);
                obj.setAmount(Long.valueOf(vcount==null?"0":vcount));
                obj.setLiked(Long.valueOf(lcount==null?"0":lcount));
                obj.setProgress(m.progress);
                obj.setCurrenTime(currentTime);
                obj.setRatio(m.ratio);
                obj.setThumb(thumb);
                obj.setThumb_webp(thumb_webp);
                obj.setVerified(verified == null ? false : Boolean.valueOf(verified));
                obj.setAvatar(Constants.STATIC_URL + m.idUser + "/image" + avatar + "M.jpg");
                obj.setDuration(m.duration.shortValue());
                obj.setTags(m.tags);
                obj.setLocation(m.location);

                entity.add(obj);
            });

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entity;
    }

    protected List<MediaPublicInfo> extractUsersEventContentByIdUser(long idUser, int offset, int limit, boolean isMine) {
        List<MediaPublicInfo> entity = new ArrayList<>();


        try {
            RiakTP transport  = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcMediaEntity> mlist = queryFactory.queryUserMediaDataByEvent(idUser, offset, limit);

            String currentTime = String.valueOf(RMemoryAPI.getInstance().currentTimeMillis());
            String username = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + idUser, "username");
            String verified = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + idUser, "verified");

            mlist.forEach(m -> {
                MediaPublicInfo obj = new MediaPublicInfo();
                String thumb = Constants.encryptAmazonURL(idUser, m.idMedia, "jpg", "thumb", Constants.STATIC_URL);
                String thumb_webp = Constants.encryptAmazonURL(idUser, m.idMedia, "webp", "thumb", Constants.STATIC_URL);
                String lcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "liked");
                String vcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "vcount");
                String avatar = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + m.idUser, "avatar");
                String evnt_time = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "evnt_time");

                obj.setIdMedia(m.idMedia);
                obj.setTitle(m.title);
                obj.setDateadded(m.dateadded);
                obj.setIdChannel(m.idChannel);
                obj.setMethod(m.method);
                obj.setStart_time(evnt_time);
                obj.setIdUser(m.idUser);
                obj.setUsername(username);
                obj.setAmount(Long.valueOf(vcount==null?"0":vcount));
                obj.setLiked(Long.valueOf(lcount==null?"0":lcount));
                obj.setProgress(m.progress);
                obj.setCurrenTime(currentTime);
                obj.setRatio(m.ratio);
                obj.setThumb(thumb);
                obj.setThumb_webp(thumb_webp);
                obj.setVerified(verified == null ? false : Boolean.valueOf(verified));
                obj.setAvatar(Constants.STATIC_URL + m.idUser + "/image" + avatar + "M.jpg");
                obj.setDuration(m.duration.shortValue());
                obj.setTags(m.tags);
                obj.setLocation(m.location);

                entity.add(obj);
            });

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entity;
    }

    protected List<MediaPublicInfo> extractUsersContentByTOP(long idUser, int off, int limit) {
        List<MediaPublicInfo> entity = new ArrayList<>();
        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? (int)LIMIT : limit);

        
        try {
            RiakTP transport  = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcMediaEntity> mlist = queryFactory.queryMediaByTOP(off, limit);

            mlist.forEach(m -> {
                MediaPublicInfo obj = new MediaPublicInfo();
                String currentTime = String.valueOf(RMemoryAPI.getInstance().currentTimeMillis());
                String username = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + m.idUser, "username");
                String verified = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + m.idUser, "verified");
                String thumb = Constants.encryptAmazonURL(m.idUser, m.idMedia, "jpg", "thumb", Constants.STATIC_URL);
                String thumb_webp = Constants.encryptAmazonURL(m.idUser, m.idMedia, "webp", "thumb", Constants.STATIC_URL);
                String lcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "liked");
                String vcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "vcount");
                String avatar = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + m.idUser, "avatar");
                String evnt_time = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "evnt_time");
                Boolean reflw = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + idUser, m.idUser + "");

                obj.setIdMedia(m.idMedia);
                obj.setTitle(m.title);
                obj.setDateadded(m.dateadded);
                obj.setIdChannel(m.idChannel);
                obj.setMethod(m.method);
                obj.setStart_time(evnt_time);
                obj.setIdUser(m.idUser);
                obj.setUsername(username);
                obj.setAmount(Long.valueOf(vcount==null?"0":vcount));
                obj.setLiked(Long.valueOf(lcount==null?"0":lcount));
                obj.setProgress(m.progress);
                obj.setCurrenTime(currentTime);
                obj.setRatio(m.ratio);
                obj.setThumb(thumb);
                obj.setThumb_webp(thumb_webp);
                obj.setVerified(verified == null ? false : Boolean.valueOf(verified));
                obj.setAvatar(Constants.STATIC_URL + m.idUser + "/image" + avatar + "M.jpg");
                obj.setDuration(m.duration.shortValue());
                obj.setTags(m.tags);
                obj.setLocation(m.location);
                obj.setFollowed(reflw);

                entity.add(obj);
            });

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entity;
    }

    public boolean extractUserInfoById(long idUser) {
        return false;
    }


    protected DcUsersEntity getUserInfoByEmail(String email)  {
        DcUsersEntity entity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            RiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            entity = queryFactory.queryUserDataByIDEmail(email);
            if (entity != null) {
                String avatar = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + entity.idUser, "avatar");
                String wallpic = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + entity.idUser, "wallpic");

                if (wallpic == null || wallpic.isEmpty() || wallpic.contains("empty")) {
                    wallpic = Constants.STATIC_URL + "defaults/profile-cover.jpg";
                } else {
                    wallpic = Constants.STATIC_URL + entity.idUser + "/image" + wallpic + ".jpg";
                }
                entity.avatar = Constants.STATIC_URL + entity.idUser + "/image" + avatar + ".jpg";
                entity.wallpic = wallpic;
            }

        } catch (Exception e) {
            log.error("ERROR IN RIAK DB API ", e);
        } 

        return entity;
    }

    protected DcUsersEntity getUserInfoByID(Long idUser)  {
        DcUsersEntity entity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            entity = queryFactory.queryUserDataByIDUser(idUser);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entity;
    }


    protected DcUsersEntity getUserProfileInfoById(long idUser)  {
        DcUsersEntity entity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            RiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            entity = queryFactory.queryUserDataByIDUser(idUser);

            if (entity != null ) {
                String avatar = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + idUser, "avatar");
                String wallpic = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + idUser, "wallpic");

                if(wallpic==null || wallpic.isEmpty() || wallpic.contains("empty")){
                    wallpic = Constants.STATIC_URL+"defaults/profile-cover.jpg";
                }
                else {
                    wallpic = Constants.STATIC_URL + idUser + "/image" + wallpic + ".jpg";
                }
                entity.avatar = Constants.STATIC_URL + idUser + "/image" + avatar + ".jpg";
                entity.wallpic = wallpic;
            } else {
                entity = null;
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        

        return entity;
    }


    protected Set<EmailRecipients> getUserEmail(long idUser, int type)  {
        Set<EmailRecipients> emails = new HashSet<>();
        EmailRecipients recipients = new EmailRecipients();
        try {
            String email = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "email");
            boolean isNotify = checkUserNotificationSettings(idUser,type,EnumPlatform.EMAIL);
            if(isNotify) {
                recipients.setIdUser(idUser);
                recipients.setEmail(email);
                emails.add(recipients);
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return emails;
    }

    protected DcUsersEntity getUserInfoByHash(String hash)  {
        DcUsersEntity entity = null;
        
        try {
            String idUser = (String) RMemoryAPI.getInstance().pullElemFromMemory(Constants.USER_HASH + hash, String.class);

            if (idUser != null) {
                RiakTP transport = RiakAPI.getInstance();
                RiakQueryFactory queryFactory = new RiakQueryFactory(transport);
                entity = queryFactory.queryUserDataByIDUser(Long.valueOf(idUser));

                if (entity != null) {
                    String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "avatar");
                    String wallpic = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "wallpic");
                    entity.avatar = (Constants.STATIC_URL + entity.idUser + "/image" + avatar + "S.jpg");
                    entity.wallpic = (Constants.STATIC_URL + entity.idUser + "/image" + wallpic + ".jpg");
                }
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entity;
    }


    protected List<DcCommentsEntity> getCommentsByIdMedia(long idMedia, long off, long limit)  {
        List<DcCommentsEntity> entityList = new ArrayList<>();

        try {
            List<String> list = RMemoryAPI.getInstance().pullListElemFromMemory(Constants.COMMENT_KEY + idMedia,
                    (int) (off < 0 ? 0 : off), (int) (off + ((limit <= 0 || limit > 60) ? LIMIT : limit)));

            entityList = GsonInsta.getInstance().fromJson(list.toString(),
                    new TypeToken<List<DcCommentsEntity>>() {
                    }.getType());
            String idOwner = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY+idMedia, "id_user");

            for (DcCommentsEntity entity : entityList) {
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.getIdUser(), "avatar");
                String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.getIdUser(), "username");

                boolean vc = RMemoryAPI.getInstance().pullIfSetElem("dc_users:comment:voice:users:"+idOwner, entity.getIdUser() + "");

                entity.setUsername(username);
                entity.setvPermit(vc?1:0);
                entity.setAvatar(Constants.STATIC_URL + entity.getIdUser() + "/image" + avatar + ".jpg");
                if(!entity.getUrl().isEmpty()) {
                    String url = Constants.STATIC_URL + entity.getUrl();
                    entity.setUrl(url);
                }
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }


        return entityList;
    }


    protected List<MediaNewsStatInfo> castMediaInArchive(long idUser, long offset, long limit, String lang)  {

        List<MediaNewsStatInfo> entity = new ArrayList<>();
        offset = (int) (offset < 0 ? 0 : offset);
        limit = (int) (offset + ((limit <= 0 || limit > 60) ? LIMIT : limit));

        try {

            List<String> res = RMemoryAPI.getInstance().pullSortedListElem(Constants.ARCHIVE + idUser,
                    Constants.MEDIA_KEY + "*->detail", 0, -1, Constants.MEDIA_KEY + "*->id_media");

            List<DcMediaEntity> list = GsonInsta.getInstance().fromJson(res.toString(),
                    new TypeToken<List<DcMediaEntity>>() {
                    }.getType());

            String currentTime = String.valueOf(RMemoryAPI.getInstance().currentTimeMillis());
            for (DcMediaEntity media : list) {
                if (media != null  && media.getProgress() == 0) {
                    String username = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + idUser, "username");
                    //media.setUsername(username);

                    String verified = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + idUser, "verified");
                    //media.setVerified(verified==null?false:Boolean.valueOf(verified));

                    String thumb = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "jpg", "thumb", Constants.STATIC_URL);
                    String thumb_webp = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "webp", "thumb", Constants.STATIC_URL);
                    media.setThumb(thumb);
                    media.setThumb_webp(thumb_webp);

                    String avatar = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "avatar");
                    //media.setAvatar(avatar);

                    String ratio = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + media.getIdMedia(), "ratio");
                    media.setRatio(ratio);

                    String location = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.MEDIA_KEY + media.getIdMedia(), "location", lang);
                    media.setLocation(location);


                    //entity.add(new MediaNewsStatInfo(media)); //todo
                }
            }

            int start = entity.size() <= (int) offset ? entity.size() : (int) offset;
            int stop = entity.size() <= (int) limit ? entity.size() : (int) limit;

            entity = entity.subList(start, stop);


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entity;

    }



    protected List<DcDebateEntity> castMediaInDebate(long idMedia) {

        List<DcDebateEntity> entity = new ArrayList<>();

        try {
            List<String> res = RMemoryAPI.getInstance()
                    .pullListElemFromMemory(Constants.MEDIA_KEY + "debate:" + idMedia, 0, -1);

            List<DcDebateEntity> list = GsonInsta.getInstance().fromJson(res.toString(),
                    new TypeToken<List<DcDebateEntity>>() {
                    }.getType());

            for (DcDebateEntity media : list) {
                String duration = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + media.getIdMedia(), "duration");
                if(duration==null){
                    duration = "0";
                }
                    String mp4 = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "_hi.mp4", "v", Constants.VIDEO_URL);
                    String hls = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "", "hls", Constants.VIDEO_URL);
                    String rtmp_url = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.LIVE_KEY + media.getIdMedia(), "rtmp_liveurl");

                    String avatar = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "avatar");
                    String username = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "username");

                    List<String> entities =
                            RMemoryAPI.getInstance()
                                    .pullListElemFromMemory(Constants.MEDIA_KEY + "properties:rotatime:" + media.getIdMedia(), 0, -1);
                    List<ScreenRotation> lrotation = new Gson().fromJson(entities.toString(),
                            new TypeToken<List<ScreenRotation>>() {
                            }.getType());

                    media.setUsername(username);
                    media.setDuration(duration);
                    media.setAvatar(Constants.STATIC_URL + media.getIdUser() + "/image" + avatar + ".jpg");
                    media.setHls_url(hls);
                    media.setMp4_url(mp4);
                    media.setRtmp_url(rtmp_url);
                    media.setRotations(lrotation);
                    entity.add(media);

            }


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entity;

    }

    protected List<DcLocationsEntity> extractAllCountries() throws SQLException {

        List<DcLocationsEntity> countries = new ArrayList<>();

        Connection dbConnection = null;
        try {

            dbConnection = JDBCUtil.getDBConnection();
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            try {
                preparedStatement = dbConnection.prepareStatement(
                        "SELECT code, name FROM countries ORDER BY code ");
                rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    DcLocationsEntity entity = new DcLocationsEntity();
                    entity.setCode(rs.getString("code"));
                    entity.setCountry(rs.getString("name"));

                    countries.add(entity);
                }

            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } finally {
            if (dbConnection != null) {
                dbConnection.commit();
                dbConnection.close();
            }
        }

        return countries;

    }

    protected String extractCountriesByCode(String code) throws SQLException {

        String country = "";

        Connection dbConnection = null;
        try {

            dbConnection = JDBCUtil.getDBConnection();
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            try {
                preparedStatement = dbConnection.prepareStatement(
                        "SELECT name FROM countries WHERE code = ? LIMIT 1");
                rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    country = rs.getString("name");
                }

            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } finally {
            if (dbConnection != null) {
                dbConnection.commit();
                dbConnection.close();
            }
        }

        return country;

    }


    protected long extractUniqueIdForLive()  {

        long id_live = System.currentTimeMillis();
        return id_live;

    }


    protected long extractUniqueIdForUser()  {
        long id_user = System.currentTimeMillis();
        return id_user;

    }


    protected long extractUniqueIdForMedia()  {
        long id_media = System.currentTimeMillis();
        return id_media;

    }

    protected short extractVideoRateByUserAVideo(long idUser, long idMedia)  {
        Short activity = 0;

        try {

            String ld = (String) RMemoryAPI.getInstance().pullElemFromMemory(Constants.ACTIVITY + idMedia + ":" + idUser, String.class);
            if (ld != null && !ld.isEmpty()) {
                activity = Short.valueOf(ld);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return activity;
    }

    protected DcUsersEntity getUserInfoByFB(String idFB)  {

        DcUsersEntity entity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            RiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            entity = queryFactory.queryUserDataByIDSocial(idFB, "FACEBOOK");

            if(entity!=null){
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "avatar");
                String wallpic = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "wallpic");

                if(wallpic==null || wallpic.isEmpty() || wallpic.contains("empty")){
                    wallpic = Constants.STATIC_URL+"defaults/profile-cover.jpg";
                }
                else {
                    wallpic = Constants.STATIC_URL + entity.idUser + "/image" + wallpic + ".jpg";
                }
                entity.avatar = Constants.STATIC_URL + entity.idUser + "/image" + avatar + ".jpg";
                entity.wallpic = wallpic;
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entity;
    }

    protected DcUsersEntity getUserInfoByVKID(String idVk)  {
        DcUsersEntity entity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            RiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            entity = queryFactory.queryUserDataByIDSocial(idVk, "VK");

            if(entity!=null){
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "avatar");
                String wallpic = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "wallpic");

                if(wallpic==null || wallpic.isEmpty() || wallpic.contains("empty")){
                    wallpic = Constants.STATIC_URL+"defaults/profile-cover.jpg";
                }
                else {
                    wallpic = Constants.STATIC_URL + entity.idUser + "/image" + wallpic + ".jpg";
                }
                entity.avatar = Constants.STATIC_URL + entity.idUser + "/image" + avatar + ".jpg";
                entity.wallpic = wallpic;
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entity;
    }

    protected DcUsersEntity getUserInfoByTwitterID(String idTw)  {
        DcUsersEntity entity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            RiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            entity = queryFactory.queryUserDataByIDSocial(idTw, "TWITTER");

            if(entity!=null){
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "avatar");
                String wallpic = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "wallpic");

                if(wallpic==null || wallpic.isEmpty() || wallpic.contains("empty")){
                    wallpic = Constants.STATIC_URL+"defaults/profile-cover.jpg";
                }
                else {
                    wallpic = Constants.STATIC_URL + entity.idUser + "/image" + wallpic + ".jpg";
                }
                entity.avatar = Constants.STATIC_URL + entity.idUser + "/image" + avatar + ".jpg";
                entity.wallpic = wallpic;
            }
        } catch (Exception e) {
            log.error("ERROR IN RIAK DB API ", e);
        } 

        return entity;
    }

    protected DcNotificationTypes getUserNotificationSettings(long idUser)  {
        DcNotificationTypes entity = null;
        try {
            entity = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + idUser, "n_settings", DcNotificationTypes.class);
            if (entity != null) {
                log.debug("USER SETTING: {}", entity.toString());
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        return entity;
    }

    private boolean checkUserNotificationSettings(long idUser, int noteType, EnumPlatform platform)  {

        return GenSettings.gettUserSettingValue(idUser,noteType,platform);
    }


    protected DcUsersEntity getUserInfoByFbID(String email, String idFb)  {
        DcUsersEntity entity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            RiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            entity = queryFactory.queryUserDataByIDSocial(idFb, "FACEBOOK");
            if(entity==null){
                entity = queryFactory.queryUserDataByIDEmail(email);
                if(entity!=null){
                    RiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                    entity.idFBSocial = idFb;
                    saveFactory.updUserData(entity);
                }
            }

            if(entity!=null){
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "avatar");
                String wallpic = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "wallpic");

                if(wallpic==null || wallpic.isEmpty() || wallpic.contains("empty")){
                    wallpic = Constants.STATIC_URL+"defaults/profile-cover.jpg";
                }
                else {
                    wallpic = Constants.STATIC_URL + entity.idUser + "/image" + wallpic + ".jpg";
                }
                entity.avatar = Constants.STATIC_URL + entity.idUser + "/image" + avatar + ".jpg";
                entity.wallpic = wallpic;
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entity;
    }

    protected DcUsersEntity getUserInfoByGooID(String email, String idGoo)  {
        DcUsersEntity entity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            RiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            entity = queryFactory.queryUserDataByIDSocial(idGoo, "GOOGLE");
            if(entity==null){
                entity = queryFactory.queryUserDataByIDEmail(email);
                if(entity!=null){
                    RiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                    entity.idGSocial = idGoo;
                    saveFactory.updUserData(entity);
                }
            }

            if(entity!=null){
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "avatar");
                String wallpic = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "wallpic");

                if(wallpic==null || wallpic.isEmpty() || wallpic.contains("empty")){
                    wallpic = Constants.STATIC_URL+"defaults/profile-cover.jpg";
                }
                else {
                    wallpic = Constants.STATIC_URL + entity.idUser + "/image" + wallpic + ".jpg";
                }
                entity.avatar = Constants.STATIC_URL + entity.idUser + "/image" + avatar + ".jpg";
                entity.wallpic = wallpic;
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entity;

    }


    protected short extractVideoReportByUserAct(long idUser, long idMedia)  {
        Short activity = 0;
        try {
            String ld = (String) RMemoryAPI.getInstance()
                    .pullElemFromMemory(Constants.ACTIVITY + "report:" + idMedia + ":" + idUser, String.class);
            if (ld != null && !ld.isEmpty()) {
                activity = Short.valueOf(ld);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return activity;
    }


    protected long extractVideoIsFollowed(long source, long dest)  {
        long activity = 0;

        try {
            Boolean member = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + source, dest + "");
            if (member) {
                activity = 1;
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return activity;
    }

    @SuppressWarnings("unchecked")
    protected List<UserShortInfo> extractPublicUserFollowers(long idUser, long idSource, long off, long limit, String lang)  {
        List<UserShortInfo> entityList = new ArrayList<>();

        UserShortInfo followsEntity = null;
        try {


            List<String> list = RMemoryAPI.getInstance().pullSortedListElem(Constants.FOLLOWERS + idUser,
                    Constants.USER_KEY + "*->detail", (int) off, (int) ((limit <= 0 || limit > 60) ? LIMIT : limit), Constants.USER_KEY + "*->id_user");
            List<DcUsersEntity> uList = GsonInsta.getInstance().fromJson(list.toString(),
                    new TypeToken<List<DcUsersEntity>>() {
                    }.getType());

            for (DcUsersEntity rs : uList) {
                followsEntity = new UserShortInfo();
                followsEntity.setIdUser(rs.idUser);
                followsEntity.setUsername(rs.username);
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + rs.idUser, "avatar");
                followsEntity.setAvatar(Constants.STATIC_URL + rs.idUser + "/image" + avatar + "S.jpg");
                Boolean follow = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + idSource, rs.idUser + "");
                followsEntity.setReFollow(follow);

                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + rs.idUser);
                followsEntity.setFlwnum(flwcount);

                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + rs.idUser, "location:" + lang);
                followsEntity.setLocation(local);

                entityList.add(followsEntity);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);

        }

        return entityList;
    }

    protected List<UserShortInfo> extractPublicIntersectUserFollowers(long idUser, long off, long limit, String lang)  {
        List<UserShortInfo> entityList = new ArrayList<>();

        UserShortInfo followsEntity = null;
        try {

            String tmpKey = RMemoryAPI.getInstance()
                    .pullInterStoreSetFromToMemory(Constants.FOLLOWERS + idUser, Constants.FOLLOWS + idUser);

            List<String> list = RMemoryAPI.getInstance().pullSortedListElem(tmpKey,
                    Constants.USER_KEY + "*->detail", (int) off, (int) ((limit <= 0 || limit > 60) ? LIMIT : limit), Constants.USER_KEY + "*->id_user");
            List<DcUsersEntity> uList = GsonInsta.getInstance().fromJson(list.toString(),
                    new TypeToken<List<DcUsersEntity>>() {
                    }.getType());

            for (DcUsersEntity rs : uList) {
                followsEntity = new UserShortInfo();
                followsEntity.setIdUser(rs.idUser);
                followsEntity.setUsername(rs.username);
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + rs.idUser, "avatar");
                followsEntity.setAvatar(Constants.STATIC_URL + rs.idUser + "/image" + avatar + "S.jpg");
                followsEntity.setReFollow(true);
                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + rs.idUser);
                followsEntity.setFlwnum(flwcount);

                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + rs.idUser, "location:" + lang);
                followsEntity.setLocation(local);

                entityList.add(followsEntity);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }

    protected List<UserShortInfo> extractPublicViewersList(long idMedia, long off, long limit) {
        List<UserShortInfo> entityList = new ArrayList<>();

        UserShortInfo viewersEntity = null;
        try {

            List<String> list = RMemoryAPI.getInstance().pullSortedListElem(Constants.MEDIA_KEY + "viewers:" + idMedia,
                    Constants.USER_KEY + "*->detail", (int) off, (int) ((limit <= 0 || limit > 60) ? LIMIT : limit), Constants.USER_KEY + "*->id_user");
            List<DcUsersEntity> uList = GsonInsta.getInstance().fromJson(list.toString(),
                    new TypeToken<List<DcUsersEntity>>() {
                    }.getType());

            for (DcUsersEntity rs : uList) {
                viewersEntity = new UserShortInfo();
                viewersEntity.setIdUser(rs.idUser);
                viewersEntity.setUsername(rs.username);
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + rs.idUser, "avatar");
                viewersEntity.setAvatar(Constants.STATIC_URL + rs.idUser + "/image" + avatar + "S.jpg");
                viewersEntity.setReFollow(true);
                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + rs.idUser);
                viewersEntity.setFlwnum(flwcount);

                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + rs.idUser, "location:en");
                viewersEntity.setLocation(local);

                entityList.add(viewersEntity);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }

    protected List<UserShortInfo> searchMyUserFollowersByName(String name, Long idUser, int off, int limit) {
        List<UserShortInfo> entityList = new ArrayList<>();

        UserShortInfo followsEntity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            Set<String> list = queryFactory.queryUserDataByName(name,"followers", idUser, off, limit);
            String lang = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+idUser, "language");
            lang = lang==null?"en":lang;

            for (String idu : list) {
                DcUsersEntity rs = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY+idu, "detail", DcUsersEntity.class);

                followsEntity = new UserShortInfo();
                followsEntity.setIdUser(rs.idUser);
                followsEntity.setUsername(rs.username);
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + rs.idUser, "avatar");
                followsEntity.setAvatar(Constants.STATIC_URL + rs.idUser + "/image" + avatar + "S.jpg");
                Boolean follow = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + idUser, rs.idUser + "");
                followsEntity.setReFollow(follow);
                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + rs.idUser);
                followsEntity.setFlwnum(flwcount);

                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + rs.idUser, "location:" + lang);
                followsEntity.setLocation(local);

                entityList.add(followsEntity);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entityList;
    }

    protected List<UserShortInfo> searchMyUserFollowsByName(String name, Long idUser, int off, int limit) {
        List<UserShortInfo> entityList = new ArrayList<>();

        UserShortInfo followsEntity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            Set<String> list = queryFactory.queryUserDataByName(name,"follows", idUser, off, limit);
            log.debug("All users data {}", list);
            String lang = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+idUser, "language");
            lang = lang==null?"en":lang;

            for (String idu : list) {
                DcUsersEntity rs = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY+idu, "detail", DcUsersEntity.class);

                followsEntity = new UserShortInfo();
                followsEntity.setIdUser(rs.idUser);
                followsEntity.setUsername(rs.username);
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + rs.idUser, "avatar");
                followsEntity.setAvatar(Constants.STATIC_URL + rs.idUser + "/image" + avatar + "S.jpg");
                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + rs.idUser);
                followsEntity.setFlwnum(flwcount);
                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + rs.idUser, "location:" + lang);
                followsEntity.setLocation(local);


                entityList.add(followsEntity);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entityList;
    }

    protected List<UserShortInfo> searchUserFollowsByName(String name, Long source, Long dest, String direct, int off, int limit) {
        List<UserShortInfo> entityList = new ArrayList<>();

        UserShortInfo followsEntity = null;

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            Set<String> list = queryFactory.queryUserDataByName(name,direct, dest, off, limit);
            String lang = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+dest, "language");
            lang = lang==null?"en":lang;

            for (String idu : list) {
                DcUsersEntity rs = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY+idu, "detail", DcUsersEntity.class);

                followsEntity = new UserShortInfo();
                followsEntity.setIdUser(rs.idUser);
                followsEntity.setUsername(rs.username);
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + rs.idUser, "avatar");
                followsEntity.setAvatar(Constants.STATIC_URL + rs.idUser + "/image" + avatar + "S.jpg");
                boolean reFollows = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS+source, dest+"");
                followsEntity.setReFollow(reFollows);
                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + rs.idUser);
                followsEntity.setFlwnum(flwcount);
                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + rs.idUser, "location:" + lang);
                followsEntity.setLocation(local);


                entityList.add(followsEntity);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }

    protected List<UserShortInfo> searchMyUserInterFollowsByName(String name, Long idUser, int off, int limit) {
        List<UserShortInfo> entityList = new ArrayList<>();

        UserShortInfo followsEntity = null;

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            Set<String> list = queryFactory.queryUserDataByName(name,"friends", idUser, off, limit);
            String lang = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+idUser, "language");
            lang = lang==null?"en":lang;

            for (String idu : list) {
                DcUsersEntity rs = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY+idu, "detail", DcUsersEntity.class);

                followsEntity = new UserShortInfo();
                followsEntity.setIdUser(rs.idUser);
                followsEntity.setUsername(rs.username);
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + rs.idUser, "avatar");
                followsEntity.setAvatar(Constants.STATIC_URL + rs.idUser + "/image" + avatar + "S.jpg");
                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + rs.idUser);
                followsEntity.setFlwnum(flwcount);
                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + rs.idUser, "location:" + lang);
                followsEntity.setLocation(local);


                entityList.add(followsEntity);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }

    protected List<UserShortInfo> searchAllUsersByName(String name, Long idUser, int off, int limit) {
        List<UserShortInfo> entityList = new ArrayList<>();

        UserShortInfo followsEntity = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            Set<String> list = queryFactory.queryUserDataByName(name,"all", null, off, limit);

            String lang = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+idUser, "language");
            lang = lang==null?"en":lang;

            for (String idu : list) {
                DcUsersEntity rs = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY+idu, "detail", DcUsersEntity.class);

                followsEntity = new UserShortInfo();
                followsEntity.setIdUser(rs.idUser);
                followsEntity.setUsername(rs.username);
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + rs.idUser, "avatar");
                followsEntity.setAvatar(Constants.STATIC_URL + rs.idUser + "/image" + avatar + "S.jpg");
                Boolean follow = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + idUser, rs.idUser + "");
                followsEntity.setReFollow(follow);
                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + rs.idUser);
                followsEntity.setFlwnum(flwcount);
                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + rs.idUser, "location:" + lang);
                followsEntity.setLocation(local);

                entityList.add(followsEntity);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } 

        return entityList;
    }

    protected Long getUserVideosCount(long idUser, boolean mine)  {
        Long count = 0L;

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            count = queryFactory.queryUsersVideoCountByIDUser(idUser);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return count;
    }

    protected Long getUserChannelsCount(long idUser, boolean mine)  {
        Long count = 0L;

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            count = queryFactory.queryUsersChannelCountByIDUser(idUser);
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return count;
    }

    protected long validateKeyPreUpdate(String key)  {
        long val = 0;

        try {
            String live = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + key, "id_media");

            if (live != null && !live.isEmpty()) {
                val = Long.valueOf(live);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return val;
    }

    @SuppressWarnings("unchecked")
    protected List<UserShortInfo> extractPublicUserFollowing(long idUser, long idSource, long off, long limit, String lang)  {
        List<UserShortInfo> entityList = new ArrayList<>();

        UserShortInfo followsEntity = null;
        try {

            List<String> list = RMemoryAPI.getInstance().pullSortedListElem(Constants.FOLLOWS + idUser,
                    Constants.USER_KEY + "*->detail", (int) off, (int) ((limit <= 0 || limit > 60) ? LIMIT : limit), Constants.USER_KEY + "*->id_user");
            List<DcUsersEntity> uList = GsonInsta.getInstance().fromJson(list.toString(),
                    new TypeToken<List<DcUsersEntity>>() {
                    }.getType());

            for (DcUsersEntity rs : uList) {
                followsEntity = new UserShortInfo();
                followsEntity.setIdUser(rs.idUser);
                followsEntity.setUsername(rs.username);
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + rs.idUser, "avatar");
                followsEntity.setAvatar(Constants.STATIC_URL + rs.idUser + "/image" + avatar + "S.jpg");
                Boolean follow = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + idSource, rs.idUser + "");
                followsEntity.setReFollow(follow);

                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + rs.idUser);
                followsEntity.setFlwnum(flwcount);

                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + rs.idUser, "location:" + lang);
                followsEntity.setLocation(local);

                entityList.add(followsEntity);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }

    protected List<MediaNewsStatInfo> castMediaByLive(long off, long limit)  {

        List<MediaNewsStatInfo> entity = new ArrayList<>();

        try {
            List<String> res = RMemoryAPI.getInstance().pullSortedListElem(Constants.LIVES,
                    Constants.MEDIA_KEY + "*->detail", (int) off, (int) (off + ((limit <= 0 || limit > 60) ? LIMIT : limit)),
                    Constants.MEDIA_KEY + "*->id_media");

            List<DcMediaEntity> objects = GsonInsta.getInstance().fromJson(res.toString(),
                    new TypeToken<List<DcMediaEntity>>() {
                    }.getType());

            String currentTime = String.valueOf(RMemoryAPI.getInstance().currentTimeMillis());
            for (DcMediaEntity media : objects) {
                String username = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "username");
               // media.setUsername(username);

                String verified = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "verified");
               // media.setVerified(verified==null?false:Boolean.valueOf(verified));

                String thumb = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "jpg", "thumb", Constants.STATIC_URL);
                String thumb_webp = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "webp", "thumb", Constants.STATIC_URL);
                media.setThumb(thumb);
                media.setThumb_webp(thumb_webp);

                String avatar = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "avatar");
              //  media.setAvatar(Constants.STATIC_URL + media.getIdUser() + "/image" + avatar + "M.jpg");

                String ratio = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + media.getIdMedia(), "ratio");
                media.setRatio(ratio);

                String location = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + media.getIdMedia(), "location");
                media.setLocation(location);


                //entity.add(new MediaNewsStatInfo(media));
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entity;
    }


    @Deprecated
    protected DcMediaEntity castSimpleMediaById(long idMedia)  {

        DcMediaEntity infoMem = new DcMediaEntity();

        try {

            infoMem = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "detail", DcMediaEntity.class);
            String idu = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "id_user");
            if (infoMem != null) {
                String thumb = Constants.encryptAmazonURL(Long.valueOf(idu), idMedia, "jpg", "thumb", Constants.STATIC_URL);
                String thumb_webp = Constants.encryptAmazonURL(Long.valueOf(idu), idMedia, "webp", "thumb", Constants.STATIC_URL);
                infoMem.setThumb(thumb);
                infoMem.setThumb_webp(thumb_webp);

                String ratio = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + idMedia, "ratio");
                infoMem.setRatio(ratio);
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return infoMem;
    }

    protected List<DcUsersEntity> extractChannelsUsers(long idChannel, long offset, long limit, String lang)  {
        List<DcUsersEntity> entityList = new ArrayList<>();

        try {
            List<String> res = RMemoryAPI.getInstance().pullSortedListElem(Constants.CHANNEL_KEY + "users:" + idChannel,
                    Constants.USER_KEY + "*->detail", (int) offset, (int) (offset + ((limit <= 0 || limit > 60) ? LIMIT : limit)),
                    Constants.USER_KEY + "*->id_user");
            Gson gson = new Gson();

            List<DcUsersEntity> list = gson.fromJson(res.toString(),
                    new TypeToken<List<DcUsersEntity>>() {
                    }.getType());

            for (DcUsersEntity entity : list) {
                DcUsersEntity object = new DcUsersEntity();
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "avatar");
                object.avatar = (Constants.STATIC_URL + entity.idUser + "/image" + avatar + ".jpg");
                object.username = (entity.username);
                object.fullname = (entity.fullname);
                object.idUser = (entity.idUser);
                entityList.add(object);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }

    protected Deque<UserShortInfo> extractChannelsUsers(long idChannel, long idUser, long offset, long limit, String lang)  {
        Deque<UserShortInfo> entityList = new LinkedList<>();

        try {
            List<String> res = RMemoryAPI.getInstance().pullSortedListElem(Constants.CHANNEL_KEY + "users:" + idChannel,
                    Constants.USER_KEY + "*->detail", (int) offset, (int) (offset + ((limit <= 0 || limit > 60) ? LIMIT : limit)),
                    Constants.USER_KEY + "*->id_user");
            Gson gson = new Gson();

            List<DcUsersEntity> list = gson.fromJson(res.toString(),
                    new TypeToken<List<DcUsersEntity>>() {
                    }.getType());

            for (DcUsersEntity entity : list) {
                UserShortInfo object = new UserShortInfo();

                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + entity.idUser, "avatar");
                object.avatar = (Constants.STATIC_URL + entity.idUser + "/image" + avatar + ".jpg");
                object.username = (entity.username);
                object.fullname = (entity.fullname);
                object.idUser = (entity.idUser);
                object.reFollow = (RMemoryAPI.getInstance()
                        .pullIfSetElem(Constants.FOLLOWS + idUser, entity.idUser + ""));

                object.location = (RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + entity.idUser, "location:" + lang));

                entityList.addLast(object);

            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }

    protected String checkMediaCompressStatus(long idMedia)  {

        String res = null;
        MediaStatus status = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY+idMedia, "progress", MediaStatus.class);

        if(status==null || status.getStatus()==null || status.getStatus().isEmpty()){
            res = "NOT FOUND";
        }
        else {
            res = status.getStatus();
        }

        return res;
    }


    protected long getLiveViewCount(long idLive)  {

        Long value = 0L;
        try {
            String vcnt = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + idLive, "vcount");
            if (vcnt != null) {
                value = Long.valueOf(vcnt);

            }


        } catch (Exception e) {
            log.error("ERROR IN GETTING VIEW COUNT ", e);
        }

        return value;
    }
}
