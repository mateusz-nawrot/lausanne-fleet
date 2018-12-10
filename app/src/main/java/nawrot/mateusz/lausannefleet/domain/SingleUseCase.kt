package nawrot.mateusz.lausannefleet.domain

import io.reactivex.Single
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider


abstract class SingleUseCase<in P, R>(private val schedulersProvider: SchedulersProvider) {

    private lateinit var observable: Single<R>

    fun execute(param: P): Single<R> {
        observable = createUseCaseSingle(param).compose(schedulersProvider.singleTransformer())
        return observable
    }

    protected abstract fun createUseCaseSingle(param: P): Single<R>

}