package xyz.santeri.pbap.data;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.android.vcard.VCardEntry;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import timber.log.Timber;
import xyz.santeri.pbap.injection.ApplicationContext;

import static android.provider.ContactsContract.CommonDataKinds;
import static android.provider.ContactsContract.Data;
import static android.provider.ContactsContract.RawContacts;

/**
 * @author Santeri Elo
 */
@Singleton
public class ContactsManager {
    private final ContentResolver contentResolver;

    @Inject
    ContactsManager(@ApplicationContext Context context) {
        contentResolver = context.getContentResolver();
    }

    /**
     * Save contacts to device memory.
     *
     * @param contacts Contacts to save
     * @return Doesn't matter really
     */
    public Observable<Boolean> saveContacts(List<VCardEntry> contacts) {
        return Observable.defer(() -> {
            for (VCardEntry entry : contacts) {
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                int insertIndex = ops.size();

                ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                        .withValue(RawContacts.ACCOUNT_TYPE, null)
                        .withValue(RawContacts.ACCOUNT_NAME, null)
                        .build());

                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, insertIndex)
                        .withValue(Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, entry.getDisplayName())
                        .build());

                if (entry.getPhoneList() != null && !entry.getPhoneList().isEmpty()) {
                    for (VCardEntry.PhoneData phone : entry.getPhoneList()) {
                        ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValueBackReference(Data.RAW_CONTACT_ID, insertIndex)
                                .withValue(Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(CommonDataKinds.Phone.NUMBER, phone.getNumber())
                                .withValue(CommonDataKinds.Phone.TYPE, phone.getType())
                                .build());
                    }
                }

                if (entry.getEmailList() != null && !entry.getEmailList().isEmpty()) {
                    for (VCardEntry.EmailData email : entry.getEmailList()) {
                        ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValueBackReference(Data.RAW_CONTACT_ID, insertIndex)
                                .withValue(Data.MIMETYPE, CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                .withValue(CommonDataKinds.Email.ADDRESS, email.getAddress())
                                .build());
                    }
                }

                try {
                    Timber.v("Applying contact operation for '%s'", entry);
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException | OperationApplicationException e) {
                    return Observable.error(e);
                }
            }

            return Observable.just(true);
        });
    }
}
