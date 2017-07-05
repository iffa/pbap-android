package xyz.santeri.pbap.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;
import xyz.santeri.pbap.injection.ApplicationContext;

/**
 * All things Bluetooth happen here. Proceed with caution!
 *
 * @author Santeri 'iffa'
 * @author Ivan Baranov
 */
@Singleton
public class BluetoothManager {
    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;
    private final PbapEventHandler eventHandler = new PbapEventHandler();
    private BluetoothPbapClient pbapClient;

    @Inject
    BluetoothManager(@ApplicationContext Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) { // This should never happen, but you never know
            throw new UnsupportedOperationException("This device does not support Bluetooth");
        }
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public void startPbapConnection(String address) {
        startPbapConnection(address, eventHandler);
    }

    public void startPbapConnection(String address, Handler eventHandler) {
        Timber.d("Starting PBAP connection to address '%s'", address);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        stopPbapConnection();

        pbapClient = new BluetoothPbapClient(device, eventHandler);
        pbapClient.connect();
    }

    public void stopPbapConnection() {
        if (pbapClient != null
                && (pbapClient.getState() == BluetoothPbapClient.ConnectionState.CONNECTED
                || pbapClient.getState() == BluetoothPbapClient.ConnectionState.CONNECTING)) {
            Timber.d("Stopping PBAP client connection");
            pbapClient.disconnect();
        } else {
            Timber.d("PBAP not connected, doing nothing");
        }
    }

    /**
     * Pull the phone book from the Bluetooth device.
     * NOTE: Do NOT call this method unless you are absolutely certain you have established a PBAP
     * connection with the device.
     *
     * @see BluetoothPbapClient.ConnectionState#CONNECTED
     */
    public void pullPhoneBook() {
        if (pbapClient != null && pbapClient.getState() == BluetoothPbapClient.ConnectionState.CONNECTED) {
            Timber.d("Pulling phone book, this can take a while");
            pbapClient.pullPhoneBook(BluetoothPbapClient.PB_PATH);
        } else {
            Timber.e("PBAP connection not established, can't pull phone book");
        }
    }

    /**
     * Get a list of already paired Bluetooth devices.
     *
     * @return List of paired devices
     */
    public Observable<List<BluetoothDevice>> getPairedDevices() {
        return Observable.defer(() -> {
            List<BluetoothDevice> devices = new ArrayList<>();
            devices.addAll(bluetoothAdapter.getBondedDevices());
            return Observable.just(devices);
        });
    }

    public boolean startDiscovery() {
        return bluetoothAdapter.startDiscovery();
    }

    public boolean cancelDiscovery() {
        return bluetoothAdapter.cancelDiscovery();
    }

    /**
     * Observe for available devices.
     *
     * @return Observable stream of found devices
     */
    public Observable<BluetoothDevice> observeDevices() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        // TODO: Fix usage of Observable.create
        return Observable.defer(() -> Observable.create(subscriber -> {
            final BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        subscriber.onNext(device);
                    } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                        EventBus.getDefault().post(new DiscoveryFinishedEvent()); // TODO: Less hacky solution!
                    }
                }
            };

            context.registerReceiver(receiver, filter);

            subscriber.add(unsubscribeInUiThread(() -> context.unregisterReceiver(receiver)));
        }));
    }

    static class PbapEventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Timber.v("Handling '%s'", msg);

            switch (msg.what) {
                case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_DONE:
                    Timber.i("BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_DONE");
                    break;
                case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_ERROR:
                    Timber.e("BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_ERROR");
                    break;
                case BluetoothPbapClient.EVENT_SESSION_CONNECTED:
                    Timber.i("BluetoothPbapClient.EVENT_SESSION_CONNECTED");
                    break;
                case BluetoothPbapClient.EVENT_SESSION_DISCONNECTED:
                    Timber.i("BluetoothPbapClient.EVENT_SESSION_DISCONNECTED");
                    break;
                default:
                    Timber.w("Unexpected event '%s'", msg.what);
                    break;
            }
        }
    }

    private Subscription unsubscribeInUiThread(final Action0 unsubscribe) {
        return Subscriptions.create(() -> {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                unsubscribe.call();
            } else {
                final Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
                inner.schedule(() -> {
                    unsubscribe.call();
                    inner.unsubscribe();
                });
            }
        });
    }
}
