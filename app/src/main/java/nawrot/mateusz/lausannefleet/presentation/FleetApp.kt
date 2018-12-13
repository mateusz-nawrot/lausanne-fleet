package nawrot.mateusz.lausannefleet.presentation

import android.app.Activity
import android.app.Application
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import nawrot.mateusz.lausannefleet.di.AppComponent
import nawrot.mateusz.lausannefleet.di.DaggerAppComponent
import javax.inject.Inject


class FleetApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()

        appComponent.inject(this)

        if (LeakCanary.isInAnalyzerProcess(this).not()) {
            LeakCanary.install(this)
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}