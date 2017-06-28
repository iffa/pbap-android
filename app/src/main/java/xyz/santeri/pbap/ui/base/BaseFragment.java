package xyz.santeri.pbap.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.pascalwelsch.compositeandroid.fragment.CompositeFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import xyz.santeri.pbap.App;
import xyz.santeri.pbap.injection.component.ActivityComponent;

/**
 * Base fragment implementation that all fragments should extend from. This used to be an abstract
 * class due to limitations with ThirtyInch, but with the latest release candidate we can now
 * use CompositeAndroid instead.
 *
 * @author Santeri 'iffa'
 */
public class BaseFragment extends CompositeFragment {
    private Unbinder unbinder;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        App.get(getContext()).getRefWatcher().watch(this);
    }

    public boolean onBackPressed() {
        return true;
    }

    /**
     * Get the activity component from the parent activity.
     *
     * @return Activity component
     */
    public ActivityComponent getComponent() {
        // Ensure that the parent activity extends from BaseActivity
        if (!(getActivity() instanceof BaseActivity)) {
            throw new UnsupportedOperationException("Parent activity for fragment should extend from BaseActivity");
        }
        return ((BaseActivity) getActivity()).getComponent();
    }
}