package com.trendsoft.lbp16;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;

public class Main {

    public static void main(String[] args) throws Exception {

        MesaCard mesaCard = new MesaCard();
        mesaCard.setDebugPrint(false);

//        readInfoAreas(mesaCard);

//        readArea(mesaCard, MemorySpace.HOSTMOT2, "B:\\CNC\\7i96\\hostmod2.dmp");
//        readArea(mesaCard, MemorySpace.ETHERNET_CHIP, "B:\\CNC\\7i96\\ether-chip.dmp");
//        readArea(mesaCard, MemorySpace.ETHERNET_EEPROM, "B:\\CNC\\7i96\\ether-eeprom.dmp");
        for (int i = 0; i < 32; ++i) {
            readEeprom(mesaCard, i, "B:\\CNC\\7i96\\eeprom-" + i + ".dmp");
        }
        // byte[] buffer = mesaCard.readSpace(MemorySpace.HOSTMOT2,
        // AccessType.ACCESS_32_BIT, 0x400, 5*4);
//        BufferUtils.printBufferContent(buffer, 0, buffer.length);
    }

    private static void readInfoAreas(MesaCard mesaCard) throws IOException {
        for (MemorySpace memorySpace : MemorySpace.values()) {
            InfoArea infoArea = mesaCard.readInfoArea(memorySpace);
            System.out.println(infoArea);
        }
    }

    private static void readArea(MesaCard mesaCard, MemorySpace memorySpace, String outFileName) throws IOException {

        InfoArea infoArea = mesaCard.readInfoArea(memorySpace);
        int areaSize = 1 << infoArea.getAddressRange();
        int step = 0x40;
        AccessType accessType = infoArea.getLArgestAccessType();
        try ( //
                FileOutputStream fileOS = new FileOutputStream(outFileName);
                BufferedOutputStream bufferedOS = new BufferedOutputStream(fileOS);) {
            for (int i = 0; i < areaSize;) {
                byte[] buffer = mesaCard.readSpace(memorySpace, accessType, i, step);
                bufferedOS.write(buffer);
                i += buffer.length;
            }
        }
    }

    private static void readEeprom(MesaCard mesaCard, int sector, String outFileName) throws IOException {
        try ( //
                FileOutputStream fileOS = new FileOutputStream(outFileName);
                BufferedOutputStream bufferedOS = new BufferedOutputStream(fileOS);) {
            byte[] buffer = mesaCard.readEeprom(sector);
            bufferedOS.write(buffer);
        }
    }
}
