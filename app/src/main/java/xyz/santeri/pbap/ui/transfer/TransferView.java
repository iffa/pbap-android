package xyz.santeri.pbap.ui.transfer;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

/**
 * @author Santeri 'iffa'
 */
interface TransferView extends TiView {
    @CallOnMainThread
    void showConnectionFailed();

    @CallOnMainThread
    void showTransferStarted();

    @CallOnMainThread
    void showTransferFinished();

    @CallOnMainThread
    void showTransferFailed();
}
