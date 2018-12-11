package nawrot.mateusz.lausannefleet.data

import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.directions.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiInterface {

    @GET("/maps/api/directions/json?origin={origin}&destination={destination}")
    fun getDirections(@Path("origin") origin: LatLng,
                      @Path("destination") destination: LatLng): Single<DirectionsResponse>

}