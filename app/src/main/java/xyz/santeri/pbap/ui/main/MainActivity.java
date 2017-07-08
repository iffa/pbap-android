package xyz.santeri.pbap.ui.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.badoualy.stepperindicator.StepperIndicator;

import net.grandcentrix.thirtyinch.plugin.TiActivityPlugin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import timber.log.Timber;
import xyz.santeri.pbap.R;
import xyz.santeri.pbap.ui.base.BaseActivity;
import xyz.santeri.pbap.ui.device.DeviceFragment;
import xyz.santeri.pbap.ui.device.DeviceFragmentActiveEvent;
import xyz.santeri.pbap.ui.finish.FinishFragment;
import xyz.santeri.pbap.ui.start.ContinueClickEvent;
import xyz.santeri.pbap.ui.start.StartFragment;
import xyz.santeri.pbap.ui.transfer.TransferFragment;
import xyz.santeri.pbap.ui.transfer.TransferFragmentActiveEvent;
import xyz.santeri.pbap.ui.view.NonSwipeableViewPager;

public class MainActivity extends BaseActivity implements MainView {
    private final TiActivityPlugin<MainPresenter, MainView> presenterPlugin =
            new TiActivityPlugin<>(() -> getComponent().mainPresenter());

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.container)
    ViewGroup container;

    @BindView(R.id.pager)
    NonSwipeableViewPager viewPager;

    @BindView(R.id.steps)
    StepperIndicator stepperIndicator;

    public MainActivity() {
        addPlugin(presenterPlugin);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        stepperIndicator.setViewPager(viewPager, pagerAdapter.getCount() - 1);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onContinueClick(ContinueClickEvent event) {
        Timber.v("Continue clicked", viewPager.getCurrentItem());

        //noinspection ConstantConditions
        getSupportActionBar().setTitle(R.string.tb_device);
        viewPager.setCurrentItem(1, true);

        EventBus.getDefault().post(new DeviceFragmentActiveEvent());

        /*
        switch (viewPager.getCurrentItem()) {
            case 0:
                break;
            case 1:
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(R.string.tb_transfer);
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                break;
            case 2:
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(R.string.tb_done);
                continueButton.setText(R.string.bt_quit);
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                break;
            case 3:
                Timber.v("User called quits, finishing");
                finishAffinity();
                break;
        }
        */
    }

    @Subscribe
    public void onChooseDevice(TransferFragmentActiveEvent event) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(R.string.app_name);
        viewPager.setCurrentItem(2, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {
        private static final int PAGE_COUNT = 4;

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return StartFragment.newInstance();
                case 1:
                    return DeviceFragment.newInstance();
                case 2:
                    return TransferFragment.newInstance();
                case 3:
                    return FinishFragment.newInstance();
                default:
                    throw new UnsupportedOperationException("Looking for fragment that shouldn't exist");
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}