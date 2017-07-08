package xyz.santeri.pbap.ui.device;

import android.bluetooth.BluetoothClass;
import android.support.annotation.NonNull;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xyz.santeri.pbap.bluetooth.BluetoothManager;
import xyz.santeri.pbap.bluetooth.DiscoveryFinishedEvent;
import xyz.santeri.pbap.ui.transfer.TransferFragmentActiveEvent;
import xyz.santeri.pbap.util.RxUtil;

/**
 * @author Santeri 'iffa'
 */
public class DevicePresenter extends TiPresenter<DeviceView> {
    private final RxTiPresenterSubscriptionHandler rxHelper = new RxTiPresenterSubscriptionHandler(this);
    private final RxUtil rxUtil;
    private final BluetoothManager bluetoothManager;
    private List<String> foundAddresses = new ArrayList<>();

    @Inject
    DevicePresenter(RxUtil rxUtil, BluetoothManager bluetoothManager) {
        this.rxUtil = rxUtil;
        this.bluetoothManager = bluetoothManager;
    }

    @Override
    protected void onAttachView(@NonNull DeviceView view) {
        super.onAttachView(view);

        rxHelper.manageViewSubscription(
                view.onDeviceClicked()
                        .subscribe(device -> {
                            Timber.d("Device '%s' selected", device.getName());
                            EventBus.getDefault().post(new TransferFragmentActiveEvent(device));
                        }));
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        bluetoothManager.cancelDiscovery();
    }

    boolean shouldEnableBluetooth() {
        return !bluetoothManager.isBluetoothEnabled();
    }

    void onBluetoothEnabled() {
        sendToView(DeviceView::showProgressBar);

        foundAddresses.clear();

        rxHelper.manageViewSubscription(
                bluetoothManager.getPairedDevices()
                        .compose(RxTiPresenterUtils.deliverToView(this))
                        .subscribe(devices -> {
                            Timber.d("Showing paired devices (size: %s)", devices.size());
                            //noinspection ConstantConditions
                            getView().addPairedDevices(devices);
                        }),
                bluetoothManager.observeDevices()
                        .compose(rxUtil.observableSchedulers())
                        .compose(RxTiPresenterUtils.deliverToView(this))
                        .subscribe(device -> {
                            Timber.d("Found device, name '%s', device class '%s'",
                                    device.getName(), device.getBluetoothClass().getDeviceClass());

                            if (foundAddresses.contains(device.getAddress())) {
                                Timber.v("Ignore duplicate");
                                return;
                            }

                            foundAddresses.add(device.getAddress());

                            if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE) {
                                //noinspection ConstantConditions
                                getView().onDeviceFound(device, true);
                            } else {
                                //noinspection ConstantConditions
                                getView().onDeviceFound(device, false);
                            }
                        }, throwable -> Timber.e(throwable, "Failed to observe found devices"))
        );

        boolean success = bluetoothManager.startDiscovery();

        if (!success) Timber.w("Failed to start Bluetooth discovery");
    }

    void onRequestCanceled() {
        Timber.e("User didn't enable Bluetooth");
        // TODO: Don't let user continue because he's an idiot
    }

    @Subscribe
    public void onDiscoveryFinished(DiscoveryFinishedEvent event) {
        Timber.d("Discovery finished");

        sendToView(DeviceView::onDiscoveryFinished);
    }
}
