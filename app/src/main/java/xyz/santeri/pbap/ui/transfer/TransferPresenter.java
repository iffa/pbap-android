package xyz.santeri.pbap.ui.transfer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.android.vcard.VCardEntry;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;
import xyz.santeri.pbap.bluetooth.BluetoothManager;
import xyz.santeri.pbap.data.ContactsManager;
import xyz.santeri.pbap.util.RxUtil;

/**
 * @author Santeri 'iffa'
 */
public class TransferPresenter extends TiPresenter<TransferView> {
    private final RxTiPresenterSubscriptionHandler rxHelper = new RxTiPresenterSubscriptionHandler(this);
    private final PbapEventHandler eventHandler = new PbapEventHandler();
    private final RxUtil rxUtil;
    private final BluetoothManager bluetoothManager;
    private final ContactsManager contactsManager;
    private boolean ignoreNoPhoneNumber = true;
    private List<VCardEntry> contacts = new ArrayList<>();

    @Inject
    TransferPresenter(RxUtil rxUtil, BluetoothManager bluetoothManager, ContactsManager contactsManager) {
        this.rxUtil = rxUtil;
        this.bluetoothManager = bluetoothManager;
        this.contactsManager = contactsManager;
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

        bluetoothManager.stopPbapConnection();
    }

    @Override
    protected void onAttachView(@NonNull TransferView view) {
        super.onAttachView(view);

        rxHelper.manageViewSubscription(view.onSaveClick()
                .flatMap(aVoid -> {
                    EventBus.getDefault().post(new TransferCompletedEvent());

                    Timber.d("Saving contacts!");

                    return contactsManager.saveContactsToDevice(contacts);
                })
                .subscribe(aBoolean -> Timber.d("Contacts saved? I guess"),
                        throwable -> Timber.e(throwable, "Failed to save contacts")));
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

                // If this does not return a list of vCard entries, I'm not sure what to say anymore
                //noinspection unchecked
                onContactsTransferred((List<VCardEntry>) msg.obj);
                break;
            case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_ERROR:
                Timber.e("EVENT_PULL_PHONE_BOOK_ERROR");
                sendToView(TransferView::showTransferFailed);
                break;
            case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_SIZE_DONE:
                Timber.d("Size of phone book from remote: %s", msg.arg1);
                sendToView(view -> view.showTransferStarted(msg.arg1));
                bluetoothManager.pullPhoneBook();
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

    private void onContactsTransferred(List<VCardEntry> contacts) {
        bluetoothManager.stopPbapConnection();

        Observable.defer(() -> {
            if (ignoreNoPhoneNumber) {
                Timber.d("Filtering out entries with no phone number");
                return Observable.from(contacts)
                        .filter(item
                                -> (item.getPhoneList() != null && !item.getPhoneList().isEmpty()))
                        .toList();
            } else {
                Timber.d("Not filtering entries");
                return Observable.just(contacts);
            }
        }).compose(rxUtil.observableSchedulers()).subscribe(sorted ->
                sendToView(view -> {
                    TransferPresenter.this.contacts = sorted;
                    view.showTransferFinished();
                    view.onContactsTransferred(sorted);
                }), Timber::e);
    }

    static class PbapEventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Timber.v("Handling message '%s'", msg);

            EventBus.getDefault().post(new PbapEvent(msg));
        }
    }
}