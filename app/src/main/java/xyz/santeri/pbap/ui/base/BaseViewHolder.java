package xyz.santeri.pbap.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * @author Santeri Elo
 */
public abstract class BaseViewHolder<M> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(RecyclerView.Adapter adapter, M item);
}