package com.dgtz.db.api.enums;

/**
 * Created by sardor on 1/2/14.
 */
public enum EnumAggregations {

    RECOMMENDED(4),
    MOST_LIKED(1),
    MOST_VIEWED(2),
    REC_VIEWED(7),
    LIVE_VIEWED(8),
    MOST_COMMENTED(3),
    LAST(0),
    NEARBY(5),
    POPULAR(6);


    public int value;

    EnumAggregations(int value) {
        this.value = value;
    }

    public static EnumAggregations getEnumValByID(int value) {
        EnumAggregations aggregations = null;
        if (value == 0) {
            aggregations = EnumAggregations.LAST;
        } else if (value == 3) {
            aggregations = EnumAggregations.MOST_COMMENTED;
        } else if (value == 2) {
            aggregations = EnumAggregations.MOST_VIEWED;
        } else if (value == 1) {
            aggregations = EnumAggregations.MOST_LIKED;
        } else if (value == 4) {
            aggregations = EnumAggregations.RECOMMENDED;
        }
        else if (value == 6) {
            aggregations = EnumAggregations.POPULAR;
        }

        return aggregations;
    }


}
