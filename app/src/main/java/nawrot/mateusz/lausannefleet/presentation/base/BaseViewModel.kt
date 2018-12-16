package nawrot.mateusz.lausannefleet.presentation.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class BaseViewModel : ViewModel() {

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    protected fun manage(disposable: Disposable) {
        compositeDisposable.addAll(disposable)
    }

}