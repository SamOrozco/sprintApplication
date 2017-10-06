package com.company.sprint;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.*;

public class SprintClient {

    public SprintClient() throws IOException {
        DatagramSocket socket = new DatagramSocket(9776);
        byte[] value = new byte[256];
        DatagramPacket datagramPacket = new DatagramPacket(value, value.length);
        socket.receive(datagramPacket);
        System.out.printf(datagramPacket.toString());
        System.out.println("got request");
    }
}
