package com.dgtz.db.api.enums;

/**
 * Created by sardor on 12/9/15.
 */
public enum EnumChannelTypes {
    PUBLIC(0),
    PRIVATE(1),
    HIDDEN(2);

    public int value;

    EnumChannelTypes(int value) {
        this.value = value;
    }
}
