package com.dgtz.db.api.features;

import java.util.Calendar;
import java.util.Date;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 2/15/14
 */
public class DateUtils {

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static Date currentDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.getTime();
    }
}
