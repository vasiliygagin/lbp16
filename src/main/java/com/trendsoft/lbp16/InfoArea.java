package com.trendsoft.lbp16;

import static com.trendsoft.lbp16.BufferUtils.readShort;
import static com.trendsoft.lbp16.BufferUtils.readString;

import java.util.List;

public class InfoArea {

    private static final int INFO_AREA_RESPONSE_COOKIE = 0x5A00;

    public static final int COOKIE_OFFSET = 0x0000; // = 0X5A0N WHERE N = ADDRESS SPACE 0..7
    public static final int MEMSIZES_OFFSET = 0x0002;
    public static final int MEMRANGES_OFFSET = 0x0004;
    public static final int ADDRESS_POINTER_OFFSET = 0x0006;
    public static final int SPACENAME_OFFSET = 0x0008;

    public static final int MEMSIZES_WRITABLE_BIT = 0x8000;
    public static final int MEMSIZES_TYPE_SHIFT = 8;
    public static final int MEMSIZES_TYPE_BITS = 0x7F00;
    public static final int MEMSIZES_ACCESS_TYPE_SHIFT = 0;
    public static final int MEMSIZES_ACCESS_TYPE_BITS = 0x000F;

    public static final int MEMRANGES_ERASE_BLOCK_SIZE_SHIFT = 11;
    public static final int MEMRANGES_ERASE_BLOCK_SIZE_BITS = 0x1800;
    public static final int MEMRANGES_PAGE_SIZE_SHIFT = 6;
    public static final int MEMRANGES_PAGE_SIZE_BITS = 0x07C0;
    public static final int MEMRANGES_PAGE_ADDRESS_RANGE_SHIFT = 0;
    public static final int MEMRANGES_PAGE_ADDRESS_RANGE_BITS = 0x003F;

    private String spaceName;
    private boolean writable;
    private AreaType areaType;
    private int accessTypes;

    private int eraseBlockSize;
    private int pageSize;
    private int addressRange;

    private int addressPointer;

    public InfoArea(MemorySpace memorySpace, byte[] responseBuffer) {

        int responseCookie = readShort(responseBuffer, COOKIE_OFFSET);
        if (responseCookie != (INFO_AREA_RESPONSE_COOKIE | memorySpace.code)) {
            throw new IllegalStateException("Invalid Response, Info  Area cookie " + responseCookie);
        }

        int memSizes = readShort(responseBuffer, MEMSIZES_OFFSET);
        writable = (memSizes & MEMSIZES_WRITABLE_BIT) != 0;
        areaType = AreaType.fromCode((memSizes & MEMSIZES_TYPE_BITS) >>> MEMSIZES_TYPE_SHIFT);
        accessTypes = (memSizes & MEMSIZES_ACCESS_TYPE_BITS) >>> MEMSIZES_ACCESS_TYPE_SHIFT;

        int memRanges = readShort(responseBuffer, MEMRANGES_OFFSET);
        eraseBlockSize = (memRanges & MEMRANGES_ERASE_BLOCK_SIZE_BITS) >>> MEMRANGES_ERASE_BLOCK_SIZE_SHIFT;
        pageSize = (memRanges & MEMRANGES_PAGE_SIZE_BITS) >>> MEMRANGES_PAGE_SIZE_SHIFT;
        addressRange = (memRanges & MEMRANGES_PAGE_ADDRESS_RANGE_BITS) >>> MEMRANGES_PAGE_ADDRESS_RANGE_SHIFT;

        addressPointer = readShort(responseBuffer, ADDRESS_POINTER_OFFSET);
        spaceName = readString(responseBuffer, SPACENAME_OFFSET, 8);
    }

    public String getSpaceName() {
        return spaceName;
    }

    public boolean isWritable() {
        return writable;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public int getAccessTypes() {
        return accessTypes;
    }

    public int getEraseBlockSize() {
        return eraseBlockSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getAddressRange() {
        return addressRange;
    }

    public int getAddressPointer() {
        return addressPointer;
    }

    public AccessType getLArgestAccessType() {
        List<AccessType> accessTypes = AccessType.fromBitmask(this.accessTypes);
        return accessTypes.get(accessTypes.size() - 1);
    }

    @Override
    public String toString() {
        return "InfoArea: spaceName=" + spaceName + ", writable=" + writable + ", areaType=" + areaType
                + ", accessTypes=" + AccessType.fromBitmask(accessTypes) + ", eraseBlockSize=" + eraseBlockSize
                + ", pageSize=" + pageSize + ", addressRange=" + addressRange + ", addressPointer=0x"
                + Integer.toHexString(addressPointer);
    }
}
