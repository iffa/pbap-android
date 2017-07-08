package xyz.santeri.pbap.ui.transfer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.vcard.VCardEntry;
import com.lucasurbas.listitemview.ListItemView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseViewHolder;

/**
 * @author Santeri Elo
 */
public class ContactViewHolder extends BaseViewHolder<VCardEntry> {
    @BindView(R.id.item_contact)
    ListItemView listItem;

    ContactViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(RecyclerView.Adapter adapter, VCardEntry item) {
        listItem.setTitle(item.getDisplayName());

        if (item.getPhoneList() != null && !item.getPhoneList().isEmpty()) {
            String number = item.getPhoneList().get(0).getNumber();
            listItem.setSubtitle(number);
        } else {
            listItem.setSubtitle(listItem.getResources().getString(R.string.tv_contact_phone_empty));
        }
    }
}
