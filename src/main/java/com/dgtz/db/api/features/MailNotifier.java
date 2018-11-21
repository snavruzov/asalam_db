package com.dgtz.db.api.features;

import com.dgtz.db.api.builder.ISaveFactory;
import com.dgtz.db.api.domain.Notificator;
import com.dgtz.db.api.factory.DataBaseAPI;
import com.dgtz.db.api.factory.HttpClientWrapper;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by sardor on 1/7/14.
 */
public final class MailNotifier extends HttpClientWrapper {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MailNotifier.class);

    public static final String MAIL_INTERNAL_URL = Constants.NOTIF_URL + "jmail/mailer/tools";

    public MailNotifier() {
    }

    public void sendMailBoxQueue(Notificator info, String key) {

        String partUrl = "/private/send/notice";


        ISaveFactory dataS = DataBaseAPI.getSaveInstance();
        dataS.saveInfoInMemory(key, info.toString(), "3");

        try {
            if (info.getType() == 4 || info.getType() == 5) {
                String waitQueue = RMemoryAPI.getInstance()
                        .pullElemFromMemory(Constants.NOTIFICATION_KEY + info.getIdMedia() + ":" + info.getToId());

                log.debug("WAIT QUEUE: {}", waitQueue);
                if (waitQueue == null) {
                    doRequestGet(MAIL_INTERNAL_URL + partUrl + "?key=" + key);
                }
            } else {
                doRequestGet(MAIL_INTERNAL_URL + partUrl + "?key=" + key);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    class TempJSONObject implements Serializable {
        private static final long serialVersionUID = 1L;

        TempJSONObject() {
        }

        private String error;

        String getError() {
            return error;
        }

        void setError(String error) {
            this.error = error;
        }
    }
}
