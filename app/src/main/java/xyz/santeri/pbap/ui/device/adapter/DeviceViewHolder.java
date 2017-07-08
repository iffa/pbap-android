package xyz.santeri.pbap.ui.device.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.lucasurbas.listitemview.ListItemView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseViewHolder;
import xyz.santeri.pbap.ui.view.DeviceItemUtil;

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

        ((ImageView) listItem.findViewById(R.id.icon_view))
                .setImageResource(DeviceItemUtil.getIconForDevice(item));
    }
}
