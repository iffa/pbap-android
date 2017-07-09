package xyz.santeri.pbap.ui.finish;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseFragment;
import xyz.santeri.pbap.ui.transfer.TransferCompletedEvent;

/**
 * @author Santeri 'iffa'
 */
public class FinishFragment extends BaseFragment implements FinishView {
    public static FinishFragment newInstance() {
        return new FinishFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finish, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onActive(TransferCompletedEvent event) {
    }
}
