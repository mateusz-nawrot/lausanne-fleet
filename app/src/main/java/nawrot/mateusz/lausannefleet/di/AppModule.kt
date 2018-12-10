package nawrot.mateusz.lausannefleet.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import nawrot.mateusz.lausannefleet.data.AndroidSchedulersProvider
import nawrot.mateusz.lausannefleet.data.FleetStationRepository
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import nawrot.mateusz.lausannefleet.presentation.FleetApp


@Module(includes = [(ViewModelModule::class)])
abstract class AppModule {

    @Binds
    abstract fun bindApplication(app: FleetApp): Application

    @Binds
    abstract fun bindContext(app: FleetApp): Context

    @Binds
    abstract fun bindSchedulersProvider(androidSchedulersProvider: AndroidSchedulersProvider): SchedulersProvider

    @Binds
    abstract fun bindStationRepository(stationRepository: FleetStationRepository): StationRepository

    @Module
    companion object {

        //@Provides methods here

    }

}