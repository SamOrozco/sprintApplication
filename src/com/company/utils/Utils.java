package com.company.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Utils {

    public static String getIPStringForMac() {
        String localhost = null;
        try {
            localhost = InetAddress.getLocalHost().toString();
            return localhost.substring(localhost.lastIndexOf("/") + 1, localhost.length());
        } catch (UnknownHostException e) {
            return "";
        }
    }


    public static String getBroadCastIP(String ipAddress) throws UnknownHostException {
        return ipAddress.substring(0, ipAddress.lastIndexOf(".")) + ".255";

    }


    public static void clearConsole() {
        String value = ".\n\r";
        for (int i = 0; i < 5; ++i) {
            value = value + value;
            System.out.printf(value);
        }
    }
}
