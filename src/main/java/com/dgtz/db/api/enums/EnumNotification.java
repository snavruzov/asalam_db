package com.dgtz.db.api.enums;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 2/28/14
 */
public enum EnumNotification {

    WANT_TO_JOIN(1),
    NEW_MEDIA(2),
    SUGGEST_TO_JOIN(3),
    LIVE_STARTED(6),
    LIKED(4),
    COMMENTED(0),
    CHANNEL_REMOVE(7),
    YOU_ARE_JOINED(22),
    CONTACT_US(9),
    MEDIA_DONE(10),
    MEDIA_WRONG(11),
    USER_ARE_JOINED(12),
    PASSWORD_RESTORE(13),
    ACCOUNT_CONFIRM(14),
    EMAIL_CONFIRM(15),
    NEWSFEED(16),
    PROMO_PUSH(17),
    CHANNEL_UPDATE(18),
    SCHEDULED_EVENT(19),
    START_FOLLOWED(20),
    SUB_CHANNEL(21),
    DEBATE_INVT(8),
    PUBLISH_EVENT(23),
    INBOX_MSG(24);


    public int value;

    EnumNotification(int value) {
        this.value = value;
    }

    public static Integer[] getList() {
        return new Integer[]{1, 2, 3, 4, 5, 6};
    }
}
