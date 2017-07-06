package xyz.santeri.pbap.ui.device.adapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.lucasurbas.listitemview.ListItemView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseViewHolder;

/**
 * @author Santeri 'iffa'
 */
public class DeviceViewHolder extends BaseViewHolder<BluetoothDevice> {
    @BindView(R.id.item_device)
    ListItemView listItem;

    DeviceViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(RecyclerView.Adapter adapter, BluetoothDevice item) {
        listItem.setTitle(item.getName());
        listItem.setSubtitle(item.getAddress());
        switch (item.getBluetoothClass().getMajorDeviceClass()) {
            // TODO: Day/night theme compatible icons
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                ((ImageView) listItem.findViewById(R.id.icon_view))
                        .setImageResource(R.drawable.ic_headset_24dp);
                break;
            case BluetoothClass.Device.Major.COMPUTER:
                ((ImageView) listItem.findViewById(R.id.icon_view))
                        .setImageResource(R.drawable.ic_computer_24dp);
                break;
            case BluetoothClass.Device.Major.PHONE:
                ((ImageView) listItem.findViewById(R.id.icon_view))
                        .setImageResource(R.drawable.ic_smartphone_24dp);
                break;
            default:
                ((ImageView) listItem.findViewById(R.id.icon_view))
                        .setImageResource(R.drawable.ic_devices_other_24dp);
                break;
        }
    }
}
