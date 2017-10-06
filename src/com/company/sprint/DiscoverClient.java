package com.company.sprint;

import com.company.models.User;
import com.company.utils.JsonConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DiscoverClient {

    public DiscoverClient() {

    }


    public void sendAware(User user, String ip) {
        String sendAwareRequests = JsonConstants.getSendAwareJsonFromUser(user);
        InetAddress[] group = null;
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            for (int i = 1; i < 255; i++) {
                String tempIp = ip.substring(0, ip.lastIndexOf(".") + 1) + i;
                InetAddress tempAddress = InetAddress.getByName(tempIp);
                byte[] message = sendAwareRequests.getBytes();
                DatagramPacket packet = new DatagramPacket(message, message.length, tempAddress, 9776);
                datagramSocket.send(packet);
                System.out.println(String.format("Sending request to : %s", tempIp));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
