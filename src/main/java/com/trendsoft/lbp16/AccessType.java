package com.trendsoft.lbp16;

import java.util.ArrayList;
import java.util.List;

public enum AccessType {

    ACCESS_8_BIT(8,0), ACCESS_16_BIT(16,1), ACCESS_32_BIT(32,2), ACCESS_64_BIT(64,3);

    public final int bits;
    public final int code;

    AccessType(int bits, int code) {
        this.bits = bits;
        this.code = code;
    }

    public static List<AccessType> fromBitmask(int bitmask) {
        List<AccessType> result = new ArrayList<>(4);
        if ((bitmask & 0x01) != 0) {
            result.add(ACCESS_8_BIT);
        }
        if ((bitmask & 0x02) != 0) {
            result.add(ACCESS_16_BIT);
        }
        if ((bitmask & 0x04) != 0) {
            result.add(ACCESS_32_BIT);
        }
        if ((bitmask & 0x08) != 0) {
            result.add(ACCESS_64_BIT);
        }
        return result;
    }
}
