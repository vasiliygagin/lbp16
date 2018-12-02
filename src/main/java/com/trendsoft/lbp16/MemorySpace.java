package com.trendsoft.lbp16;

public enum MemorySpace {

    HOSTMOT2(0), ETHERNET_CHIP(1), ETHERNET_EEPROM(2), FPGA_FLASH(3), LBP16_RW(6), LBP16_RO(7);

    public final int code;

    MemorySpace(int code) {
        this.code = code;
    }
}
