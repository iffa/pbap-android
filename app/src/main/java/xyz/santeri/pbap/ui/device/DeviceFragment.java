package xyz.santeri.pbap.ui.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.itemanimators.AlphaInAnimator;

import net.grandcentrix.thirtyinch.plugin.TiFragmentPlugin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx_activity_result.RxActivityResult;
import timber.log.Timber;
import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseFragment;
import xyz.santeri.pbap.ui.device.adapter.DeviceAdapter;
import xyz.santeri.pbap.ui.view.SwagRecyclerView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * @author Santeri 'iffa'
 */
public class DeviceFragment extends BaseFragment implements DeviceView {
    private TiFragmentPlugin<DevicePresenter, DeviceView> presenterPlugin =
            new TiFragmentPlugin<>(() -> getComponent().devicePresenter());

    @BindView(R.id.container)
    ViewGroup container;

    @BindView(R.id.pb_found)
    ProgressBar foundProgressBar;

    @BindView(R.id.tv_found_empty)
    TextView foundEmpty;

    @BindView(R.id.rv_found)
    SwagRecyclerView foundRecyclerView;

    DeviceAdapter foundAdapter;

    public static DeviceFragment newInstance() {
        return new DeviceFragment();
    }

    public DeviceFragment() {
        addPlugin(presenterPlugin);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();
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

    /**
     * TODO: Don't draw divider after last item
     */
    private void initRecyclerView() {
        foundRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        foundRecyclerView.setAdapter(foundAdapter = new DeviceAdapter());
        foundRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        foundRecyclerView.setItemAnimator(new AlphaInAnimator());
        foundRecyclerView.setEmptyView(foundEmpty);
    }

    @Override
    public Observable<BluetoothDevice> onDeviceClicked() {
        return foundAdapter.onItemClicked();
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        foundAdapter.addItem(device);
    }

    @Override
    public void addPairedDevices(List<BluetoothDevice> devices) {
        // TODO: Determine if showing already paired devices makes any sense
        /*
        for (BluetoothDevice device : devices) {
            if (foundAdapter.contains(device)) continue;

            foundAdapter.addItem(device);
        }
        */
    }

    @Override
    public void showProgressBar() {
        TransitionManager.beginDelayedTransition(container);
        foundProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDiscoveryFinished() {
        TransitionManager.beginDelayedTransition(container);
        foundProgressBar.setVisibility(View.GONE);
        Snackbar discoverySnack = Snackbar.make(foundRecyclerView, R.string.sb_discovery_finished, Snackbar.LENGTH_INDEFINITE);
        discoverySnack.setAction(R.string.action_retry, v -> {
            // TODO: Fix buggy behavior of retry
            foundAdapter.clear();
            presenterPlugin.getPresenter().onBluetoothEnabled();
            discoverySnack.dismiss();
        });
        discoverySnack.show();
    }

    @Subscribe
    public void onActiveEvent(DeviceFragmentActiveEvent event) {
        Timber.d("DeviceFragment is active");

        if (presenterPlugin.getPresenter().shouldEnableBluetooth()) {
            Timber.d("Enabling Bluetooth before proceeding");
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            RxActivityResult.on(this).startIntent(enableBluetooth)
                    .subscribe(result -> {
                        Timber.d("Request to enable Bluetooth, result %s", result.resultCode());

                        if (result.resultCode() == RESULT_OK) {
                            result.targetUI().presenterPlugin.getPresenter().onBluetoothEnabled();
                        } else if (result.resultCode() == RESULT_CANCELED) {
                            result.targetUI().presenterPlugin.getPresenter().onRequestCanceled();
                        }
                    });
        } else {
            Timber.d("Bluetooth already enabled, continuing");
            presenterPlugin.getPresenter().onBluetoothEnabled();
        }
    }
}
