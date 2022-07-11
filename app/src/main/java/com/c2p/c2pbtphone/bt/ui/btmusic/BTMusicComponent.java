package com.c2p.c2pbtphone.bt.ui.btmusic;


import com.c2p.c2pbtphone.bt.injector.Module.ActivityModule;
import com.c2p.c2pbtphone.bt.injector.PerActivity;
import com.c2p.c2pbtphone.bt.injector.component.ApplicationComponent;

import dagger.Component;

/**
 * Created by heyu on 2016/7/26.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface BTMusicComponent{
    void inject(BTMusicActivity activity);
}
