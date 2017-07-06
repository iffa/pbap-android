package xyz.santeri.pbap.ui.transfer;

import android.bluetooth.BluetoothDevice;

/**
 * @author Santeri 'iffa'
 */
public class TransferFragmentActiveEvent {
    private final BluetoothDevice device;

    public TransferFragmentActiveEvent(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }
}
