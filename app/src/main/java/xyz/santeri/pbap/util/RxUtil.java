package xyz.santeri.pbap.util;


import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;
import rx.Scheduler;
import rx.Single;

/**
 * @author Santeri 'iffa'
 */
@Singleton
public class RxUtil {
    private final Observable.Transformer observableTransformer;
    private final Single.Transformer singleTransformer;

    @Inject
    RxUtil(@Named("subscribing") Scheduler subscribingScheduler,
           @Named("observing") Scheduler observingScheduler) {
        //noinspection RedundantCast
        observableTransformer = observable -> ((Observable) observable)
                .subscribeOn(subscribingScheduler)
                .observeOn(observingScheduler);

        //noinspection RedundantCast
        singleTransformer = single -> ((Single) single)
                .subscribeOn(subscribingScheduler)
                .observeOn(observingScheduler);
    }

    @SuppressWarnings("unchecked")
    public <T> Observable.Transformer<T, T> observableSchedulers() {
        return (Observable.Transformer<T, T>) observableTransformer;
    }

    @SuppressWarnings("unchecked")
    public <T> Single.Transformer<T, T> singleSchedulers() {
        return (Single.Transformer<T, T>) singleTransformer;
    }
}