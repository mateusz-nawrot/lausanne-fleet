package nawrot.mateusz.lausannefleet.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton
import nawrot.mateusz.lausannefleet.presentation.FleetApp


@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityBuilderModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: FleetApp): Builder

        fun build(): AppComponent

    }

    fun inject(app: FleetApp)

}