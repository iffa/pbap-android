package xyz.santeri.pbap.ui.device.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseAdapter;
import xyz.santeri.pbap.ui.base.BaseViewHolder;

/**
 * @author Santeri 'iffa'
 */
public class DeviceAdapter extends BaseAdapter<BluetoothDevice> {

    @Override
    public BaseViewHolder<BluetoothDevice> onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);

        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<BluetoothDevice> holder, int position) {
        holder.bind(this, getItemAt(position));

        ((DeviceViewHolder) holder).listItem.setOnClickListener(v
                -> getItemClickSubject().onNext(getItemAt(position)));
    }
}
