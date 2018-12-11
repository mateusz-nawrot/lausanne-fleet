package nawrot.mateusz.lausannefleet.domain.base

import io.reactivex.CompletableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer


interface SchedulersProvider {

    fun <T> singleTransformer(): SingleTransformer<T, T>

    fun <T> observableTransformer(): ObservableTransformer<T, T>

    fun completableTransformer(): CompletableTransformer

}