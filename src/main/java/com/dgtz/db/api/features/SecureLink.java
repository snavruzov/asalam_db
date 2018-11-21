package com.dgtz.db.api.features;

import com.brocast.riak.api.beans.DcMediaEntity;
import com.brocast.riak.api.beans.DcUsersEntity;
import com.dgtz.db.api.builder.IQueryFactory;
import com.dgtz.db.api.domain.Notificator;
import com.dgtz.db.api.factory.DataBaseAPI;
import com.dgtz.mcache.api.factory.Constants;
import org.slf4j.LoggerFactory;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 3/2/14
 */
public class SecureLink {

    private static final String SECRET = "gagginator";
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SecureLink.class);


    public static String urlThumbGenerator(Notificator notify) {
        IQueryFactory dataQ = DataBaseAPI.getInstance();

        String url = "";
        if (notify.getIdChannel() != null && notify.getIdChannel() > 0) {
            DcUsersEntity usersEntity = dataQ.getUserProfileInfoById(notify.getFromId());
            url = usersEntity.avatar;
            log.debug("URL TO MOBILE CHANNEL NOTIFICATION::: {}", url);
        } else if (notify.getIdMedia() != null && notify.getIdMedia() > 0) {
            DcMediaEntity mediaEntity = dataQ.castSimpleMediaById(notify.getIdMedia());
            url = Constants.encryptAmazonURL(mediaEntity.getIdUser(), notify.getIdMedia(), "jpg", "thumb", Constants.STATIC_URL);
        }


        System.out.println("URL PUSH: " + url);

        return url;
    }
}