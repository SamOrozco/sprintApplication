package com.company.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Utils {

    public static String getIPStringForMac() throws UnknownHostException {
        String localhost = InetAddress.getLocalHost().toString();
        return localhost.substring(localhost.lastIndexOf("/") + 1, localhost.length());
    }


    public static String getBroadCastIP(String ipAddress) throws UnknownHostException {
        return ipAddress.substring(0, ipAddress.lastIndexOf(".")) + ".255";

    }
}
