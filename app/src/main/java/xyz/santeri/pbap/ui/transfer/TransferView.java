package xyz.santeri.pbap.ui.transfer;

import com.android.vcard.VCardEntry;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import java.util.List;

import rx.Observable;

/**
 * @author Santeri 'iffa'
 */
interface TransferView extends TiView {
    @CallOnMainThread
    void showConnectionStarted();

    @CallOnMainThread
    void showConnectionFailed();

    @CallOnMainThread
    void showTransferFinished();

    @CallOnMainThread
    void showTransferFailed();

    @CallOnMainThread
    void onContactsTransferred(List<VCardEntry> contacts);

    @CallOnMainThread
    void showTransferStarted(int contactsSize);

    Observable<VCardEntry> onContactClick();
}
