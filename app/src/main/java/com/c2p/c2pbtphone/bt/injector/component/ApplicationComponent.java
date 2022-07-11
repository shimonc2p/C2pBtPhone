package com.c2p.c2pbtphone.bt.injector.component;

import android.content.Context;

import com.c2p.c2pbtphone.bt.BaseApplication;
import com.c2p.c2pbtphone.bt.injector.Module.ApplicationModule;
import com.c2p.c2pbtphone.bt.ui.BaseActivity;
import com.c2p.c2pbtphone.bt.ui.BaseSupportActivity;


import javax.inject.Singleton;

import dagger.Component;
import de.greenrobot.event.EventBus;

/**
  *  @Component: Components is fundamentally an injector, or a bridge between @Inject and @Module,
  * Its main function is to connect these two parts. Components can provide instances of all defined types,
  * For example: we have to annotate an interface with @Component and list all @Modules that make up the component,
  * If any block is missing, an error will be reported at compile time. All components can know the scope of dependencies through their modules.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Context getContext();

    EventBus getBus();

    String getString();

    void inject(BaseApplication mApplication);

    void inject(BaseActivity activity);

    void inject(BaseSupportActivity activity);
}
