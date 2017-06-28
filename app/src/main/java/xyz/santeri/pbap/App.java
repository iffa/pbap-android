package xyz.santeri.pbap;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;
import xyz.santeri.pbap.injection.component.ApplicationComponent;
import xyz.santeri.pbap.injection.component.DaggerApplicationComponent;
import xyz.santeri.pbap.injection.module.ApplicationModule;

/**
 * @author Santeri 'iffa'
 */
public class App extends Application {
    private ApplicationComponent component;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        refWatcher = LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }

        getComponent().inject(this);

        /*
        if (preferencesManager.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        */
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        if (component == null) {
            component = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return component;
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }

}
