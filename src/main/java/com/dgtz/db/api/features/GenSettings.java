package com.dgtz.db.api.features;

import com.dgtz.db.api.domain.DcUserSettings;
import com.dgtz.db.api.enums.EnumPlatform;
import com.dgtz.db.api.factory.GsonInsta;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.slf4j.LoggerFactory;

/**
 * Created by Sardor Navruzov on 8/27/15.
 * Copyrights Digitizen Co.
 */
public class GenSettings {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GenSettings.class);

    public GenSettings() {
    }

    public static boolean gettUserSettingValue(long idUser, int noteType, EnumPlatform platform) {
        boolean isDeliver = false;
        try {
            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "settings");
            DcUserSettings userNotice = (DcUserSettings) GsonInsta.getInstance().fromJson(val, DcUserSettings.class);

            if (userNotice != null) {
                log.debug("USER SETTING: {}", userNotice.toString());
                switch (noteType) {
                    case 2: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getNew_media().isNotification();
                        else
                            isDeliver = userNotice.getNew_media().isEmail();
                        break;
                    }
                    case 4: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getLiked().isNotification();
                        else
                            isDeliver = userNotice.getLiked().isEmail();
                        log.debug("LIKED NOTE isDelivery  {}", isDeliver);
                        break;
                    }
                    case 1: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getWant_to_join().isNotification();
                        else
                            isDeliver = userNotice.getWant_to_join().isEmail();
                        break;
                    }
                    case 3: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getSuggest_to_join().isNotification();
                        else
                            isDeliver = userNotice.getSuggest_to_join().isEmail();
                        break;
                    }
                    case 6: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getLive_started().isNotification();
                        else
                            isDeliver = userNotice.getLive_started().isEmail();
                        break;
                    }
                    case 5: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getCommented().isNotification();
                        else
                            isDeliver = userNotice.getCommented().isEmail();
                        break;
                    }
                    case 9: {
                        isDeliver = false;
                        break;
                    }
                    case 10: {
                        isDeliver = platform != EnumPlatform.MOBILE && userNotice.getVideo_status().isEmail();
                        break;
                    }
                    case 11: {

                        isDeliver = platform != EnumPlatform.MOBILE && userNotice.getVideo_status().isEmail();
                        break;
                    }
                    case 8: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getYou_channel_joined().isNotification();
                        else
                            isDeliver = userNotice.getYou_channel_joined().isEmail();
                        break;
                    }
                    case 12: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getUser_channel_joined().isNotification();
                        else
                            isDeliver = userNotice.getUser_channel_joined().isEmail();
                        break;
                    }
                    case 16: {
                        isDeliver = platform != EnumPlatform.MOBILE && userNotice.getNewsfeed().isEmail();
                        break;
                    }
                    case 17: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getPromo_push().isNotification();
                        else
                            isDeliver = userNotice.getPromo_push().isEmail();
                        break;
                    }
                    case 18: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getChannel_upd().isNotification();
                        else
                            isDeliver = userNotice.getChannel_upd().isEmail();
                        break;
                    }
                    case 19: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getSchd_event().isNotification();
                        else
                            isDeliver = userNotice.getSchd_event().isEmail();
                        break;
                    }
                    case 20: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getFlw_added().isNotification();
                        break;
                    }
                    case 21: {
                        if (platform == EnumPlatform.MOBILE)
                            isDeliver = userNotice.getSub_channel().isNotification();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("ERROR IN DB API ", e);
        }

        return isDeliver;
    }
}
