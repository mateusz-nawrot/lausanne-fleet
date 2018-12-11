package nawrot.mateusz.lausannefleet.data

import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.directions.DirectionsResponse
import nawrot.mateusz.lausannefleet.domain.map.Position
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {

    @GET("/maps/api/directions/json")
    fun getDirections(@Query("origin") origin: Position,
                      @Query("destination") destination: Position): Single<DirectionsResponse>

}