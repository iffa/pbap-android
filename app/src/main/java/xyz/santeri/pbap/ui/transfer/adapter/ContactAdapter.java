package xyz.santeri.pbap.ui.transfer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.vcard.VCardEntry;

import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseAdapter;
import xyz.santeri.pbap.ui.base.BaseViewHolder;

/**
 * @author Santeri Elo
 */
public class ContactAdapter extends BaseAdapter<VCardEntry> {
    @Override
    public BaseViewHolder<VCardEntry> onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<VCardEntry> holder, int position) {
        holder.bind(this, getItemAt(position));

        ((ContactViewHolder) holder).listItem.setOnClickListener(v
                -> getItemClickSubject().onNext(getItemAt(position)));
    }
}
