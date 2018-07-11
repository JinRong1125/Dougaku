package shinei.com.dougaku.helper

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type

class RxThreadCallAdapterFactory : CallAdapter.Factory() {

    companion object {
        fun create(): CallAdapter.Factory = RxThreadCallAdapterFactory()
    }

    // 常に新しいスレッドで実行する
    private val original: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread())

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        // `original.get() == null` -> Retrofit throws IllegalArgumentException.
        return RxCallAdapterWrapper(original.get(returnType, annotations, retrofit)!!)
    }

    private class RxCallAdapterWrapper<R>(private val wrapped: CallAdapter<R, *>) : CallAdapter<R, Any> {
        override fun responseType(): Type = wrapped.responseType()

        // 常にメインスレッドに通知する
        override fun adapt(call: Call<R>) = wrapped.adapt(call)
                .let {
                    when (it) {
                        is Completable -> {
                            it.observeOn(AndroidSchedulers.mainThread())
                        }
                        is Observable<*> -> {
                            it.observeOn(AndroidSchedulers.mainThread())
                        }
                        is Single<*> -> {
                            it.observeOn(AndroidSchedulers.mainThread())
                        }
                        is Flowable<*> -> {
                            it.observeOn(AndroidSchedulers.mainThread())
                        }
                        is Maybe<*> -> {
                            it.observeOn(AndroidSchedulers.mainThread())
                        }
                        else -> {}
                    }
                }!!
    }
}