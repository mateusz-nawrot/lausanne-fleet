package nawrot.mateusz.lausannefleet.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import nawrot.mateusz.lausannefleet.presentation.map.MapActivity
import nawrot.mateusz.lausannefleet.presentation.map.MapActivityModule


@Module
abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MapActivityModule::class])
    abstract fun mapActivity(): MapActivity

}
