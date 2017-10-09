package com.company.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Utils {

    public static String getIPStringForMac() {
        final String[] localhost = new String[1];
        Runnable getIp = () -> {
            try {
                localhost[0] = InetAddress.getLocalHost().toString();
                localhost[0] = localhost[0].substring(localhost[0].lastIndexOf("/") + 1, localhost[0].length());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        };
        getIp.run();
        return localhost[0];
    }


    public static String getBroadCastIP(String ipAddress) {
        return ipAddress.substring(0, ipAddress.lastIndexOf(".")) + "";

    }


    public static void clearConsole() {
        String value = "\n\r";
        for (int i = 0; i < 5; ++i) {
            value = value + value;
            System.out.printf(value);
        }
    }
}
