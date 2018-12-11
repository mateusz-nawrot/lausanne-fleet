package nawrot.mateusz.lausannefleet.di

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import nawrot.mateusz.lausannefleet.data.AndroidSchedulersProvider
import nawrot.mateusz.lausannefleet.data.ApiInterface
import nawrot.mateusz.lausannefleet.data.car.FleetCarRepository
import nawrot.mateusz.lausannefleet.data.station.FleetStationRepository
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import nawrot.mateusz.lausannefleet.domain.car.CarRepository
import nawrot.mateusz.lausannefleet.domain.station.StationRepository
import nawrot.mateusz.lausannefleet.presentation.FleetApp
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun okHttpClient(context: Context): OkHttpClient {
            val cacheSize = 10 * 1024 * 1024
            val builder = OkHttpClient.Builder()
            builder.retryOnConnectionFailure(true)
            builder.cache(Cache(context.cacheDir, cacheSize.toLong()))

            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logInterceptor)
            return builder.build()
        }

        @JvmStatic
        @Provides
        @Singleton
        fun gson(): Gson {
            return GsonBuilder().create()
        }

        @JvmStatic
        @Provides
        @Singleton
        fun retrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
            return Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create(gson))
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

    }

}