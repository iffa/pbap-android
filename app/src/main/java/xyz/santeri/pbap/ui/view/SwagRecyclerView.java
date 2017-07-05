package xyz.santeri.pbap.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * RecyclerView with support for showing another View when adapter is empty.
 *
 * @author Santeri 'iffa'
 */
public class SwagRecyclerView extends RecyclerView {
    private View emptyView;

    private final AdapterDataObserver emptyObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkState();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkState();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            checkState();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkState();
        }

        private void checkState() {
            RecyclerView.Adapter adapter = getAdapter();
            if (adapter != null && emptyView != null) {
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    SwagRecyclerView.this.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    SwagRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    public SwagRecyclerView(Context context) {
        super(context);
    }

    public SwagRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwagRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}