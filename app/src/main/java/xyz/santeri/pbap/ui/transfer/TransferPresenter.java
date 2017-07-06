package xyz.santeri.pbap.ui.transfer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.os.Handler;
import android.os.Message;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import timber.log.Timber;
import xyz.santeri.pbap.bluetooth.BluetoothManager;
import xyz.santeri.pbap.util.RxUtil;

/**
 * @author Santeri 'iffa'
 */
public class TransferPresenter extends TiPresenter<TransferView> {
    private final RxTiPresenterSubscriptionHandler rxHelper = new RxTiPresenterSubscriptionHandler(this);
    private final PbapEventHandler eventHandler = new PbapEventHandler();
    private final RxUtil rxUtil;
    private final BluetoothManager bluetoothManager;

    @Inject
    TransferPresenter(RxUtil rxUtil, BluetoothManager bluetoothManager) {
        this.rxUtil = rxUtil;
        this.bluetoothManager = bluetoothManager;
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
    }

    void onActiveEvent(BluetoothDevice device) {
        Timber.d("TransferFragment is active, connecting");
        bluetoothManager.startPbapConnection(device.getAddress(), eventHandler);
    }

    @Subscribe
    public void onPbapEvent(PbapEvent event) {
        Message msg = event.getMessage();

        switch (msg.what) {
            case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_DONE:
                Timber.i("EVENT_PULL_PHONE_BOOK_DONE");
                bluetoothManager.stopPbapConnection();
                sendToView(TransferView::showTransferFinished);
                break;
            case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_ERROR:
                Timber.e("EVENT_PULL_PHONE_BOOK_ERROR");
                sendToView(TransferView::showTransferFailed);
                break;
            case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_SIZE_DONE:
                Timber.i("EVENT_PULL_PHONE_BOOK_SIZE_DONE");
                bluetoothManager.pullPhoneBook();
                sendToView(TransferView::showTransferStarted);
                break;
            case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_SIZE_ERROR:
                Timber.e("EVENT_PULL_PHONE_BOOK_SIZE_ERROR");
                sendToView(TransferView::showTransferFailed);
                break;
            case BluetoothPbapClient.EVENT_SESSION_CONNECTED:
                Timber.i("EVENT_SESSION_CONNECTED");
                bluetoothManager.pullPhoneBookSize();
                break;
            case BluetoothPbapClient.EVENT_SESSION_DISCONNECTED:
                Timber.w("EVENT_SESSION_DISCONNECTED");
                break;
            case BluetoothPbapClient.EVENT_SESSION_AUTH_TIMEOUT:
                Timber.w("EVENT_SESSION_AUTH_TIMEOUT");
                sendToView(TransferView::showConnectionFailed);
                break;
            default:
                Timber.w("Unexpected event '%s'", msg.what);
                break;
        }
    }

    static class PbapEventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Timber.v("Handling message '%s'", msg);

            EventBus.getDefault().post(new PbapEvent(msg));
        }
    }
}