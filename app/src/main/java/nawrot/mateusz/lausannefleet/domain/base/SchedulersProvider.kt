package nawrot.mateusz.lausannefleet.domain.base

import io.reactivex.SingleTransformer


interface SchedulersProvider {

    fun <T> singleTransformer(): SingleTransformer<T, T>

}