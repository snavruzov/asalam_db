package com.dgtz.db.api.features;

import com.dgtz.db.api.enums.EnumCompressingState;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/22/13
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public final class ConverterUtils {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConverterUtils.class);


    public ConverterUtils() {

    }

    public static byte[] parseToByteArray(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
        } catch (IOException ex) {
            log.error("ERROR IN DB API ", ex);
        }

        return bos.toByteArray();
    }

    public static String parseStatusToText(int progress) {
        String status = "NOT FOUND";
        switch (progress){
            case 0:status = EnumCompressingState.COMPLETED.value;break;
            case 1:status = EnumCompressingState.INPROCESS.value;break;
            case 2:status = EnumCompressingState.BROKEN.value;break;
            case 4:status = EnumCompressingState.TOOLONG.value;break;
        }

        return status;
    }

}
