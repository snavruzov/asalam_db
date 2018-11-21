package com.dgtz.db.api.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 3/19/14
 */
public class GsonInsta {
    private static Gson instance = null;

    public static synchronized Gson getInstance() {
        if (instance == null) {
            instance = new GsonBuilder().serializeNulls().create();
        }

        return instance;
    }

    private GsonInsta() {
    }

}
