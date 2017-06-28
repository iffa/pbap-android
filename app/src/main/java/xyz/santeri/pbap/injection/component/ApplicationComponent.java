package xyz.santeri.pbap.injection.component;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import xyz.santeri.pbap.injection.module.ApplicationModule;

/**
 * @author Santeri 'iffa'
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    ConfigPersistentComponent configPersistentComponent();

    void inject(Application application);
}