package nawrot.mateusz.lausannefleet.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class BaseViewModel : ViewModel() {

//    abstract val defaultState: T
//
//    private val viewState: MutableLiveData<T> by lazy { MutableLiveData<T>() }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

//    fun viewState(): LiveData<T> = viewState
//
//    protected fun updateViewState(newViewState: T) {
//        viewState.value = newViewState
//    }
//
//    protected fun currentViewState(): T {
//        return viewState.value ?: defaultState
//    }

    protected fun manage(disposable: Disposable) {
        compositeDisposable.addAll(disposable)
    }

}