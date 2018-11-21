package com.dgtz.db.api.features;

import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;

import java.util.Set;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 12/1/14
 */
public class MediaTagTokenizer {

    public MediaTagTokenizer() {
    }

    public static void storeTags(Set<String> tags, Long idMedia) {
        for (String tag : tags) {
            RMemoryAPI.getInstance()
                    .pushLSetElemToMemory(Constants.MEDIA_KEY + "tag:" + tag, idMedia + "");
            RMemoryAPI.getInstance()
                    .pushLSetElemToMemory(Constants.MEDIA_KEY + "tags", tag);

        }
    }

    public static void removeIdMediaFromTag(Set<String> tags, Long idMedia) {
        for (String tag : tags) {
            RMemoryAPI.getInstance()
                    .delFromLSetElem(Constants.MEDIA_KEY + "tag:" + tag, idMedia + "");
            RMemoryAPI.getInstance()
                    .delFromLSetElem(Constants.MEDIA_KEY + "tags", tag);

        }
    }
}
