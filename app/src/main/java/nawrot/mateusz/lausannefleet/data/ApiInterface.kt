package nawrot.mateusz.lausannefleet.data

import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.directions.DirectionsResponse


interface ApiInterface {

    fun getDirections(): Single<DirectionsResponse>
}