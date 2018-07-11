package shinei.com.dougaku.helper

import io.reactivex.CompletableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object RxSchedulersHelper {

    fun <T> singleIoToMain(): SingleTransformer<T, T> {
        return SingleTransformer {
            upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }

    fun completableIoToMain(): CompletableTransformer {
        return CompletableTransformer {
            upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }
}


