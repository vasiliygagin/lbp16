package com.trendsoft.lbp16;

public enum AreaType {

    REGISTER(1), MEMORY(2), EEPROM(14), FLASH(15);

    public final int code;

    AreaType(int code) {
        this.code = code;
    }

    public static AreaType fromCode(int code) {
        for (AreaType areaType : values()) {
            if (areaType.code == code) {
                return areaType;
            }
        }
        throw new IllegalArgumentException("Invalid AreaType code " + code);
    }
}
