package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.DcChannelsEntity;
import com.brocast.riak.api.beans.DcMediaEntity;
import com.brocast.riak.api.beans.DcUsersEntity;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakQueryFactory;
import com.brocast.riak.api.factory.IRiakSaveFactory;
import com.brocast.riak.api.factory.RiakQueryFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.db.api.beans.DcDeviceType;
import com.dgtz.db.api.beans.EmailRecipients;
import com.dgtz.db.api.dao.db.DBUtils;
import com.dgtz.db.api.dao.db.JDBCUtil;
import com.dgtz.db.api.domain.*;
import com.dgtz.db.api.enums.EnumAggregations;
import com.dgtz.db.api.enums.EnumChannelTypes;
import com.dgtz.db.api.enums.EnumOperationSystem;
import com.dgtz.db.api.enums.EnumSQLErrors;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sardor on 12/1/15.
 */
public class ChannelBuilder extends ChannelRedisBuilder implements IChannelFactory {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ChannelBuilder.class);
    private static final long LIMIT = 12;

    @Override
    public Set<EmailRecipients> getUserMultiChannelEmails(long idOwner, long idChannel) {
        Set<EmailRecipients> emails = new HashSet<>();
        String id_owner = String.valueOf(idOwner);
        try {
            Set<String> list = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.CHANNEL_KEY + "users:" + idChannel);

            list.forEach(usr -> {
                if (!usr.equals(id_owner)) {
                    EmailRecipients recipients = new EmailRecipients();
                    recipients.setIdUser(Long.valueOf(usr));
                    String email = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + usr, "email");
                    recipients.setEmail(email);
                    emails.add(recipients);
                }
            });

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return emails;
    }

    @Override
    public Set<String> getUserMultiChannelDevice(long idOwner, long idChannel, EnumOperationSystem os) {
        Set<String> devices = new HashSet<>();
        try {
            String id_owner = String.valueOf(idOwner);
            Set<String> list = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.CHANNEL_KEY + "users:" + idChannel);

            list.forEach(usr -> {
                if (!usr.equals(id_owner)) {
                    try {
                        Set<String> dvs = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.USER_KEY + "device:" + usr);
                        for (String idDev : dvs) {
                            DcDeviceType dev = (DcDeviceType) RMemoryAPI.getInstance()
                                    .pullElemFromMemory(Constants.DEVICE_KEY + idDev, DcDeviceType.class);
                            if (dev != null && dev.getDevType() == os.value) {
                                devices.add(idDev);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return devices;
    }

    @Override
    public boolean ifUserJoinedToChannel(long idUser, long idChannel) {
        String owner = RMemoryAPI.getInstance().pullHashFromMemory(Constants.CHANNEL_KEY + idChannel, "owner");

        return owner != null && String.valueOf(idUser).equals(owner);
    }

    @Override
    public PublicChannelsEntity extractChannelInfoByIdChannel(long idChannel, long idUser) {
        
        PublicChannelsEntity che = new PublicChannelsEntity();
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            DcChannelsEntity ch = queryFactory.queryChannelDataByID(idChannel);
            if(ch!=null){
                long uCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "users:" + ch.getIdChannel());
                long mCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + ch.getIdChannel());
                che.setIdChannel(ch.idChannel);
                che.setTitle(ch.title);
                che.setDescription(ch.description);
                che.setPrivacy(ch.privacy);
                che.setAccess(ch.props.access);
                Boolean followed = RMemoryAPI.getInstance()
                        .pullIfSetElem(Constants.FOLLOWS + "channels:" + idUser, ch.idChannel + "");
                che.setFollowed(followed);
                che.setOwnerIdUser(ch.idUser);
                che.setUcount(uCount);
                che.setMcount(mCount);
                che.setDateadded(ch.getDateadded());
                String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + ch.idUser, "username");
                che.setUsername(username);
                che.setAvatar(Constants.STATIC_URL + ch.idUser + "/image" + ch.getAvatar() + ".jpg");
                if(ch.getWall()==null || ch.getWall().equals("empty")){
                    che.setWall(Constants.STATIC_URL + "defaults/channel-cover.jpg");
                }else {
                    che.setWall(Constants.STATIC_URL + ch.idUser + "/image" + ch.wall + ".jpg");
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        } 

        return che;
    }

    @Override
    public List<DcChannelsEntity> castMediaByChannelName(String chName, int off, int limit) {
        
        List<DcChannelsEntity> list = new ArrayList<>();
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            list = queryFactory.queryChannelDataByTitle(chName, off, limit);
        } catch (Exception e){
            e.printStackTrace();
        } 
        return list;
    }


    @Override
    public List<DcChannelsEntity> extractChannelsInfoByOwner(long idOUser) {
        List<DcChannelsEntity> channels = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        Connection dbConnection = null;
        try {
            dbConnection = JDBCUtil.getDBConnection();

            preparedStatement = dbConnection.prepareStatement(
                    "SELECT id_channel,title,description,avatar,owner_id_user,enabled, COALESCE(dm.mcnt,0) as mcount " +
                            "FROM dc_channels dc " +
                            "LEFT JOIN (SELECT id_channel, count(*) as mcnt " +
                            "FROM dc_media where progress=0 GROUP BY id_channel) as dm(chn,mcnt) ON dm.chn=dc.id_channel " +
                            "WHERE owner_id_user=? AND enabled");

            preparedStatement.setLong(1, idOUser);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                DcChannelsEntity channelsEntity = new DcChannelsEntity();
                channelsEntity.setIdChannel(rs.getLong("id_channel"));
                channelsEntity.setTitle(rs.getString("title"));
                channelsEntity.setMcount(rs.getLong("mcount"));
                channelsEntity.setDescription(rs.getString("description"));
                channelsEntity.setAvatar(Constants.STATIC_URL + rs.getLong("owner_id_user") +
                        "/image" + rs.getString("avatar") + ".jpg");
                channelsEntity.setIdUser(rs.getLong("owner_id_user"));
                channelsEntity.setEnabled(rs.getBoolean("enabled"));

                channels.add(channelsEntity);
            }


        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, rs);
        }

        return channels;
    }

    @Override
    public List<PublicChannelsEntity> extractChannelsInfo(EnumAggregations enumAgr, Long idUser, long off, long limit) {

        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);
        List<DcChannelsEntity> list;
        List<PublicChannelsEntity> entity = new ArrayList<>();

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            if(enumAgr == EnumAggregations.LAST) {
                list = queryFactory.queryChannelDataByLast((int) off, (int) limit);
            } else {
                list = queryFactory.queryChannelDataByTOP((int) off, (int) limit);
            }

            for (DcChannelsEntity ch : list) {
                PublicChannelsEntity che = new PublicChannelsEntity();
                che.setIdChannel(ch.idChannel);
                che.setTitle(ch.title);
                che.setDescription(ch.description);
                che.setPrivacy(ch.privacy);
                Boolean followed = RMemoryAPI.getInstance()
                        .pullIfSetElem(Constants.FOLLOWS + "channels:" + idUser, ch.idChannel + "");
                che.setFollowed(followed);
                che.setOwnerIdUser(ch.idUser);
                che.setUcount(ch.ucount);
                che.setMcount(ch.mcount);
                che.setDateadded(ch.getDateadded());
                String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + ch.idUser, "username");
                che.setUsername(username);
                che.setAvatar(ch.avatar);
                che.setWall(ch.wall);
                entity.add(che);
            }


        } catch (Exception e) {
            log.error("ERROR IN REDIS DB API ", e);
        }
        return entity;
    }

    @Override
    public List<PublicChannelsEntity> extractChannelsInfoByIdUser(long idUser, long off, long limit) {
        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);

        List<PublicChannelsEntity> channels = new ArrayList<>();

        try {

            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcChannelsEntity> chList = queryFactory.queryChannelDataByIDuser(idUser, (int) off, (int) limit);

            for (DcChannelsEntity ch : chList) {
                {
                    PublicChannelsEntity che = new PublicChannelsEntity();
                    long uCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "users:" + ch.getIdChannel());
                    long mCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + ch.getIdChannel());

                    if(ch.getWall()==null || ch.getWall().equals("empty")){
                        che.setWall(Constants.STATIC_URL + "defaults/channel-cover.jpg");
                    }else {
                        che.setWall(Constants.STATIC_URL + ch.idUser + "/image" + ch.wall + ".jpg");
                    }

                    boolean reFlw = RMemoryAPI.getInstance()
                            .pullIfSetElem(Constants.FOLLOWS + "channels:" + idUser, ch.idChannel + "");
                    che.setIdChannel(ch.idChannel);
                    che.setFollowed(reFlw || ch.idUser==idUser);

                    che.setUcount(uCount);
                    che.setMcount(mCount);
                    che.setTitle(ch.title);
                    che.setDescription(ch.description);
                    che.setDateadded(ch.getDateadded());
                    che.setOwnerIdUser(ch.idUser);
                    che.setEnabled(true);
                    che.setAccess(ch.props.access);
                    String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + ch.getIdUser(), "username");
                    che.setUsername(username);
                    che.setAvatar(Constants.STATIC_URL + ch.idUser + "/image" + ch.getAvatar() + ".jpg");
                    channels.add(che);
                }
            }

        } catch (Exception e) {
            log.error("ERROR IN REDIS DB API ", e);
        }
        return channels;
    }

    @Override
    public List<PublicChannelsEntity> extractChannelsInfoBySearch(String name, long idUser, long off, long limit) {
        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);

        List<PublicChannelsEntity> channels = new ArrayList<>();

        try {

            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcChannelsEntity> chList = queryFactory.queryChannelDataByTitleByIDUser(name, idUser, (int) off, (int) limit);

            for (DcChannelsEntity ch : chList) {
                {
                    PublicChannelsEntity che = new PublicChannelsEntity();
                    long uCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "users:" + ch.getIdChannel());
                    long mCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + ch.getIdChannel());

                    if(ch.getWall()==null || ch.getWall().equals("empty")){
                        che.setWall(Constants.STATIC_URL + "defaults/channel-cover.jpg");
                    }else {
                        che.setWall(Constants.STATIC_URL + ch.idUser + "/image" + ch.wall + ".jpg");
                    }

                    boolean reFlw = RMemoryAPI.getInstance()
                            .pullIfSetElem(Constants.FOLLOWS + "channels:" + idUser, ch.idChannel + "");
                    che.setIdChannel(ch.idChannel);
                    che.setFollowed(reFlw || ch.idUser==idUser);

                    che.setUcount(uCount);
                    che.setMcount(mCount);
                    che.setTitle(ch.title);
                    che.setDescription(ch.description);
                    che.setDateadded(ch.getDateadded());
                    che.setOwnerIdUser(ch.idUser);
                    che.setEnabled(true);
                    che.setAccess(ch.props.access);
                    che.setPrivacy(ch.privacy);
                    String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + ch.getIdUser(), "username");
                    che.setUsername(username);
                    che.setAvatar(Constants.STATIC_URL + ch.idUser + "/image" + ch.getAvatar() + ".jpg");
                    channels.add(che);
                }
            }

        } catch (Exception e) {
            log.error("ERROR IN REDIS DB API ", e);
        }
        return channels;
    }


    @Override
    public List<PublicChannelsEntity> extractChannelsInfoByFollowsSearch(String name, long idUser, long off, long limit) {
        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);

        List<PublicChannelsEntity> channels = new ArrayList<>();

        try {

            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcChannelsEntity> chList = queryFactory.queryChannelDataByTitleByFollowing(name, idUser, (int) off, (int) limit);

            for (DcChannelsEntity ch : chList) {
                {
                    PublicChannelsEntity che = new PublicChannelsEntity();
                    long uCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "users:" + ch.getIdChannel());
                    long mCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + ch.getIdChannel());

                    if(ch.getWall()==null || ch.getWall().equals("empty")){
                        che.setWall(Constants.STATIC_URL + "defaults/channel-cover.jpg");
                    }else {
                        che.setWall(Constants.STATIC_URL + ch.idUser + "/image" + ch.wall + ".jpg");
                    }

                    boolean reFlw = RMemoryAPI.getInstance()
                            .pullIfSetElem(Constants.FOLLOWS + "channels:" + idUser, ch.idChannel + "");
                    che.setIdChannel(ch.idChannel);
                    che.setFollowed(reFlw || ch.idUser==idUser);

                    che.setUcount(uCount);
                    che.setMcount(mCount);
                    che.setTitle(ch.title);
                    che.setDescription(ch.description);
                    che.setDateadded(ch.getDateadded());
                    che.setOwnerIdUser(ch.idUser);
                    che.setEnabled(true);
                    che.setAccess(ch.props.access);
                    che.setPrivacy(ch.privacy);
                    String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + ch.getIdUser(), "username");
                    che.setUsername(username);
                    che.setAvatar(Constants.STATIC_URL + ch.idUser + "/image" + ch.getAvatar() + ".jpg");
                    channels.add(che);
                }
            }

        } catch (Exception e) {
            log.error("ERROR IN REDIS DB API ", e);
        }
        return channels;
    }

    @Override
    public List<PublicChannelsEntity> extractChannelsInfoByFollowing(long idUser, long off, long limit) {
        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);
        List<PublicChannelsEntity> channels = new ArrayList<>();

        try {

            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcChannelsEntity> chList = queryFactory.queryChannelDataByFollows(idUser, (int) off, (int) limit);

            for (DcChannelsEntity ch : chList) {
                {
                    PublicChannelsEntity che = new PublicChannelsEntity();
                    long uCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "users:" + ch.getIdChannel());
                    long mCount = RMemoryAPI.getInstance().checkSetElemCount(Constants.CHANNEL_KEY + "videos:" + ch.getIdChannel());

                    if(ch.getWall()==null || ch.getWall().equals("empty")){
                        che.setWall(Constants.STATIC_URL + "defaults/channel-cover.jpg");
                    }else {
                        che.setWall(Constants.STATIC_URL + ch.idUser + "/image" + ch.wall + ".jpg");
                    }

                    boolean reFlw = RMemoryAPI.getInstance()
                            .pullIfSetElem(Constants.FOLLOWS + "channels:" + idUser, ch.idChannel + "");
                    che.setIdChannel(ch.idChannel);
                    che.setFollowed(reFlw || ch.idUser==idUser);

                    che.setUcount(uCount);
                    che.setTitle(ch.title);
                    che.setDescription(ch.description);
                    che.setMcount(mCount);
                    che.setDateadded(ch.getDateadded());
                    che.setOwnerIdUser(ch.idUser);
                    che.setEnabled(true);
                    che.setAccess(ch.props.access);
                    String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + ch.getIdUser(), "username");
                    che.setUsername(username);
                    che.setAvatar(Constants.STATIC_URL + ch.idUser + "/image" + ch.getAvatar() + ".jpg");
                    channels.add(che);
                }
            }

        } catch (Exception e) {
            log.error("ERROR IN REDIS DB API ", e);
        }
        return channels;
    }

    @Override
    public List<MediaNewsStatInfo> extractChannelsContent(long idChannel, long off, long limit, String lang) {
        List<MediaNewsStatInfo> entity = new ArrayList<>();

        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcMediaEntity> list = queryFactory.queryMediaDataByIdChannel(idChannel, (int) off, (int) limit);


            String currentTime = String.valueOf(RMemoryAPI.getInstance().currentTimeMillis());
            for (DcMediaEntity m : list) {
                MediaNewsStatInfo mediaList = new MediaNewsStatInfo();

                mediaList.setIdMedia(m.getIdMedia());
                mediaList.setTitle(m.getTitle());
                mediaList.setContentType("video");
                mediaList.setDuration(m.getDuration());
                mediaList.setDateadded(m.getDateadded());
                mediaList.setLive(m.method.equals("live"));
                mediaList.setIdUser(m.getIdUser());
                String vcnt = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "vcount");
                String lcnt = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + m.idMedia, "liked");

                mediaList.setAmount(Long.valueOf(vcnt));
                String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + m.getIdUser(), "username");
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + m.getIdUser(), "avatar");
                mediaList.setUsername(username);
                mediaList.setAvatar(Constants.STATIC_URL + m.getIdUser() + "/image" + avatar + ".jpg");

                String thumb = Constants.encryptAmazonURL(m.getIdUser(), m.getIdMedia(), "jpg", "thumb", Constants.STATIC_URL);
                String thumb_webp = Constants.encryptAmazonURL(m.getIdUser(), m.getIdMedia(), "webp", "thumb", Constants.STATIC_URL);
                mediaList.setThumb(thumb);
                mediaList.setThumb_webp(thumb_webp);

                mediaList.setLiked(Long.valueOf(lcnt));
                mediaList.setCurrentTime(currentTime);
                mediaList.setRatio(m.ratio);

                mediaList.setLocation(m.location);

                entity.add(mediaList);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }
        

        return entity;
    }

    @Override
    public List<UserShortInfo> extractChannelsUsers(long idChannel, long offset, long limit, String lang) {
        List<UserShortInfo> entityList = new ArrayList<>();

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
                object.setAvatar(Constants.STATIC_URL + entity.idUser + "/image" + entity.avatar + ".jpg");
                object.setUsername(entity.username);
                object.setFullname(entity.fullname);
                object.setIdUser(entity.idUser);
                entityList.add(object);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }

    @Override
    public Deque<UserShortInfo> extractChannelsUsers(long idChannel, long idUser, long offset, long limit, String lang) {
        Deque<UserShortInfo> entityList = new LinkedList<>();

        try {
            List<String> res = RMemoryAPI.getInstance().pullSortedListElem(Constants.CHANNEL_KEY + "users:" + idChannel,
                    Constants.USER_KEY + "*->detail", (int) offset, (int) (offset + ((limit <= 0 || limit > 60) ? LIMIT : limit)),
                    Constants.USER_KEY + "*->id_user");
            Gson gson = new Gson();

            List<DcUsersEntity> list = gson.fromJson(res.toString(),
                    new TypeToken<List<DcUsersEntity>>() {
                    }.getType());

            String chAuthor = RMemoryAPI.getInstance().pullHashFromMemory(Constants.CHANNEL_KEY + idChannel, "owner");

            for (DcUsersEntity entity : list) {
                UserShortInfo object = new UserShortInfo();

                object.setAbout(entity.about);
                object.setAvatar(Constants.STATIC_URL + entity.idUser + "/image" + entity.avatar + ".jpg");
                object.setUsername(entity.username);
                object.setFullname(entity.fullname);
                object.setIdUser(entity.idUser);

                Boolean follow = RMemoryAPI.getInstance()
                        .pullIfSetElem(Constants.FOLLOWS + idUser, entity.idUser + "");
                Long flwcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + entity.idUser);
                object.setFlwnum(flwcount);
                object.setReFollow(follow);

                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + entity.idUser, "location:" + lang);
                object.setLocation(local);

                if (Objects.equals(entity.idUser, Long.valueOf(chAuthor))) {
                    entityList.addFirst(object);
                } else {
                    entityList.addLast(object);
                }
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }

    @Override
    public NotificationInfo getChannelNotification(long idUser, long off, long limit) {
        List<Notification> infoList = new ArrayList<>();
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        NotificationInfo notificationInfo = new NotificationInfo();
        try {
            dbConnection = JDBCUtil.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(
                    "SELECT dn.id_note,dn.id_user, dn.id_dest, dn.id_channel,dt.id_note_tp,du.username, dc.title, " +
                            "dn.is_valid, du.avatar, dc.avatar as chava " +
                            "FROM dc_notificator dn " +
                            "JOIN dc_users du ON dn.id_user = du.id_user " +
                            "JOIN dc_channels dc ON dc.id_channel = dn.id_channel AND dc.enabled " +
                            "JOIN dc_notification_type dt ON dn.id_note_type=dt.id_note_tp " +
                            "WHERE dn.is_valid AND dn.id_dest=? " +
                            "AND dn.id_note_type IN (1,3) ORDER BY dn.id_note DESC LIMIT ? OFFSET ?");

            preparedStatement.setLong(1, idUser);
            preparedStatement.setLong(2, (limit <= 0 || limit > 60) ? LIMIT : limit);
            preparedStatement.setLong(3, off < 0 ? 0 : off);

            rs = preparedStatement.executeQuery();
            AtomicInteger inc = new AtomicInteger(0);
            while (rs.next()) {
                Notification note = new Notification();

                note.setIdChannel(rs.getLong("id_channel"));
                note.setIdUser(rs.getLong("id_user"));
                note.setText(rs.getString("title"));
                note.setUsername(rs.getString("username"));
                note.setValid(rs.getBoolean("is_valid"));
                note.setIdNote(rs.getLong("id_note"));
                note.setType(rs.getInt("id_note_tp"));
                note.setUrl(Constants.STATIC_URL + note.getIdUser() + "/image" + rs.getString("avatar") + ".jpg", null);
                note.setChAvatar(Constants.STATIC_URL +
                        (rs.getInt("id_note_tp") == 1 ? rs.getLong("id_dest") : note.getIdUser())
                        + "/image" + rs.getString("chava") + ".jpg");

                if (note.isValid()) {
                    notificationInfo.setCount(inc.incrementAndGet());
                }
                infoList.add(note);
            }

            notificationInfo.setNotifications(infoList);

        } catch (SQLException e) {
            log.error("ERROR IN DB API ", e);
        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, rs);
        }
        return notificationInfo;
    }

    @Override
    public long createChannel(DcChannelsEntity entity) {

        long idChannel = 0;

        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        
        try {

            dbConnection = JDBCUtil.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(
                    "INSERT INTO dc_channels(title,description, avatar, owner_id_user, enabled, private) " +
                            "VALUES (?, ?, ?, ?, TRUE, ?) RETURNING id_channel");

            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.setString(3, entity.getAvatar());
            preparedStatement.setLong(4, entity.getIdUser());
            preparedStatement.setLong(5, entity.getPrivacy());

            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                idChannel = rs.getLong("id_channel");
                entity.idChannel = idChannel;
            }

            RiakTP transport = RiakAPI.getInstance();
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.buildChannelContent(entity);

            RMemoryAPI.getInstance().pushHashToMemory(Constants.CHANNEL_KEY + idChannel, "rating", "0.0");
            saveRedisChannel(entity);

        } catch (SQLException e) {
            log.error("ERROR IN DB API ", e);
            DBUtils.dbRollback(dbConnection);

        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, rs);
        }

        return idChannel;
    }

    @Override
    public EnumSQLErrors updateChannelInfo(DcChannelsEntity entity) {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.updChannelContent(entity);

            dbConnection = JDBCUtil.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(
                    "SELECT a.t FROM update_channel_info(?, ?, ?, ?, ?, ?) AS a(t); ");

            preparedStatement.setLong(1, entity.idUser);
            preparedStatement.setLong(2, entity.idChannel);
            preparedStatement.setString(3, entity.title);
            preparedStatement.setString(4, entity.description);
            preparedStatement.setString(5, entity.avatar);
            preparedStatement.setInt(6, entity.privacy);

            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                saveRedisChannel(entity);
            }

        } catch (Exception e) {
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
            log.error("ERROR UNKNOWN_SQL_ERROR: ", e);
            DBUtils.dbRollback(dbConnection);
        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, rs);
        }

        return errors;
    }

    @Override
    public JSONObject joinToChannel(long id_user, long idChannel, boolean byOwner, boolean accept, boolean refuse) {

        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        JSONObject json = null;
        try {

            boolean iswait = RMemoryAPI.getInstance().pullIfSetElem(Constants.CHANNEL_KEY + "qwaiters:" + idChannel, id_user + "");
            boolean ispart = RMemoryAPI.getInstance().pullIfSetElem(Constants.CHANNEL_KEY + "users:" + idChannel, id_user + "");

            json = new JSONObject("{\"ispart\":\"" + ispart + "\",\"iswait\":\"" + iswait + "\"}");
            log.debug("JSON JOIN: {}", json.toString());

            if (!ispart) {
                joinToRedisChannel(id_user, idChannel, accept, refuse);
                dbConnection = JDBCUtil.getDBConnection();
                String idOwner = RMemoryAPI.getInstance().pullHashFromMemory(Constants.CHANNEL_KEY + idChannel, "owner");

                if (!accept && !iswait && !refuse) {
                    preparedStatement = dbConnection.prepareStatement("INSERT INTO dc_notificator (id_user,id_dest,id_channel,id_note_type) VALUES (?,?,?,?)");
                    preparedStatement.setLong(1, byOwner ? Long.parseLong(idOwner) : id_user);
                    preparedStatement.setLong(2, byOwner ? id_user : Long.parseLong(idOwner));
                    preparedStatement.setLong(3, idChannel);
                    preparedStatement.setLong(4, byOwner ? 3 : 1); // 3 - owner is inviting, 1 - user is asking for join

                    preparedStatement.executeUpdate();

                } else if (iswait && (accept || refuse)) {
                    preparedStatement = dbConnection.prepareStatement("UPDATE dc_notificator SET is_valid = FALSE WHERE id_dest=? AND id_channel=?");
                    preparedStatement.setLong(1, byOwner ? Long.parseLong(idOwner) : id_user);
                    preparedStatement.setLong(2, idChannel);

                    preparedStatement.executeUpdate();
                }

            }

        } catch (Exception e) {
            DBUtils.dbRollback(dbConnection);
            log.error("ERROR IN DB API ", e);
        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, null);
        }

        return json;
    }

    @Override
    public boolean leaveTheChannel(long id_user, long idChannel) {
        boolean isLeft = false;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            dbConnection = JDBCUtil.getDBConnection();

            preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT a.t FROM leave_from_channel(%s,%s) as a(t)", id_user, idChannel));

            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                isLeft = rs.getBoolean(1);
                leaveTheRedisChannel(id_user, idChannel);
            }

        } catch (SQLException e) {
            log.error("ERROR IN DB API ", e);
        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, rs);
        }

        return isLeft;
    }

    @Override
    public EnumSQLErrors acceptJoinerByOwner(long id_user, long id_act_channel) {
        return null;
    }

    @Override
    public EnumSQLErrors removeChannelById(long id_ouser, long idChannel) {
        EnumSQLErrors errors = EnumSQLErrors.OK;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            DcChannelsEntity entity = queryFactory.queryChannelDataByID(idChannel);

            entity.enabled = false;

            saveFactory.updChannelContent(entity);

            dbConnection = JDBCUtil.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT a.t FROM remove_channel_info(%s,%s) as a(t)", id_ouser, idChannel));

            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                removeRedisChannelById(saveFactory, id_ouser, idChannel);
            }

        } catch (SQLException e) {
            errors = EnumSQLErrors.UNKNOWN_SQL_ERROR;
            log.error("ERROR IN DB API ", e);
        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, rs);
        }
        return errors;
    }

    @Override
    public EnumSQLErrors removeMediaFromChannel(long idMedia, long idChannel) {
        Connection dbConnection = null;
        EnumSQLErrors errors = EnumSQLErrors.OK;
        PreparedStatement preparedStatement = null;
        try{

            String privacy = RMemoryAPI.getInstance().pullHashFromMemory(Constants.CHANNEL_KEY + idChannel, "private");
            dbConnection = JDBCUtil.getDBConnection();
            int chprops = Integer.valueOf(privacy);
            preparedStatement = dbConnection.prepareStatement(
                    "UPDATE dc_media SET id_channel=0, props = ? WHERE id_media=? AND id_channel=?");

            preparedStatement.setLong(3, idChannel);
            preparedStatement.setLong(2, idMedia);
            preparedStatement.setLong(1, chprops != 0 ? 2 : 0);
            preparedStatement.executeUpdate();

            removeRedisMediaFromChannel(idMedia, idChannel);


        } catch (SQLException e) {
            DBUtils.dbRollback(dbConnection);
            errors = EnumSQLErrors.UPDATE_SQL_ERROR;
            log.error("ERROR IN UPDATING - " + e.getMessage());

        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, null);
        }

        return errors;
    }

    @Override
    public EnumSQLErrors subscribeToChannel(long idUser, long idChannel) {
        Connection dbConnection = null;
        EnumSQLErrors errors = EnumSQLErrors.OK;
        PreparedStatement preparedStatement = null;

        try {
            RMemoryAPI.getInstance().pushSetElemToMemory(Constants.CHANNEL_KEY + "subs:" + idChannel, idUser + "");
            boolean isMemeber = RMemoryAPI.getInstance().pullIfSetElem(Constants.CHANNEL_KEY + "users:" + idChannel, idUser + "");

            dbConnection = JDBCUtil.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(
                    "INSERT INTO dc_channel_activity (id_user,id_channel,confirm,activity_type) VALUES (?,?,?,?) ");

            preparedStatement.setLong(1, idUser);
            preparedStatement.setLong(2, idChannel);
            preparedStatement.setBoolean(3, isMemeber);
            preparedStatement.setLong(4, 1);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            DBUtils.dbRollback(dbConnection);
            errors = EnumSQLErrors.UPDATE_SQL_ERROR;
            log.error("ERROR IN UPDATING - ", e);

        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, null);
        }

        return errors;
    }

    @Override
    public EnumSQLErrors unsubFromChannel(long idUser, long idChannel) {
        Connection dbConnection = null;
        EnumSQLErrors errors = EnumSQLErrors.OK;
        PreparedStatement preparedStatement = null;

        try {
            RMemoryAPI.getInstance().delFromSetElem(Constants.CHANNEL_KEY + "subs:" + idChannel, idUser + "");

            dbConnection = JDBCUtil.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(
                    "DELETE FROM dc_channel_activity WHERE id_user=? AND id_channel=? AND activity_type=1");

            preparedStatement.setLong(1, idUser);
            preparedStatement.setLong(2, idChannel);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            DBUtils.dbRollback(dbConnection);
            errors = EnumSQLErrors.UPDATE_SQL_ERROR;
            log.error("ERROR IN UPDATING - ", e);

        } finally {
            DBUtils.closeConnections(preparedStatement, dbConnection, null);
        }

        return errors;
    }

    @Override
    public List<UserShortInfo> extractSubsOfChannel(long idChannel, long idUser, long offset, long limit, String lang) {
        List<UserShortInfo> entityList = new ArrayList<>();

        try {
            List<String> res = RMemoryAPI.getInstance().pullSortedListElem(Constants.CHANNEL_KEY + "subs:" + idChannel,
                    Constants.USER_KEY + "*->detail", (int) offset, (int) (offset + ((limit <= 0 || limit > 60) ? LIMIT : limit)),
                    Constants.USER_KEY + "*->id_user");
            Gson gson = new Gson();

            List<DcUsersEntity> list = gson.fromJson(res.toString(),
                    new TypeToken<List<DcUsersEntity>>() {
                    }.getType());

            for (DcUsersEntity entity : list) {
                UserShortInfo object = new UserShortInfo();
                object.setAvatar(Constants.STATIC_URL + entity.idUser + "/image" + entity.avatar + ".jpg");
                object.setUsername(entity.username);
                object.setFullname(entity.fullname);
                object.setIdUser(entity.idUser);

                Boolean follow = RMemoryAPI.getInstance()
                        .pullIfSetElem(Constants.FOLLOWS + idUser, entity.idUser + "");
                object.setReFollow(follow);

                String local = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + entity.idUser, "location:" + lang);
                object.setLocation(local);
                entityList.add(object);
            }

        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return entityList;
    }
}
