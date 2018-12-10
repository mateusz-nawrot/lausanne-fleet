package nawrot.mateusz.lausannefleet.data

import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import nawrot.mateusz.lausannefleet.domain.base.SchedulersProvider
import javax.inject.Inject


class AndroidSchedulersProvider @Inject constructor() : SchedulersProvider {

    override fun <T> singleTransformer(): SingleTransformer<T, T> {
        return SingleTransformer { it -> it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }

}