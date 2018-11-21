package com.dgtz.db.api.enums;

/**
 * Digital Citizen.
 * User: Sardor Navuzov
 * Date: 3/6/14
 */
public enum EnumCompressingState {

    COMPLETED("COMPLETED"),
    INPROCESS("INPROCESS"),
    BROKEN("BROKEN"),
    TOOLONG("TOOLONG");

    public String value;

    EnumCompressingState(String value) {
        this.value = value;
    }
}
