package xyz.santeri.pbap.ui.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;

import com.pascalwelsch.compositeandroid.activity.CompositeActivity;

import java.util.concurrent.atomic.AtomicLong;

import butterknife.ButterKnife;
import timber.log.Timber;
import xyz.santeri.pbap.App;
import xyz.santeri.pbap.injection.component.ActivityComponent;
import xyz.santeri.pbap.injection.component.ConfigPersistentComponent;
import xyz.santeri.pbap.injection.module.ActivityModule;

/**
 * Base activity implementation that all activities should extend from. We use CompositeAndroid
 * so that activities can use plugins for e.g. MVP libraries and the like.
 * <p>
 * ConfigPersistentComponent idea & implementation borrowed from Ribot's boilerplate app with
 * slight modifications to allow getting the component before onCreate is called.
 *
 * @author Santeri 'iffa'
 */
@SuppressLint("Registered")
public class BaseActivity extends CompositeActivity {
    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private static final LongSparseArray<ConfigPersistentComponent> componentMap = new LongSparseArray<>();
    private ActivityComponent activityComponent;
    private long activityId;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        ButterKnife.bind(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createComponent(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ACTIVITY_ID, activityId);
    }

    @Override
    public void onDestroy() {
        if (!isChangingConfigurations()) {
            Timber.v("Clearing ConfigPersistentComponent '%d'", activityId);
            componentMap.remove(activityId);
        }
        super.onDestroy();
    }

    public ActivityComponent getComponent() {
        if (activityComponent == null) createComponent(null);
        return activityComponent;
    }

    private void createComponent(@Nullable Bundle savedInstanceState) {
        activityId = savedInstanceState != null ?
                savedInstanceState.getLong(KEY_ACTIVITY_ID) : NEXT_ID.getAndIncrement();
        ConfigPersistentComponent configPersistentComponent;
        if (componentMap.get(activityId) == null) {
            Timber.d("Creating new ConfigPersistentComponent '%d'", activityId);
            configPersistentComponent = App.get(this).getComponent()
                    .configPersistentComponent();
            componentMap.put(activityId, configPersistentComponent);
        } else {
            Timber.d("Reusing ConfigPersistentComponent '%d'", activityId);
            configPersistentComponent = componentMap.get(activityId);
        }
        activityComponent = configPersistentComponent.activityComponent(new ActivityModule(this));
    }
}