package com.dcep.inet;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class inet {

    private final Enumeration<NetworkInterface> interfaces;
    private String Ipv4Address;  // Get own ip address.

    public inet() throws SocketException {
        this.interfaces = NetworkInterface.getNetworkInterfaces();
        this.Ipv4Address = null;
    }

    public Enumeration<NetworkInterface> getNetInterfaces() {
        return interfaces;
    }

    public String getInterfaceIP(String interfaceName) throws SocketException {
        for (InetAddress inetAddress : Collections.list(NetworkInterface.getByName(interfaceName).getInetAddresses())) {
                        this.Ipv4Address = inetAddress.getHostAddress();
        }
        return Ipv4Address;
    }


}
