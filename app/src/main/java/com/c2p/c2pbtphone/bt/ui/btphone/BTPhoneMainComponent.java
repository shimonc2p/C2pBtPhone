package com.c2p.c2pbtphone.bt.ui.btphone;

import com.c2p.c2pbtphone.bt.injector.Module.ActivityModule;
import com.c2p.c2pbtphone.bt.injector.PerActivity;
import com.c2p.c2pbtphone.bt.injector.component.ApplicationComponent;
import com.c2p.c2pbtphone.bt.ui.btphone.contact.ContactFragment;
import com.c2p.c2pbtphone.bt.ui.btphone.dail.DailFragment;
import com.c2p.c2pbtphone.bt.ui.btphone.device.DeviceFragment;

import dagger.Component;

/**
	* BTPhoneMainComponent
	*
	* @author heyu
	* @date 2017/6/15.
	*/
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface BTPhoneMainComponent{
				void inject(BTPhoneMainActivity activity);
				void inject(DailFragment fragment);
				void inject(ContactFragment fragment);
				void inject(DeviceFragment fragment);
}
