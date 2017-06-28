package xyz.santeri.pbap.injection.component;

import dagger.Subcomponent;
import xyz.santeri.pbap.injection.PerActivity;
import xyz.santeri.pbap.injection.module.ActivityModule;

/**
 * @author Santeri 'iffa'
 */
@PerActivity
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {
    /*
    SearchPresenter searchPresenter();

    DownloadPresenter downloadPresenter();
    */
}