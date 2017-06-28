package xyz.santeri.pbap.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xyz.santeri.pbap.injection.ApplicationContext;

/**
 * @author Santeri 'iffa'
 */
@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @Provides
    @ApplicationContext
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Named("observing")
    Scheduler provideObservingScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Named("subscribing")
    Scheduler provideSubscribingScheduler() {
        return Schedulers.io();
    }
}