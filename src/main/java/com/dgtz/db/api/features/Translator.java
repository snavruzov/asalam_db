package com.dgtz.db.api.features;

import com.brocast.riak.api.beans.DcUsersEntity;
import com.brocast.riak.api.beans.PrivateInfo;
import com.dgtz.db.api.beans.Language;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by Sardor Navruzov on 9/17/15.
 * Copyrights Digitizen Co.
 */
public class Translator {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Translator.class);

    public Translator() {
    }

    public static String initTranslate(String text, String lang) {
        String KEY = "trnsl.1.1.20150917T131735Z.a2e5329f4864d441.8f2ef3ddf3ceab65f1b4cd6cbcbaec2a622c4ef5";
        String result = "";
        try {
            HttpResponse<JsonNode> node = Unirest.get("https://translate.yandex.net/api/v1.5/tr.json/translate")
                    .queryString("key", KEY)
                    .queryString("lang", "en-" + lang)
                    .queryString("text", text)
                    .asJson();

            Language ret = new Gson().fromJson(node.getBody().toString(), Language.class);
            result = ret.getText()[0];
            log.debug("Language result: {}", ret.getText()[0]);
        } catch (Exception e) {
            log.error("Error in translator: {}", e);
        }

        return result;
    }

    public static void internationalUserProfile(DcUsersEntity info, long idUser) {

        Set<String> codes = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.TRANSLATION + ":list");
        String valConcat = info.city + "," + info.country;

        RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "location:en", valConcat);
        final String val = valConcat.replace(" ", "");

        codes.forEach(cod -> {
            String val_tr = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.TRANSLATION + val, "location" + ":" + cod);
            if (val_tr == null) {
                try {
                    String translateProfile = initTranslate(valConcat, cod);
                    if (translateProfile != null && !translateProfile.isEmpty()) {
                        RMemoryAPI.getInstance()
                                .pushHashToMemory(Constants.TRANSLATION + val, "location" + ":" + cod, translateProfile);
                        RMemoryAPI.getInstance()
                                .pushHashToMemory(Constants.USER_KEY + idUser, "location" + ":" + cod, translateProfile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                RMemoryAPI.getInstance().pushHashToMemory(Constants.USER_KEY + idUser, "location" + ":" + cod, val_tr);
            }

        });

    }
}
