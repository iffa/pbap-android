package xyz.santeri.pbap.ui.device;

import android.bluetooth.BluetoothDevice;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import java.util.List;

import rx.Observable;

/**
 * @author Santeri 'iffa'
 */
interface DeviceView extends TiView {
    Observable<BluetoothDevice> onDeviceClicked();

    @CallOnMainThread
    void onDeviceFound(BluetoothDevice device);

    @CallOnMainThread
    void addPairedDevices(List<BluetoothDevice> devices);

    @CallOnMainThread
    void showProgressBar();

    @CallOnMainThread
    void onDiscoveryFinished();
}
