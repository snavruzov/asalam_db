package com.dgtz.db.api.builder;

import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;

import java.util.Set;

/**
 * Created by sardor on 3/15/16.
 */
public final class HomePageFactory {

    public HomePageFactory() {
    }

    protected static void geoAddToMediaIds(String idMedia){
        String location = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "lang");
        if(location==null || location.isEmpty())
        {
            location = "en";
        }
        else {
            boolean doGeoSpat = RMemoryAPI.getInstance().pullIfSetElem(Constants.TRANSLATION + ":list", location);
            if(!doGeoSpat){
                location = "en";
            }
        }
        RMemoryAPI.getInstance().pushSetElemToMemory(Constants.MEDIA_KEY + "ids:"+location, idMedia);
    }

    protected static void geoDelFromMediaIds(String idMedia){
        Set<String> codes = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.TRANSLATION + ":list");
        codes.forEach(code -> RMemoryAPI.getInstance().delFromSetElem(Constants.MEDIA_KEY + "ids:"+code, idMedia));
    }

}
