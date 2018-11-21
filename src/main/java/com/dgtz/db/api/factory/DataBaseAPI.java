package com.dgtz.db.api.factory;

import com.dgtz.db.api.builder.*;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/22/13
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public final class DataBaseAPI {

    private static QueryFactory apiInstance = null;
    private static UpdateFactory apiSaveInstance = null;
    private static ChannelBuilder apiChannelInstance = null;

    public DataBaseAPI() {
    }


    public static IQueryFactory getInstance() {

        if (apiInstance == null) {
            apiInstance = new QueryFactory();
        }

        return apiInstance;
    }

    public static ISaveFactory getSaveInstance() {
        //

        if (apiSaveInstance == null) {
            apiSaveInstance = new UpdateFactory();
        }

        return apiSaveInstance;
    }

    public static IChannelFactory getChannelInstance() {
        //

        if (apiChannelInstance == null) {
            apiChannelInstance = new ChannelBuilder();
        }

        return apiChannelInstance;
    }

}
