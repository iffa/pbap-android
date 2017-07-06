package xyz.santeri.pbap.ui.transfer;

import android.os.Message;

/**
 * @author Santeri 'iffa'
 */
class PbapEvent {
    private final Message msg;

    public PbapEvent(Message msg) {
        this.msg = msg;
    }

    public Message getMessage() {
        return msg;
    }
}
