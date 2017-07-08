package xyz.santeri.pbap.ui.view;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.DrawableRes;

import xyz.santeri.pbap.R;

/**
 * @author Santeri Elo
 */
public class DeviceItemUtil {
    @DrawableRes
    public static int getIconForDevice(BluetoothDevice device) {
        @DrawableRes int icon;
        switch (device.getBluetoothClass().getMajorDeviceClass()) {
            // TODO: Day/night theme compatible icons
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                icon = R.drawable.ic_headset_24dp;
                break;
            case BluetoothClass.Device.Major.COMPUTER:
                icon = R.drawable.ic_computer_24dp;
                break;
            case BluetoothClass.Device.Major.PHONE:
                icon = +R.drawable.ic_smartphone_24dp;
                break;
            default:
                icon = R.drawable.ic_devices_other_24dp;
                break;
        }

        // Second switch statement with more fine-tuning to detect e.g. TVs
        // It is up to device manufacturers to get this right
        switch (device.getBluetoothClass().getDeviceClass()) {
            case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER:
                icon = R.drawable.ic_tv_24dp;
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET:
                // TODO: Icon
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO:
                // TODO: Icon
                break;
        }

        return icon;
    }
}
