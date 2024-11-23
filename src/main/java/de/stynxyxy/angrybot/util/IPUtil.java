package de.stynxyxy.angrybot.util;

import java.net.InetAddress;

public class IPUtil {
    public static String getBlankIpByInet(InetAddress address) {
        return address.toString().replace("/","");
    }
}
