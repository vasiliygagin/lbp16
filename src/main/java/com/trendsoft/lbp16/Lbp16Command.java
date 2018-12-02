package com.trendsoft.lbp16;

public class Lbp16Command {

    public static final int WRITE_BIT = 0x8000;
    public static final int ADDRESS_BIT = 0x4000;
    public static final int INFO_AREA_BIT = 0x2000;
    public static final int MEMORY_SPACE_SHIFT = 10;
    public static final int MEMORY_SPACE_BITS = 0x1C00;
    public static final int TRANSFER_ELEMENT_SIZE_SHIFT = 8;
    public static final int TRANSFER_ELEMENT_SIZE_BITS = 0x300;
    public static final int INCREMENT_ADDRESS_BIT = 0x80;
    public static final int TRANSFER_UNIT_COUNT_SHIFT = 0;
    public static final int TRANSFER_UNIT_COUNT_BITS = 0x7F;

    private boolean write = false;
    private boolean hasAddress = false;
    private boolean infoArea = false;
    private MemorySpace memorySpace = MemorySpace.HOSTMOT2; // 0-7
    private AccessType transferElementSize = AccessType.ACCESS_8_BIT; // 0=8,1=16,2=32,3=64
    private boolean incrementAddress;
    private int transferUnitCount; // 7bits

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isHasAddress() {
        return hasAddress;
    }

    public void setHasAddress(boolean hasAddress) {
        this.hasAddress = hasAddress;
    }

    public boolean isInfoArea() {
        return infoArea;
    }

    public void setInfoArea(boolean infoArea) {
        this.infoArea = infoArea;
    }

    public MemorySpace getMemorySpace() {
        return memorySpace;
    }

    public void setMemorySpace(MemorySpace memorySpace) {
        this.memorySpace = memorySpace;
    }

    public AccessType getTransferElementSize() {
        return transferElementSize;
    }

    public void setTransferElementSize(AccessType transferElementSize) {
        this.transferElementSize = transferElementSize;
    }

    public boolean isIncrementAddress() {
        return incrementAddress;
    }

    public void setIncrementAddress(boolean incrementAddress) {
        this.incrementAddress = incrementAddress;
    }

    public int getTransferUnitCount() {
        return transferUnitCount;
    }

    public void setTransferUnitCount(int transferUnitCount) {
        if (transferUnitCount < 0 || transferUnitCount >= 0x7F) {
            throw new IllegalArgumentException("Transfer Unit count " + transferUnitCount);
        }
        this.transferUnitCount = transferUnitCount;
    }

    public int to16Bit() {
        int result = 0;
        if (write) {
            result |= WRITE_BIT;
        }
        if (hasAddress) {
            result |= ADDRESS_BIT;
        }
        if (infoArea) {
            result |= INFO_AREA_BIT;
        }
        result |= (memorySpace.code << MEMORY_SPACE_SHIFT) & MEMORY_SPACE_BITS;
        result |= (transferElementSize.code << TRANSFER_ELEMENT_SIZE_SHIFT) & TRANSFER_ELEMENT_SIZE_BITS;
        if (incrementAddress) {
            result |= INCREMENT_ADDRESS_BIT;
        }
        result |= (transferUnitCount << TRANSFER_UNIT_COUNT_SHIFT) & TRANSFER_UNIT_COUNT_BITS;
        return result;
    }
}
