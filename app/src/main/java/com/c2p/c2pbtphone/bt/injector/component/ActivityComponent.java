package com.c2p.c2pbtphone.bt.injector.component;

import android.app.Activity;


import com.c2p.c2pbtphone.bt.injector.Module.ActivityModule;
import com.c2p.c2pbtphone.bt.injector.PerActivity;

import dagger.Component;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity getActivity();
}
