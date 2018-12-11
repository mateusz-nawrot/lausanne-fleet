package nawrot.mateusz.lausannefleet.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import nawrot.mateusz.lausannefleet.R
import nawrot.mateusz.lausannefleet.data.AndroidSchedulersProvider
import nawrot.mateusz.lausannefleet.data.ApiInterface
import nawrot.mateusz.lausannefleet.data.ApiKeyInterceptor
import nawrot.mateusz.lausannefleet.data.car.FleetCarRepository
import nawrot.mateusz.lausannefleet.data.station.FleetStationRepository
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.map.MapHelper
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import nawrot.mateusz.lausannefleet.presentation.FleetApp
import nawrot.mateusz.lausannefleet.presentation.GoogleMapHelper
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


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

    @Binds
    abstract fun bindCarRepository(carRepository: FleetCarRepository): CarRepository

    @Binds
    abstract fun bindMapHelper(mapHelper: GoogleMapHelper): MapHelper

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun okHttpClient(context: Context, apiKeyInterceptor: ApiKeyInterceptor): OkHttpClient {
            val cacheSize = 10 * 1024 * 1024
            val builder = OkHttpClient.Builder()
            builder.retryOnConnectionFailure(true)
            builder.cache(Cache(context.cacheDir, cacheSize.toLong()))

            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logInterceptor)
            builder.addInterceptor(apiKeyInterceptor)
            return builder.build()
        }

        @JvmStatic
        @Provides
        @Singleton
        fun retrofit(okHttpClient: OkHttpClient, @Named("base_url") baseUrl: String): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
        }

        @JvmStatic
        @Provides
        @Singleton
        fun apiInterface(retrofit: Retrofit): ApiInterface {
            return retrofit.create(ApiInterface::class.java)
        }

        @JvmStatic
        @Provides
        @Named("api_key")
        fun apiKey(context: Context): String {
            return context.getString(R.string.google_maps_key)
        }

        @JvmStatic
        @Provides
        @Named("base_url")
        fun baseUrl(context: Context): String {
            return context.getString(R.string.base_url)
        }

    }

}