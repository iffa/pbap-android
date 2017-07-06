package xyz.santeri.pbap.ui.transfer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lucasurbas.listitemview.ListItemView;

import net.grandcentrix.thirtyinch.plugin.TiFragmentPlugin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseFragment;

/**
 * @author Santeri 'iffa'
 */
public class TransferFragment extends BaseFragment implements TransferView {
    private TiFragmentPlugin<TransferPresenter, TransferView> presenterPlugin =
            new TiFragmentPlugin<>(() -> getComponent().transferPresenter());

    @BindView(R.id.tv_connecting)
    TextView connectionText;

    @BindView(R.id.item_device)
    ListItemView deviceView;

    public TransferFragment() {
        addPlugin(presenterPlugin);
    }

    public static TransferFragment newInstance() {
        return new TransferFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer, container, false);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Subscribe
    public void onActiveEvent(TransferFragmentActiveEvent event) {
        presenterPlugin.getPresenter().onActiveEvent(event.getDevice());

        deviceView.setTitle(event.getDevice().getName());
        deviceView.setSubtitle(event.getDevice().getAddress());
    }

    @Override
    public void showConnectionFailed() {
        connectionText.setText(R.string.tv_connection_error);
    }

    @Override
    public void showTransferStarted() {
        connectionText.setText(R.string.tv_transfer_started);
    }

    @Override
    public void showTransferFinished() {
        connectionText.setText(R.string.tv_transfer_finished);
    }

    @Override
    public void showTransferFailed() {
        connectionText.setText(R.string.tv_transfer_error);
    }
}
