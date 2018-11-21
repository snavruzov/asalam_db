package com.dgtz.db.api.features;

import com.dgtz.db.api.builder.ISaveFactory;
import com.dgtz.db.api.domain.Notificator;
import com.dgtz.db.api.factory.DataBaseAPI;
import com.dgtz.db.api.factory.HttpClientWrapper;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.slf4j.LoggerFactory;

/**
 * Created by sardor on 1/7/14.
 */
public final class PushNotifier extends HttpClientWrapper {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MailNotifier.class);


    public static final String PUSH_INTERNAL_URL = Constants.NOTIF_URL + "jmail/mailer/tools";
    public static final String PUSH_NOTIFCATION_URL = "http://localhost:8443/web/api/tools";

    public PushNotifier() {
    }

    /*deviceType: 4- Android, 5- iPhone*/
    public void sendPushBoxQueue(Notificator info, int deviceType, String key) {

        String partUrl = "/private/push/notice";

        ISaveFactory dataS = DataBaseAPI.getSaveInstance();
        dataS.saveInfoInMemory(key, info.toString(), "3");

        if(info.getType()==4 || info.getType()==5) {
            /*long waitQueue = RMemoryAPI.getInstance()
                    .checkSetElemCount(Constants.NOTIFICATION_KEY + info.getIdMedia()+":"+ info.getToId());*/
            String waitQueue = RMemoryAPI.getInstance()
                    .pullElemFromMemory(Constants.NOTIFICATION_KEY + info.getIdMedia()+":"+ info.getToId());
            if(waitQueue==null) {
                doRequestGet(PUSH_INTERNAL_URL+ partUrl + "?key=" + key+"&os="+deviceType);
            }
        } else {
            doRequestGet(PUSH_INTERNAL_URL+ partUrl + "?key=" + key+"&os="+deviceType);
        }


    }

    public void sendPushBoxQueue(long key) {

        String partUrl = "/private/push/live/notice";

        doRequestGet(PUSH_NOTIFCATION_URL + partUrl + "?key=" + key);
    }


    public void sendTokenRemoveQueue(String devID) {

        String partUrl = "/private/del/deviceid";
        doRequestGet(PUSH_NOTIFCATION_URL + partUrl + "?key=" + devID);
    }
}
