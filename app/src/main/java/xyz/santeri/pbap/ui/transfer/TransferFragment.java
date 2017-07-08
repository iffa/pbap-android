package xyz.santeri.pbap.ui.transfer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.vcard.VCardEntry;
import com.lucasurbas.listitemview.ListItemView;
import com.mikepenz.itemanimators.AlphaInAnimator;

import net.grandcentrix.thirtyinch.plugin.TiFragmentPlugin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import rx.Observable;
import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseFragment;
import xyz.santeri.pbap.ui.transfer.adapter.ContactAdapter;
import xyz.santeri.pbap.ui.view.DeviceItemUtil;
import xyz.santeri.pbap.ui.view.SwagRecyclerView;

/**
 * @author Santeri 'iffa'
 */
public class TransferFragment extends BaseFragment implements TransferView {
    private TiFragmentPlugin<TransferPresenter, TransferView> presenterPlugin =
            new TiFragmentPlugin<>(() -> getComponent().transferPresenter());

    @BindView(R.id.container)
    ViewGroup container;

    @BindView(R.id.tv_transfer_status)
    TextView connectionText;

    @BindView(R.id.item_device)
    ListItemView deviceView;

    @BindView(R.id.pb_transfer)
    ProgressBar progressBar;

    @BindView(R.id.tv_contacts_empty)
    TextView emptyTextView;

    @BindView(R.id.rv_contacts)
    SwagRecyclerView recyclerView;
    private ContactAdapter adapter;

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

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter = new ContactAdapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new AlphaInAnimator());
        recyclerView.setEmptyView(emptyTextView);
    }

    @Subscribe
    public void onActiveEvent(TransferFragmentActiveEvent event) {
        presenterPlugin.getPresenter().onActiveEvent(event.getDevice());

        deviceView.setTitle(event.getDevice().getName());
        deviceView.setSubtitle(event.getDevice().getAddress());

        ((ImageView) deviceView.findViewById(R.id.icon_view))
                .setImageResource(DeviceItemUtil.getIconForDevice(event.getDevice()));
    }

    @Override
    public void showConnectionStarted() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showConnectionFailed() {
        TransitionManager.beginDelayedTransition(container);
        connectionText.setText(R.string.tv_connection_error);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showTransferFinished() {
        TransitionManager.beginDelayedTransition(container);
        connectionText.setText(R.string.tv_transfer_finished);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showTransferFailed() {
        TransitionManager.beginDelayedTransition(container);
        connectionText.setText(R.string.tv_transfer_error);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onContactsTransferred(List<VCardEntry> contacts) {
        adapter.setItems(contacts);
    }

    @Override
    public void showTransferStarted(int contactsSize) {
        TransitionManager.beginDelayedTransition(container);
        connectionText.setText(getString(R.string.tv_transfer_started, contactsSize));
    }

    @Override
    public Observable<VCardEntry> onContactClick() {
        return adapter.onItemClicked();
    }
}
