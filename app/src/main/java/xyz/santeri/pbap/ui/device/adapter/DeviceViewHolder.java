package xyz.santeri.pbap.ui.device.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
    }
}
