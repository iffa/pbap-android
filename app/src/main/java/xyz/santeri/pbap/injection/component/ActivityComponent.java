package xyz.santeri.pbap.injection.component;

import dagger.Subcomponent;
import xyz.santeri.pbap.injection.PerActivity;
import xyz.santeri.pbap.injection.module.ActivityModule;
import xyz.santeri.pbap.ui.device.DevicePresenter;
import xyz.santeri.pbap.ui.main.MainPresenter;
import xyz.santeri.pbap.ui.transfer.TransferPresenter;

/**
 * @author Santeri 'iffa'
 */
@PerActivity
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {
    MainPresenter mainPresenter();

    DevicePresenter devicePresenter();

    TransferPresenter transferPresenter();
}