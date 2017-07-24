package xyz.santeri.pbap.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.vcard.VCardEntry;

import java.util.ArrayList;
import java.util.Arrays;
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
 * TODO: Don't create duplicate contacts
 *
 * @author Santeri Elo
 */
@Singleton
public class ContactsManager {
    private static final String ACCOUNT_GOOGLE = "com.google";
    private final ContentResolver contentResolver;
    private final AccountManager accountManager;

    @Inject
    ContactsManager(@ApplicationContext Context context) {
        contentResolver = context.getContentResolver();
        accountManager = AccountManager.get(context);
    }

    /**
     * @return Returns true if device has at least one Google account
     */
    public boolean hasGoogleAccount() {
        return !getAccountsByType(ACCOUNT_GOOGLE).isEmpty();
    }

    /**
     * @return Returns a list of all available accounts (Google)
     */
    public List<Account> getAvailableAccounts() {
        return getAccountsByType(ACCOUNT_GOOGLE);
    }

    /**
     * Save contacts to device memory.
     *
     * @param contacts Contacts to save
     * @return Doesn't matter
     */
    public Observable<Boolean> saveContactsToDevice(@NonNull List<VCardEntry> contacts) {
        return saveContacts(contacts, null, null);
    }

    /**
     * Save contacts to a specified Google account.
     *
     * @param contacts    Contacts to save
     * @param accountName Google account name, e.g. jorma@gmail.com
     * @return Doesn't matter
     */
    public Observable<Boolean> saveContactsToGoogle(@NonNull List<VCardEntry> contacts,
                                                    @NonNull String accountName) {
        return saveContacts(contacts, ACCOUNT_GOOGLE, accountName);
    }

    private Observable<Boolean> saveContacts(@NonNull List<VCardEntry> contacts,
                                             @Nullable String accountType,
                                             @Nullable String accountName) {
        return Observable.defer(() -> {
            for (VCardEntry entry : contacts) {
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                int insertIndex = ops.size();

                ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                        .withValue(RawContacts.ACCOUNT_TYPE, accountType)
                        .withValue(RawContacts.ACCOUNT_NAME, accountName)
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

    @NonNull
    private List<Account> getAccountsByType(String type) {
        Account[] accounts = accountManager.getAccountsByType(type);
        return Arrays.asList(accounts);
    }
}
