package nawrot.mateusz.lausannefleet.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import nawrot.mateusz.lausannefleet.presentation.map.MapViewModel


@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(westwingViewModelFactory: FleetViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    abstract fun bindMapViewModel(mapViewModel: MapViewModel): ViewModel

}