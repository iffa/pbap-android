package xyz.santeri.pbap.injection.component;

import dagger.Subcomponent;
import xyz.santeri.pbap.injection.ConfigPersistent;
import xyz.santeri.pbap.injection.module.ActivityModule;

/**
 * @author Santeri 'iffa'
 */
@ConfigPersistent
@Subcomponent
public interface ConfigPersistentComponent {
    ActivityComponent activityComponent(ActivityModule activityModule);
}