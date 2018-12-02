package com.trendsoft.lbp16;

public class BufferUtils {

    private BufferUtils() {
    }

    public static int readShort(byte[] buffer, int offset) {
        return buffer[offset] & 0xFF | ((buffer[offset + 1] & 0xFF) << 8);
    }

    public static String readString(byte[] buffer, int offset, int maxLength) {
        StringBuilder sb = new StringBuilder(maxLength);
        for (int i = 0; i < maxLength; ++i) {
            byte b = buffer[offset++];
            if (b == 0) {
                break;
            }
            sb.append((char) b);
        }
        return sb.toString();
    }

    public static void printBufferContent(byte[] buffer, int offset, int length) {
        int end = offset + length;
        while (offset < end) {
            for (int i = 15; i >= 0; --i) {
                if (offset >= end) {
                    break;
                }
                byte b = buffer[offset++];
                System.out.print(Integer.toHexString(b & 0xff) + ' ');
            }
            System.out.println();
        }
    }

    public static void printBufferContent(byte[] buffer) {
        printBufferContent(buffer, 0, buffer.length);
    }

    public static void writeInt(byte[] buffer, int offset, int value) {
        buffer[offset] = (byte) (value & 0xFF);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xFF);
        buffer[offset + 2] = (byte) ((value >> 16) & 0xFF);
        buffer[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }
}
