package xyz.santeri.pbap.ui.base;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * @author Santeri Elo
 */
public abstract class BaseAdapter<M> extends RecyclerView.Adapter<BaseViewHolder<M>> {
    private List<M> items = new ArrayList<>();
    private final PublishSubject<M> onItemClickSubject = PublishSubject.create();

    protected PublishSubject<M> getItemClickSubject() {
        return onItemClickSubject;
    }

    public void addItem(M item) {
        items.add(item);
        notifyItemInserted(items.indexOf(item));
    }

    public void addItemAt(M item, int position) {
        items.add(position, item);
        notifyItemInserted(items.indexOf(item));
    }

    public void addItems(List<M> newItems) {
        items.addAll(newItems);
    }

    public void setItems(List<M> newItems) {
        notifyItemRangeRemoved(0, items.size());
        items = newItems;
        notifyItemRangeInserted(0, newItems.size());
    }

    protected M getItemAt(int position) {
        return items.get(position);
    }

    public void clear() {
        notifyItemRangeRemoved(0, items.size());
        items.clear();
    }

    public boolean contains(M item) {
        return items.contains(item);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Observable<M> onItemClicked() {
        return onItemClickSubject.asObservable();
    }
}
