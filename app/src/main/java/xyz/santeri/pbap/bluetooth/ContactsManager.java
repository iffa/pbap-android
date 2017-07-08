package xyz.santeri.pbap.bluetooth;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import xyz.santeri.pbap.injection.ApplicationContext;

/**
 * @author Santeri Elo
 */
@Singleton
public class ContactsManager {

    @Inject
    ContactsManager(@ApplicationContext Context context) {}
}
