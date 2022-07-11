package com.c2p.c2pbtphone.bt.injector.Module;

import android.app.NotificationManager;
import android.content.Context;
import android.view.LayoutInflater;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * @Module: The methods in the Modules class specifically provide dependencies, so we define a class, annotated with @Module,
 * This way, when Dagger constructs an instance of a class, it knows where to find the required dependencies.
 * An important feature of *modules is that they are designed to be partitioned and grouped together (for example, we can have multiple modules grouped together in our app).
 */
@Module
public class ApplicationModule {

    private final Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }


    /**
     * @Provide: In modules, the method we define is to use this annotation to tell Dagger that we want to construct the object and provide these dependencies.
     */
    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return context.getApplicationContext();
    }

    @Provides
    @Singleton
    LayoutInflater provideLayoutInflater(Context context) {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Provides
    @Singleton
    public EventBus provideBusEvent() {
        return new EventBus();
    }

    @Provides
    @Singleton
    public String providString() {
        return "xxx213";
    }

    @Provides
    @Singleton
    NotificationManager provideNotificationManager(Context mContext) {
        return (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
