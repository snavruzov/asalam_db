package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.DcCommentsEntity;
import com.brocast.riak.api.beans.DcMediaEntity;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakQueryFactory;
import com.brocast.riak.api.factory.RiakQueryFactory;
import com.dgtz.db.api.beans.MediaMappingStatInfo;
import com.dgtz.db.api.domain.MediaNewsStatInfo;
import com.dgtz.db.api.enums.EnumAggregations;
import com.dgtz.mcache.api.dao.RedisAPI;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.dgtz.mcache.api.utils.GsonInsta;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.sql.SQLException;
import java.util.*;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 6/22/14
 */
public abstract class RedisQueryTemplate {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RedisQueryTemplate.class);
    private static final long LIMIT = 12;

    public RedisQueryTemplate() {
    }

    protected List<MediaNewsStatInfo> castMediaByIdCategory(int idCategory, int sortType, long off, long limit, String lang)
    {

        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);

        List<MediaNewsStatInfo> entity = new ArrayList<>();

        String order = "liked";
        /*0 - BY LIKED, 1 - BY VIEWED, 2 - BY COMMENTED, 3 - BY DATE*/
        if (sortType == 1) order = "vcount";
        if (sortType == 2) order = "ccount";
        if (sortType == 3) order = "id_media";

        try {

            String categories =
                    (String) RMemoryAPI.getInstance()
                            .pullElemFromMemory("cid:" + idCategory, String.class);

            List<String> res = RMemoryAPI.getInstance().pullSortedListElem(Constants.CETEGORIES + categories,
                    "dc_media:*->detail", (int) off, (int) limit, "dc_media:*->" + order);

            Gson gson = new Gson();
            List<DcMediaEntity> list = gson.fromJson(res.toString(),
                    new TypeToken<List<DcMediaEntity>>() {
                    }.getType());

            String currentTime = String.valueOf(RMemoryAPI.getInstance().currentTimeMillis());
            for (DcMediaEntity media : list) {
                if (media != null  /*&& media.getProps() == 0*/ && media.getProgress() == 0) {
                    String username = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "username");
                    //media.setUsername(username);

                    String verified = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "verified");
                   // media.setVerified(verified==null?false:Boolean.valueOf(verified));

                    String thumb = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "jpg", "thumb", Constants.STATIC_URL);
                    String thumb_webp = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "webp", "thumb", Constants.STATIC_URL);
                    media.setThumb(thumb);
                    media.setThumb_webp(thumb_webp);

                    String avatar = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "avatar");
                   // media.setAvatar(Constants.STATIC_URL + media.getIdUser() + "/image" + avatar + "M.jpg");

                    String ratio = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + media.getIdMedia(), "ratio");
                    media.setRatio(ratio);

                    String location = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.MEDIA_KEY + media.getIdMedia(), "location", lang);
                    media.setLocation(location);

                   // entity.add(new MediaNewsStatInfo(media));

                }
            }


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);

        }

        return entity;
    }

    protected Map<Long, List<DcCommentsEntity>> castCommentsByIdLive(long idMedia, int duration, long off, long limit, boolean reverse, boolean sort) {
        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);
        Map<Long, List<DcCommentsEntity>> entity = new LinkedHashMap<>();

        try {

            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcCommentsEntity> clist = queryFactory.queryMediaComments(duration, idMedia, (int)off, (int)limit, reverse, sort);
            clist.forEach(elem -> {
                Long time = elem.getTkey();
                List<DcCommentsEntity> types = entity.get(time);
                String username = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + elem.idUser, "username");
                elem.setUsername(username);
                elem.setUrl(elem.url.isEmpty()?"":Constants.STATIC_URL+elem.url);
                if (types == null) {
                    types = new ArrayList<>();
                    types.add(elem);
                    entity.put(time, types);
                } else {
                    types.add(elem);
                    entity.put(time, types);
                }
            });


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entity;
    }

    protected Map<Long, List<String>> castRatingsByLive(String idLive) {

        final Map<Long, List<String>> entity = new HashMap<>();

        if(idLive!=null && !idLive.isEmpty()) {

            try {

                Set<Tuple> res = pullSortedSetFromRedis(Constants.MEDIA_KEY + "rating:" + idLive, 0, -1);
                res.forEach(tpl -> {
                    Long time = (long) tpl.getScore();
                    List<String> types = entity.get(time);
                    String tp = tpl.getElement().split("_")[0];
                    if(types==null) {
                        types = new ArrayList<>();
                        types.add(tp);
                        entity.put(time, types);
                    } else {
                        types.add(tp);
                        entity.put(time, types);
                    }

                });

            } catch (Exception e) {
                log.error("ERROR IN DB API ", e);
            }
        }

        return entity;
    }

    private Set<Tuple> pullSortedSetFromRedis(String key, int start, int count){
        Jedis jedis = RedisAPI.getInstance();
        Set<Tuple> list = null;

        try {
            list = jedis.zrangeWithScores(key, start, count);
        } catch (JedisConnectionException var10) {
            if(null != jedis) {
                RedisAPI.putBrokenBack(jedis);
            }
        } finally {
            if(null != jedis) {
                RedisAPI.putBack(jedis);
            }

        }

        return list;
    }

    private Set<Tuple> pullSortedCommentSetFromRedis(String key, int duration,  int start, int count){
        Jedis jedis = RedisAPI.getInstance();
        Set<Tuple> list = null;

        try {
            list = jedis.zrevrangeByScoreWithScores(key, duration+"", "0", start, count);
        } catch (JedisConnectionException var10) {
            if(null != jedis) {
                RedisAPI.putBrokenBack(jedis);
            }
        } finally {
            if(null != jedis) {
                RedisAPI.putBack(jedis);
            }

        }

        return list;
    }

    protected List<MediaNewsStatInfo> castMediaByStats(EnumAggregations enumAgr, String user_lang, Set<String> langs,
                                                       long off, long limit) {

        List<MediaNewsStatInfo> entity = new ArrayList<>();

        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);

        String order = "id_media";

        if (enumAgr == EnumAggregations.MOST_VIEWED) {
            order = "vcount";
        } else if (enumAgr == EnumAggregations.MOST_COMMENTED) {
            order = "ccount";
        } else if (enumAgr == EnumAggregations.MOST_LIKED) {
            order = "liked";
        } else if (enumAgr == EnumAggregations.RECOMMENDED) {
            order = "props";
        }

        try {

            List<String> res = null;


            if (enumAgr == EnumAggregations.RECOMMENDED) {
                res = RMemoryAPI.getInstance().pullSortedListElem(Constants.MEDIA_KEY + "recommended"
                        , "dc_media:*->detail", (int) off, (int) limit, "dc_media:*->rating");
            } else {
                String tmp_store = Constants.MEDIA_KEY + "tmp_store"+RMemoryAPI.getInstance().currentTimeMillis();

                log.debug("Langs IDs {}", langs.toString());
                RMemoryAPI.getInstance().pushUnionSetElemToMemory(tmp_store, langs.toArray(new String[langs.size()]));

                res = RMemoryAPI.getInstance().pullSortedListElem(tmp_store
                        , "dc_media:*->detail", (int) off, (int) limit, "dc_media:*->" + order);
            }

            Gson gson = new Gson();
            List<DcMediaEntity> list = gson.fromJson(res.toString(),
                    new TypeToken<List<DcMediaEntity>>() {
                    }.getType());

            String currentTime = String.valueOf(RMemoryAPI.getInstance().currentTimeMillis());
            for (DcMediaEntity media : list) {
                if (media != null /*&& media.getProps() == 0*/ && media.getProgress() == 0) {
                    String username = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "username");
                   // media.setUsername(username);

                    String verified = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "verified");
                 //   media.setVerified(verified==null?false:Boolean.valueOf(verified));

                    String thumb = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "jpg", "thumb", Constants.STATIC_URL);
                    String thumb_webp = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "webp", "thumb", Constants.STATIC_URL);
                    media.setThumb(thumb);
                    media.setThumb_webp(thumb_webp);

                    String avatar = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "avatar");
                  //  media.setAvatar(Constants.STATIC_URL + media.getIdUser() + "/image" + avatar + "M.jpg");

                    String ratio = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + media.getIdMedia(), "ratio");
                    media.setRatio(ratio);

                    String location = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.MEDIA_KEY + media.getIdMedia(), "location", user_lang);
                    media.setLocation(location);

                  //  entity.add(new MediaNewsStatInfo(media));

                }
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);

        }

        return entity;
    }

    protected List<MediaMappingStatInfo> castMediaByLatLng() {

        List<MediaMappingStatInfo> entity = new ArrayList<>();


        try {
            List<DcMediaEntity> list;
            String mlist = RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY+"maplist");

            if(mlist==null) {
                RiakTP transport = RiakAPI.getInstance();
                IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
                list = queryFactory.queryMediaByMAP(0, 50);
            } else {
                list = new Gson().fromJson(mlist,
                        new TypeToken<List<DcMediaEntity>>() {
                        }.getType());
            }

            String currentTime = String.valueOf(RMemoryAPI.getInstance().currentTimeMillis());
            list.forEach(media -> {
                MediaMappingStatInfo info = new MediaMappingStatInfo();
                String username = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "username");
                String thumb = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "jpg", "thumb", Constants.STATIC_URL);
                String thumb_webp = Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "webp", "thumb", Constants.STATIC_URL);
                String avatar = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + media.getIdUser(), "avatar");
                String vcnt = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + media.idMedia, "vcount");
                String lcnt = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + media.idMedia, "liked");
                String evnt_time = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + media.idMedia, "evnt_time");

                info.setUsername(username);
                info.setThumb(thumb);
                info.setThumb_webp(thumb_webp);
                info.setAvatar(Constants.STATIC_URL + media.getIdUser() + "/image" + avatar + "M.jpg");
                info.setLocation(media.location);
                info.setIdUser(media.idUser);
                info.setDateadded(media.dateadded);
                info.setTitle(media.title);
                info.setDuration(media.duration);
                info.setCurrentTime(currentTime);
                info.setRatio(media.ratio);
                info.setAmount(vcnt == null ? 0 : Long.valueOf(vcnt));
                info.setLiked(lcnt == null ? 0 : Long.valueOf(lcnt));
                info.setMethod(media.method);
                info.setLatLng(media.coordinate);
                info.setIdMedia(media.idMedia);
                info.setStart_time(evnt_time);

                if (!media.method.equals("live")) {
                    info.setVideo_url(Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "_hi.mp4", "v", Constants.VIDEO_URL));
                    info.setVideo_hls(Constants.encryptAmazonURL(media.getIdUser(), media.getIdMedia(), "", "hls", Constants.VIDEO_URL));
                } else if (media.method.equals("live")) {
                    info.setVideo_hls(media.getLiveProps().hls_url);
                    info.setVideo_url(RMemoryAPI.getInstance().pullHashFromMemory(Constants.LIVE_KEY + media.getIdMedia(), "rtmp_liveurl"));

                }

                entity.add(info);

            });
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);

        }

        return entity;
    }

    protected List<DcMediaEntity> castMediaByFeatured() {

        List<DcMediaEntity> entity = new ArrayList<>();


        try {
            String mlist = RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY+"toplist");

            if(mlist==null) {
                RiakTP transport = RiakAPI.getInstance();
                IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
                entity = queryFactory.queryMediaByFeaturedTOP(5);
            } else {
                entity = new Gson().fromJson(mlist,
                        new TypeToken<List<DcMediaEntity>>() {
                        }.getType());
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);

        }

        return entity;
    }
}
