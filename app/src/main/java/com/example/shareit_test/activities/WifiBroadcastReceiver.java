package com.example.shareit_test.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.shareit_test.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import static android.content.Context.WIFI_SERVICE;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private SendActivity activity;
    private String ownerIP;

    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                 SendActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
                activity.resetData();
            }
            Log.d("SendActivity.TAG", "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manager.requestPeers(channel, (WifiP2pManager.PeerListListener) activity.getFragmentManager()
                        .findFragmentById(R.id.avail_list_frag));

                /*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    manager.requestNetworkInfo(channel, new WifiP2pManager.NetworkInfoListener() {
                        @Override
                        public void onNetworkInfoAvailable(@NonNull NetworkInfo networkInfo) {
                            Log.d("Send WIFI BR",networkInfo.toString());
                            activity.makeToast(networkInfo.toString());
                        }
                    });
                }

                 */

            }
            Log.d("Send Wifi BR", "P2P peers changed");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }

            final NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            assert networkInfo != null;
            if (networkInfo.isConnected()) {
                //check if Group Owner(then become server) else client
                // we are connected with the other device, request connection

                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

                        ownerIP = wifiP2pInfo.groupOwnerAddress.getHostAddress();
                        Log.d("Owner IP:",ownerIP);
                        // info to find group owner IP
                        //check if Group Owner(then become server) else client
                        //all server/client init calls go through Send Activity
                        String myIP = Utils.getMyIP();
                        activity.makeToast("MY IP:"+myIP);

                        if(ownerIP.equals(myIP)){
                            // i am the owner
                            activity.readyServer();
                        } else {
                            // else client
                            activity.readyClient(ownerIP);
                        }
                    }
                });




            } else {
                // It's a disconnect
                activity.resetData();
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.avail_list_frag);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }
}
