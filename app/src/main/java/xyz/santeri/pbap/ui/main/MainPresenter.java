package xyz.santeri.pbap.ui.main;

import android.support.annotation.NonNull;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import javax.inject.Inject;

import xyz.santeri.pbap.util.RxUtil;

/**
 * @author Santeri 'iffa'
 */
public class MainPresenter extends TiPresenter<MainView> {
    private final RxTiPresenterSubscriptionHandler rxHelper = new RxTiPresenterSubscriptionHandler(this);
    private final RxUtil rxUtil;

    @Inject
    MainPresenter(RxUtil rxUtil) {
        this.rxUtil = rxUtil;
    }

    @Override
    protected void onAttachView(@NonNull MainView view) {
        super.onAttachView(view);
    }

    @Override
    protected void onDetachView() {
        super.onDetachView();
    }
}
