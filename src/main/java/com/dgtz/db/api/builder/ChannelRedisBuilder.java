package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.DcChannelsEntity;
import com.brocast.riak.api.beans.DcMediaEntity;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakSaveFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Set;

/**
 * Created by sardor on 12/1/15.
 */
public abstract class ChannelRedisBuilder {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ChannelRedisBuilder.class);


    /**
     * Create redis channel.
     *
     * @param entity    the entity
     */
    public void saveRedisChannel(DcChannelsEntity entity) {

        try {
            Long idChannel = entity.idChannel;
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.CHANNEL_KEY + entity.idChannel,
                            "id_channel", entity.idChannel + "");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.CHANNEL_KEY + idChannel,
                            "mcount", entity.mcount+"");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.CHANNEL_KEY + idChannel,
                            "ucount", entity.ucount+"");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.CHANNEL_KEY + idChannel,
                            "private", entity.getPrivacy() + "");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.CHANNEL_KEY + idChannel,
                            "access", entity.props.access + "");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.CHANNEL_KEY + idChannel,
                            "title", entity.title);
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.CHANNEL_KEY + idChannel,
                            "owner", entity.idUser + "");
            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.CHANNEL_KEY + idChannel,
                            "detail", entity.toString());

//            RMemoryAPI.getInstance()
//                    .pushSetElemToMemory(Constants.CHANNEL_KEY + "users:" + idChannel, entity.idUser + "");
            RMemoryAPI.getInstance()
                    .pushLSetElemToMemory(Constants.USER_KEY + "channels:" + entity.getIdUser(), entity.getPrivacy(), idChannel+"");


        } catch (Exception e) {
            log.error("ERROR IN REDIS IMPL IN DB API ", e);
        }

    }

    /**
     * Update redis channel info.
     *
     */
    @Deprecated
    public void updateRedisChannelInfo(DcChannelsEntity channel) {

        try {

            if (channel != null) {
//                RMemoryAPI.getInstance().pushHashToMemory(Constants.CHANNEL_KEY + channel.idChannel, "private", channel.privacy + "");
//                RMemoryAPI.getInstance().pushHashToMemory(Constants.CHANNEL_KEY + idChannel, "detail", channel.toString());
//                RMemoryAPI.getInstance()
//                        .pushLSetElemToMemory(Constants.USER_KEY + "channels:" + channel.getIdUser(), props, idChannel+"");
            }


        } catch (Exception e) {
            log.error("ERROR IN CHANNEL REDIS DB UPDATING: ", e);
        }

    }

    /**
     * Join to r channel.
     *
     * @param id_user   the id _ user
     * @param idChannel the id channel
     * @throws SQLException the sQL exception
     */
    public void joinToRedisChannel(long id_user, long idChannel, boolean accept, boolean refuse) throws SQLException {

        try {

            if(accept) {
                String privacy = RMemoryAPI.getInstance().pullHashFromMemory(Constants.CHANNEL_KEY + idChannel, "private");
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.CHANNEL_KEY + "users:" + idChannel, id_user + "");
                RMemoryAPI.getInstance().pushLSetElemToMemory(Constants.USER_KEY + "channels:" + id_user, Double.valueOf(privacy), idChannel + "");
            }
            if(accept || refuse) {
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.CHANNEL_KEY + "qwaiters:" + idChannel, id_user + "");
            } else {
                RMemoryAPI.getInstance().delFromSetElem(Constants.CHANNEL_KEY + "qwaiters:" + idChannel, id_user + "");
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.CHANNEL_KEY + "qwaiters:" + idChannel, id_user + "");
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
    }

    /**
     * Leave the r channel.
     *
     * @param id_user   the id _ user
     * @param idChannel the id channel
     * @throws SQLException the sQL exception
     */
    public void leaveTheRedisChannel(long id_user, long idChannel) throws SQLException {

        try {
            RMemoryAPI.getInstance().delFromSetElem(Constants.CHANNEL_KEY + "users:" + idChannel, id_user + "");
            RMemoryAPI.getInstance().delFromLSetElem(Constants.USER_KEY + "channels:" + id_user, idChannel+"");
            RMemoryAPI.getInstance().delFromSetElem(Constants.CHANNEL_KEY + "qwaiters:" + idChannel, id_user + "");

            Set<String> videoIds = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.CHANNEL_KEY + "videos:" + idChannel);
            for(String idMedia: videoIds) {
                DcMediaEntity entity = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "detail", DcMediaEntity.class);
                if(entity.getIdUser()==id_user){
                    RMemoryAPI.getInstance().delFromSetElem(Constants.CHANNEL_KEY + "videos:" + idChannel, idMedia);
//                    entity.setIdChannel(0); todo
//                    if(entity.getProps()!=0){
//                        entity.setProps(2);
//                        RMemoryAPI.getInstance().pushUnlimitedListToMemory(Constants.ARCHIVE + id_user, entity.getIdMedia()+""); //private videos archived
//                    }
                    RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia, "detail", entity.toString());
                }
            }

            updateChannelVideosCount(idChannel);

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

    }

    /**
     * Remove r channel by id.
     *
     * @param id_ouser  the id _ ouser
     * @param idChannel the id channel
     * @throws SQLException the sQL exception
     */
    public void removeRedisChannelById(IRiakSaveFactory saveFactory, long id_ouser, long idChannel) throws SQLException {

        try {
            Set<String> users = RMemoryAPI.getInstance()
                    .pullSetElemFromMemory(Constants.CHANNEL_KEY + "users:" + idChannel);

            users.forEach(idu -> RMemoryAPI.getInstance()
                    .delFromSetElem(Constants.FOLLOWS + "channels:" + idu, idChannel + ""));

            RMemoryAPI.getInstance().delFromMemory(Constants.CHANNEL_KEY + idChannel);
            RMemoryAPI.getInstance().delFromMemory(Constants.CHANNEL_KEY + "users:" + idChannel);
            RMemoryAPI.getInstance().delFromListElem(Constants.CHANNEL_KEY + "ids", idChannel + "");


            Set<String> chUsers = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.CHANNEL_KEY + "users:" + idChannel);
            chUsers.forEach(idUser->RMemoryAPI.getInstance().delFromLSetElem(Constants.USER_KEY + "channels:" + idUser, idChannel+""));

            Set<String> list = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.CHANNEL_KEY + "videos:" + idChannel);
            for (String idMedia : list) {
                DcMediaEntity media = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "detail", DcMediaEntity.class);
                if (media != null) {
                    media.setIdChannel(0l);

                    saveFactory.updMediaContent(media);

                    RMemoryAPI.getInstance().pushUnlimitedListToMemory(Constants.ARCHIVE + media.getIdUser(), media.getIdMedia() + "");
                    RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia, "detail", media.toString());
                }

                RedisSaveTemplate.delFormMediaLists(idMedia);
            }

            RMemoryAPI.getInstance().delFromMemory(Constants.CHANNEL_KEY + "videos:" + idChannel);

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

    }

    /**
     * Remove r media from channel.
     *
     * @param idMedia   the id media
     * @param idChannel the id channel
     * @throws SQLException the sQL exception
     */
    protected void removeRedisMediaFromChannel(long idMedia, long idChannel) throws SQLException {
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            DcMediaEntity media = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "detail", DcMediaEntity.class);
            RMemoryAPI.getInstance().delFromSetElem(Constants.CHANNEL_KEY + "videos:" + idChannel, idMedia + "");

            if (media != null) {
                media.setIdChannel(0l);
                saveFactory.updMediaContent(media);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia, "detail", media.toString());
            }

            updateChannelVideosCount(idChannel);

        } catch (Exception e) {
            log.error("ERROR IN UPDATING - ", e);

        }
        

    }


    private void updateChannelVideosCount(long idChannel){
        long mcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + idChannel);
        DcChannelsEntity channel = RMemoryAPI.getInstance().pullHashFromMemory(Constants.CHANNEL_KEY + idChannel, "detail", DcChannelsEntity.class);
        channel.setMcount(mcount);
        RMemoryAPI.getInstance().pushHashToMemory(Constants.CHANNEL_KEY + idChannel, "detail", channel.toString());
        RMemoryAPI.getInstance().pushHashToMemory(Constants.CHANNEL_KEY + idChannel, "mcount", mcount+"");
    }



    private void alterChannelVideoPrvcCollection(int ch_props,int crnt_privacy, long idChannel) throws Exception {
        Set<String> videoIds = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.CHANNEL_KEY + "videos:" + idChannel);

        for(String idMedia: videoIds){
            DcMediaEntity entity = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "detail", DcMediaEntity.class);
            if(entity!=null){
                if(ch_props>0){
                    String categories =
                            (String) RMemoryAPI.getInstance().pullElemFromMemory("cid:" + entity.getIdCategory(), String.class);

                    RMemoryAPI.getInstance().delFromListElem(Constants.CETEGORIES + categories, idMedia + "");
                    //RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_KEY + "ids", idMedia + "");
                    HomePageFactory.geoDelFromMediaIds(idMedia);

                    RMemoryAPI.getInstance().delFromSetElem(Constants.LIVES, idMedia + "");
                    RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_KEY + "recommended", idMedia + "");
                } else {
                    String categories = (String) RMemoryAPI.getInstance()
                            .pullElemFromMemory("cid:" + entity.getIdCategory(), String.class);

                    RMemoryAPI.getInstance().pushListElemToMemory(Constants.CETEGORIES + categories, idMedia + "");
                    //RMemoryAPI.getInstance().pushListElemToMemory(Constants.MEDIA_KEY + "ids", idMedia + "");
                    HomePageFactory.geoAddToMediaIds(idMedia);
                }

               // entity.setProps(ch_props==0?0:1);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia, "detail", entity.toString());
            }
        }

        if (ch_props == 2) {
            RMemoryAPI.getInstance().delFromListElem(Constants.CHANNEL_KEY + "ids", idChannel + "");
        } else if (crnt_privacy==2 && ch_props<=1) {
            RMemoryAPI.getInstance()
                    .pushListElemToMemory(Constants.CHANNEL_KEY + "ids", idChannel + "");
        }
    }
}
