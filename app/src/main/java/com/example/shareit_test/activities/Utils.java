package com.example.shareit_test.activities;

import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Utils {

    public static String getMyIP(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String myaddr = inetAddress.getHostAddress();
                        boolean isIPv4 = myaddr.indexOf(':')<0;
                        if (!isIPv4) {
                            continue;
                        }else {
                            return myaddr;
                        }

                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Utility", ex.toString());
        }
        return null;
    }
}
