package nawrot.mateusz.lausannefleet.domain.base

import io.reactivex.Single


abstract class SingleUnitUseCase<R>(private val schedulersProvider: SchedulersProvider) {

    private lateinit var observable: Single<R>

    fun execute(): Single<R> {
        observable = createUseCaseSingle().compose(schedulersProvider.singleTransformer())
        return observable
    }

    protected abstract fun createUseCaseSingle(): Single<R>

}