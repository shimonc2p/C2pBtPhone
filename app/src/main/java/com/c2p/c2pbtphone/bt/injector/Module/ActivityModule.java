package com.c2p.c2pbtphone.bt.injector.Module;

import android.app.Activity;

import com.c2p.c2pbtphone.bt.injector.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sll on 2016/1/6.
 */
@Module
public class ActivityModule {

    private final Activity mActivity;

    public ActivityModule(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * @Provide: In modules, the method we define is to use this annotation to tell Dagger that we want to construct the object and provide these dependencies.
     * */
    @Provides
    @PerActivity
    public Activity provideActivity() {
        return mActivity;
    }
}
