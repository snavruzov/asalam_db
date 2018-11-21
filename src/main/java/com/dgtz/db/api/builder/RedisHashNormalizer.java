package com.dgtz.db.api.builder;

import com.brocast.riak.api.beans.DcMediaEntity;
import com.dgtz.db.api.factory.GsonInsta;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;

import java.util.Set;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 11/25/14
 */
public final class RedisHashNormalizer {

    public RedisHashNormalizer() {
    }

    protected void channelVideoNormalize(Long idChannel) {
        Set<String> list = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.CHANNEL_KEY + "videos:" + idChannel);

        for (String idMedia : list) {

            String val = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "detail");
            DcMediaEntity media = (DcMediaEntity) GsonInsta.getInstance().fromJson(val, DcMediaEntity.class);

            if (media == null || media.getProgress() > 0) {
                RMemoryAPI.getInstance().delFromSetElem(Constants.CHANNEL_KEY + "videos:" + idChannel, idMedia);

                UpdateFactory updateFactory = new UpdateFactory();
                updateFactory.updateChannelVideoAMount(idChannel, 0l);
            }
        }
    }
}
