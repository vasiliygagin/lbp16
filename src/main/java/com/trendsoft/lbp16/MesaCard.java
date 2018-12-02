package com.trendsoft.lbp16;

import static com.trendsoft.lbp16.BufferUtils.printBufferContent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.EnumMap;

public class MesaCard {

    private static final String DEFAULT_IP_ADDRESS = "192.168.1.121";
    private static final int BOARD_PORT = 27181;

    private boolean debugPrint;
    private DatagramSocket socket;
    private InetAddress inetAddress;

    public MesaCard() throws UnknownHostException, SocketException {
        this(DEFAULT_IP_ADDRESS);
    }

    public MesaCard(String ipAddress) throws UnknownHostException, SocketException {
        inetAddress = InetAddress.getByName(ipAddress);
        socket = new DatagramSocket();
    }

    public void setDebugPrint(boolean debugPrint) {
        this.debugPrint = debugPrint;
    }

    public void sendCommand(Lbp16Command command) throws IOException {
        command.setHasAddress(false);

        int commandCode = command.to16Bit();

        byte[] buf = new byte[2];
        buf[0] = (byte) commandCode;
        buf[1] = (byte) ((commandCode & 0xFF00) >> 8);

        if (debugPrint) {
            System.out.println("Request:");
            printBufferContent(buf, 0, buf.length);
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, inetAddress, BOARD_PORT);
        socket.send(packet);
    }

    public void sendCommand(Lbp16Command command, int address) throws IOException {
        command.setHasAddress(true);

        int commandCode = command.to16Bit();

        byte[] buf = new byte[4];
        buf[0] = (byte) commandCode;
        buf[1] = (byte) ((commandCode & 0xFF00) >> 8);
        buf[2] = (byte) address;
        buf[3] = (byte) ((address & 0xFF00) >> 8);

        if (debugPrint) {
            System.out.println("Request:");
            printBufferContent(buf, 0, 4);
        }

        DatagramPacket packet = new DatagramPacket(buf, 4, inetAddress, BOARD_PORT);
        socket.send(packet);
    }

    public int readResponse(byte[] buffer) throws IOException {
        return readResponse(buffer, 0, buffer.length);
    }

    public int readResponse(byte[] buffer, int offset, int length) throws IOException {

        DatagramPacket packet = new DatagramPacket(buffer, offset, length);
        socket.receive(packet);

        if (debugPrint) {
            System.out.println("Response:");
            printBufferContent(buffer, offset, length);
        }
        return packet.getLength();
    }

    public InfoArea readInfoArea(MemorySpace memorySpace) throws IOException {

        Lbp16Command command = new Lbp16Command();
        command.setWrite(false);
        command.setHasAddress(true);
        command.setInfoArea(true);
        command.setMemorySpace(memorySpace);
        command.setTransferElementSize(AccessType.ACCESS_16_BIT);
        command.setIncrementAddress(true);
        command.setTransferUnitCount(8);

        byte[] responseBuffer = new byte[16];
        sendCommand(command, 0);
        int length = readResponse(responseBuffer);
        if (length != 16) {
            throw new IllegalStateException("Response size " + length);
        }

        return new InfoArea(memorySpace, responseBuffer);
    }

    public byte[] readSpace(MemorySpace memorySpace, AccessType accessType, int address, int size) throws IOException {

        int bytesInUnit = accessType.bits / 8;
        int units = (size - 1) / bytesInUnit + 1;

        Lbp16Command command = new Lbp16Command();
        command.setWrite(false);
        command.setHasAddress(true);
        command.setInfoArea(false);
        command.setMemorySpace(memorySpace);
        command.setTransferElementSize(accessType);
        command.setIncrementAddress(true);
        command.setTransferUnitCount(units);

        byte[] responseBuffer = new byte[units * bytesInUnit];
        sendCommand(command, address);
        int length = readResponse(responseBuffer);
        if (length != responseBuffer.length) {
            throw new IllegalStateException("Response size " + length);
        }

        return responseBuffer;
    }

    public byte[] readEeprom(int sector) throws IOException {
        if (sector < 0 || sector >= 32) {
            throw new IllegalArgumentException("Sector " + sector);
        }

        byte[] buffer = new byte[0x10000];

        writeEepromAddress(sector);
        readEepromData(buffer);
        return buffer;
    }

    public void readEepromData(byte[] buffer) throws IOException {

        Lbp16Command command = new Lbp16Command();
        command.setWrite(false);
        command.setHasAddress(true);
        command.setInfoArea(false);
        command.setMemorySpace(MemorySpace.FPGA_FLASH);
        command.setTransferElementSize(AccessType.ACCESS_32_BIT);
        command.setIncrementAddress(false);
        command.setTransferUnitCount(0x40);

        sendCommand(command, 0x0004);
        readResponse(buffer, 0, 0x100);

        command.setHasAddress(false);
        for (int i = 0x100; i < 0x10000; i += 0x100) {
            sendCommand(command);
            readResponse(buffer, i, 0x100);
        }
    }

    public void writeEepromAddress(int sector) throws IOException {

        Lbp16Command command = new Lbp16Command();
        command.setWrite(true);
        command.setHasAddress(true);
        command.setInfoArea(false);
        command.setMemorySpace(MemorySpace.FPGA_FLASH);
        command.setTransferElementSize(AccessType.ACCESS_32_BIT);
        command.setIncrementAddress(false);
        command.setTransferUnitCount(1);

        byte[] dataBuffer = new byte[4];
        BufferUtils.writeInt(dataBuffer, 0, 0x10000 * sector);
        sendCommand(command, 0, dataBuffer);
    }

    public void sendCommand(Lbp16Command command, int address, byte[] dataBuffer) throws IOException {
        command.setHasAddress(true);

        int commandCode = command.to16Bit();

        byte[] buf = new byte[4 + dataBuffer.length];
        buf[0] = (byte) commandCode;
        buf[1] = (byte) ((commandCode & 0xFF00) >> 8);
        buf[2] = (byte) address;
        buf[3] = (byte) ((address & 0xFF00) >> 8);
        System.arraycopy(dataBuffer, 0, buf, 4, dataBuffer.length);

        if (debugPrint) {
            System.out.println("Request:");
            printBufferContent(buf);
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, inetAddress, BOARD_PORT);
        socket.send(packet);
    }
}
